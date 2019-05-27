/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization.connector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.feature.visualization.AsyncLineReader;
import org.roboticsapi.feature.visualization.LookupClient;
import org.roboticsapi.feature.visualization.RapiInfo;

/**
 * The task of a {@link LookupServer} is to provide information about all
 * running services on this host. This might be a port for connecting a
 * visualization over tcp o a port for connecting a visualization over rmi.
 * 
 * There is never more than one {@link LookupServer} running on one host at a
 * time. This one {@link LookupServer} informs about all services of all running
 * and available Robotics API instances, even if they're running in different
 * JVMs.
 * 
 * A {@link LookupServer} is an asynchronously running service. A
 * {@link LookupClient} can be started to connect to a {@link LookupServer} on a
 * specific host.
 */
public class LookupServer {

	public static void main(String[] args) {
		String type = Math.random() > 0.5 ? "rmi" : "tcp";
		int port = (int) (Math.random() * 999);
		LookupServer lookupServer = new LookupServer(4320);
		lookupServer.registerServer(new VisualizationServer() {
			@Override
			public void shutdown() {
			}

			@Override
			public String getType() {
				return type;
			}

			@Override
			public String getRapiInstanceName() {
				return "Test Application";
			}

			@Override
			public int getPort() {
				return port;
			}
		});
		while (true) {
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
			}
		}
//			lookupServer.close();
	}

	private static final int TIMEOUT_MILLIS = 100;
	private static final long MAX_CONFIG_AGE_WHEN_SERVERSWITCH = 500;
	private final Thread serverThread;
	private final Set<VisualizationServer> servers = new HashSet<>();
	private final Object serversMonitor = new Object();
	private boolean alive = true;

	public LookupServer(int lookupPort) {
		serverThread = new Thread(() -> run(lookupPort));
		serverThread.setDaemon(true);
		serverThread.setName("Lookup server");
		serverThread.start();
	}

	public void registerServer(VisualizationServer server) {
		synchronized (serversMonitor) {
			servers.add(server);
			serversMonitor.notifyAll();
		}
	}

	public void unregisterServer(VisualizationServer server) {
		synchronized (serversMonitor) {
			servers.remove(server);
			serversMonitor.notifyAll();
		}
	}

	private void run(int lookupPort) {
		RapiInfo[] lastConfigurationFromOtherServer = null;
		while (alive) {
			// Kann ich eigenen Server aufbauen, oder gibt es lokal schon einen
			// anderen Lookup-Server?
			try {
				ConfigCache lastConfigurationFromOvertakenServer = new ConfigCache(lastConfigurationFromOtherServer,
						MAX_CONFIG_AGE_WHEN_SERVERSWITCH);
				startAndManageServer(lookupPort, lastConfigurationFromOvertakenServer);
			} catch (Exception e) {
				RapiInfo[] lastInfo = bindToOtherServer(lookupPort);
				if (lastInfo != null) {
					lastConfigurationFromOtherServer = lastInfo;
				}
			}
		}
	}

	private void startAndManageServer(int port, final ConfigCache lastConfigurationFromOvertakenServer)
			throws IOException {
		ServerSocket socket = new ServerSocket(port);

		// Now, we are THE ONE lookup server! Accept clients to connect...
		RAPILogger.getLogger().fine("Started local lookup server on port " + port + ".");

		final Map<Socket, Thread> clientThreads = new ConcurrentHashMap<>();
		try {
			while (alive) {
				try {
					socket.setSoTimeout(TIMEOUT_MILLIS);
					Socket connection = socket.accept();
					RAPILogger.getLogger().fine("Incoming connection established from "
							+ connection.getRemoteSocketAddress().toString() + ".");
					Thread t = new Thread(
							() -> handleClientConnectionAsync(connection, lastConfigurationFromOvertakenServer));
					t.setDaemon(true);
					t.setName("Visualization client handler");
					clientThreads.put(connection, t);
					t.start();
				} catch (SocketTimeoutException e) {
				} catch (IOException e) {
					RAPILogger.getLogger().warning(getClass() + ": " + e);
				}
			}
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
			}

			// Shutdown all client connections!
			clientThreads.forEach((c, t) -> {
				safeexec(() -> c.close());
				t.interrupt();
				while (true) {
					try {
						t.join();
						break;
					} catch (InterruptedException e) {
					}
				}
			});
			RAPILogger.getLogger().fine("Closed local lookup server on port " + port + ".");
		}
	}

	private final Object configurationsFromOtherServersMonitor = new HashMap<>();
	private final Map<Socket, RapiInfo[]> configurationsFromOtherServers = new HashMap<>();

	private void handleClientConnectionAsync(final Socket connection,
			final ConfigCache lastConfigurationFromOvertakenServer) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			AsyncLineReader in = new AsyncLineReader(connection.getInputStream());
			RapiInfo[] lastSent = new RapiInfo[0];

			while (alive) {
				// Anderem Server die eigene Konfiguration + gecachtes von altem server schicken
				RapiInfo[] _cache = getGlobalConfigurationIfModified(lastConfigurationFromOvertakenServer, lastSent);
				if (_cache != null) {
					lastSent = _cache;
					sendNewConfiguration(out, lastSent);
				}

				// Receive config from ohter Robotics API hosts, which are clients (sub servers)
				try {
					RapiInfo[] clientConfig = RapiInfo.fromText(in.readLine(100));
					synchronized (configurationsFromOtherServersMonitor) {
						configurationsFromOtherServers.put(connection, clientConfig);
					}
				} catch (TimeoutException e) {
				}
			}
		} catch (Exception e) {
			// Ignore exceptions, just shut down!
		} finally {
			try {
				connection.close();
			} catch (Exception e) {
			}
			synchronized (configurationsFromOtherServersMonitor) {
				configurationsFromOtherServers.remove(connection);
			}
			RAPILogger.getLogger()
					.fine("Closed connection from " + connection.getRemoteSocketAddress().toString() + ".");
		}
	}

	private RapiInfo[] getGlobalConfigurationIfModified(ConfigCache lastConfigurationFromOvertakenServer,
			RapiInfo[] lastSent) {
		List<RapiInfo> toSend;

		// Local visualization servers
		synchronized (serversMonitor) {
			toSend = serversToRapiInfos(servers);
		}

		// Visualization Servers from other lookup hosts
		synchronized (configurationsFromOtherServersMonitor) {
			for (RapiInfo[] infos : configurationsFromOtherServers.values()) {
				for (RapiInfo info : infos) {
					toSend.add(info);
				}
			}
		}

		// Overtaken visualization servers from old lookup host
		for (RapiInfo l : lastConfigurationFromOvertakenServer.getCurrent()) {
			boolean duplicate = false;
			for (RapiInfo curr : toSend) {
				if (curr.describesSameServer(l)) {
					duplicate = true;
					break;
				}
			}
			if (!duplicate) {
				toSend.add(l);
			}
		}
		return getNewConfigOrNullIfNoChanges(lastSent, toSend);
	}

	private RapiInfo[] getLocalConfigurationIfModified(RapiInfo[] lastSent) {
		synchronized (serversMonitor) {
			List<RapiInfo> toSend = serversToRapiInfos(servers);
			return getNewConfigOrNullIfNoChanges(lastSent, toSend);
		}
	}

	private static List<RapiInfo> serversToRapiInfos(Set<VisualizationServer> servers) {
		List<RapiInfo> result = new ArrayList<>();
		servers.forEach(vs -> {
			result.add(new RapiInfo(vs.getType(), vs.getPort(), vs.getRapiInstanceName()));
		});
		return result;
	}

	private static RapiInfo[] getNewConfigOrNullIfNoChanges(RapiInfo[] oldInfos, List<RapiInfo> newInfos) {
		List<RapiInfo> result = new ArrayList<>();
		newInfos = new ArrayList<>(newInfos);

		// Take all from newInfos, which are also in oldInfos (or realize, which entries
		// have vanished)
		boolean change = false;
		for (RapiInfo oldInfo : oldInfos) {
			RapiInfo found = null;
			for (RapiInfo newInfo : newInfos) {
				if (oldInfo.describesSameServer(newInfo)) {
					if (found == null) {
						found = newInfo;
						if (!oldInfo.equals(newInfo))
							change = true;
					} else if (newInfo.age < found.age) {
						// duplicate (older) entry has vanished
						found = newInfo;
						change = true;
					} else {
						// duplicate entry has vanished
						change = true;
					}
				}
			}
			if (found == null) {
				// entry has vanished
				change = true;
			} else {
				result.add(found);
				newInfos.remove(found);
				if (!oldInfo.name.equals(found.name))
					change = true;
			}
		}

		// Take all remaining from newInfos
		if (!newInfos.isEmpty()) {
			result.addAll(newInfos);
			change = true;
		}

		return change ? result.toArray(new RapiInfo[result.size()]) : null;
	}

	private static void sendNewConfiguration(BufferedWriter out, RapiInfo[] info) throws IOException {
		String message = RapiInfo.serialize(info) + "\n";
		out.write(message);
		out.flush();
	}

	private RapiInfo[] bindToOtherServer(int port) {
		Socket clientSocket = null;
		RapiInfo[] lastConfigurationFromOtherServer = null;
		try {
			clientSocket = new Socket("localhost", port);
			RAPILogger.getLogger().fine("Established connection to other lookup server "
					+ clientSocket.getRemoteSocketAddress().toString() + ".");

			try {
				AsyncLineReader inFromServer = new AsyncLineReader(clientSocket.getInputStream());
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

				RapiInfo[] lastSent = new RapiInfo[0];
				while (alive) {

					// Anderem Server (nur!) die eigene Konfiguration schicken
					RapiInfo[] _cache = getLocalConfigurationIfModified(lastSent);
					if (_cache != null) {
						lastSent = _cache;
						sendNewConfiguration(out, lastSent);
					}

					// Die Konfiguration des anderen Servers empfangen
					try {
						lastConfigurationFromOtherServer = RapiInfo.fromText(inFromServer.readLine(100));
					} catch (TimeoutException e) {
					}
				}
			} catch (Exception e) {
				// Just ignore and resume
			} finally {
				RAPILogger.getLogger().fine("Closed connection from other lookup server "
						+ clientSocket.getRemoteSocketAddress().toString() + ".");
				try {
					clientSocket.close();
				} catch (IOException e) {
				}
			}
		} catch (Exception e) {
			// Just ignore and resume
		}

		return lastConfigurationFromOtherServer;
	}

	private final void safeexec(RunnableWithException r) {
		try {
			r.run();
		} catch (Exception e) {
			RAPILogger.getLogger().warning(getClass() + ": " + e);
		}
	}

	private interface RunnableWithException {
		public void run() throws Exception;
	}

	public void close() {
		alive = false;
		serverThread.interrupt();
		while (true) {
			try {
				serverThread.join();
				break;
			} catch (InterruptedException e) {
			}
		}
	}

}
