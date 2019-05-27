/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.roboticsapi.core.util.RAPILogger;

/**
 * A {@link LookupClient} is a asynchronously running service which tracks the
 * online state and data from a {@link LookupServer} running on a certain host.
 * 
 * If a {@link LookupServer} is detected to be online, the {@link LookupClient}
 * connects to it and receives an overview about all running services on this
 * host. This might be a port for connecting a visualization over tcp o a port
 * for connecting a visualization over rmi.
 * 
 * There is never more than one {@link LookupServer} running on one host at a
 * time. This one {@link LookupServer} provides information about all running
 * and available Robotics API instances, even if they're running in different
 * JVMs.
 */
public abstract class LookupClient {

	public static void main(String[] args) {
		new LookupClient("localhost", 4320) {
			@Override
			public void newConfiguration(RapiInfo[] servers) {
				for (RapiInfo s : servers) {
					System.out.println(s.type);
					System.out.println(s.port);
					System.out.println(s.name);
					System.out.println();
				}
			}

			@Override
			public void connectionStateChanged(boolean online) {
				System.out.println("Connection state: " + (online ? "online" : "offline"));
			}
		};
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

	public static interface RapiInfoListener {
		public void newConfiguration(RapiInfo[] servers);

		public void connectionStateChanged(boolean online);
	}

	private static final long ALLOWED_SERVER_OVERTAKE_DELAY = 200;

	private final Thread thread;
	private boolean alive = true;
	private Boolean connectionState = null;

	private Socket clientSocket = null;
	private Object socketMonitor = new Object();

	public LookupClient(String host, int port) {
		thread = new Thread(() -> run(host, port));
		thread.setDaemon(true);
		thread.setName("Robotics API lookup client [" + host + ":" + port + "]");
		thread.start();
	}

	public abstract void newConfiguration(RapiInfo[] servers);

	public abstract void connectionStateChanged(boolean online);

	private final void run(String host, int port) {
		Long offlineSince = 0l;
		while (alive) {
			try {
				synchronized (socketMonitor) {
					if (!alive)
						break;
					clientSocket = new Socket(host, port);
				}
				RAPILogger.getLogger()
						.fine("Connection established to lookup server " + host + " on port " + port + ".");
				offlineSince = null;
				switchConnectionState(true);
				clientSocket.setKeepAlive(true);

				BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				while (alive) {
					String message = reader.readLine();
					if (message == null)
						throw new EOFException();
					RapiInfo[] servers = RapiInfo.fromText(message);
					safeexec(() -> newConfiguration(servers));
				}
				// Verbindung wurde serverseitig ordentlich beendet
			} catch (IOException e) {
			} catch (Exception e) {
				RAPILogger.getLogger().warning(getClass() + ": " + e);
				alive = false;
				return;
			} finally {
				synchronized (socketMonitor) {
					if (!alive)
						break;
					if (clientSocket != null) {
						try {
							clientSocket.close();
							clientSocket = null;
						} catch (IOException e) {
						}
						RAPILogger.getLogger().fine("Connection closed from " + host + " on port " + port + ".");
					}
				}
				if (offlineSince == null) {
					offlineSince = System.currentTimeMillis();
				} else if (System.currentTimeMillis() - offlineSince > ALLOWED_SERVER_OVERTAKE_DELAY) {
					switchConnectionState(false);
				}
			}
		}
		switchConnectionState(false);
	}

	private final void switchConnectionState(boolean newConnectionState) {
		if (connectionState != null && newConnectionState == connectionState)
			return;

		connectionState = newConnectionState;

		// wenn ofline: sende leere RapiInfo liste // TODO:
		if (!newConnectionState)
			safeexec(() -> newConfiguration(new RapiInfo[0]));

		safeexec(() -> connectionStateChanged(newConnectionState));
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

	/**
	 * Shuts down this lookup client. After done, a new empty configuration is set
	 * as well as a new offline state.
	 */
	public final void shutdown() {
		alive = false;
		thread.interrupt();
		synchronized (socketMonitor) {
			try {
				if (clientSocket != null)
					clientSocket.close();
			} catch (IOException e) {
			}
		}
		while (true) {
			try {
				thread.join();
				break;
			} catch (InterruptedException e) {
			}
		}
		switchConnectionState(false);
	}

}
