/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.controlcore.dioprotocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.roboticsapi.runtime.controlcore.SoftRobotDeviceListener;
import org.roboticsapi.runtime.controlcore.SoftRobotDeviceStatus;
import org.roboticsapi.runtime.controlcore.SoftRobotDirectIONet;
import org.roboticsapi.runtime.controlcore.dioprotocol.parser.DIOProtocolParser;
import org.roboticsapi.runtime.rpi.Fragment;
import org.roboticsapi.runtime.rpi.NetStatus;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.RPIException;

public abstract class DIOProtocol {
	protected static final String DIO_VERSION = "2.0";
	protected final boolean DEBUG = false;
	protected final String name;
	protected final ProtocolCallback callback;
	protected boolean ready = false;
	protected boolean netcommsChanged = false;

	private final Map<String, Semaphore> waitList = new Hashtable<String, Semaphore>();
	private final Map<String, String> resultList = new Hashtable<String, String>();
	private final Map<String, String> netMap = new Hashtable<String, String>();
	private String devicetag;

	public interface ProtocolCallback extends SoftRobotDeviceListener {
		void setNetStatus(String net, NetStatus status);

		void setNetcommValue(String net, String netcomm, String value);

		void notifyNetcommUpdated();
	}

	public DIOProtocol(ProtocolCallback callback, String name) throws RPIException {
		this.name = name;
		this.callback = callback;
	}

	protected String getTagStatus(String string) throws RPIException {
		if (isReaderThread()) {
			return "";
		}
		try {
			synchronized (waitList) {
				if (!resultList.containsKey(string)) {
					waitList.put(string, new Semaphore(1));
					waitList.get(string).acquire();
				}
			}

			if (!resultList.containsKey(string)) {
				waitList.get(string).acquire();
			}
			synchronized (waitList) {
				String result = resultList.get(string);
				waitList.remove(string);
				resultList.remove(string);
				if (result.startsWith("-")) {
					throw new RPIException(result.substring(1));
				}
				return result.substring(1);
			}
		} catch (InterruptedException ex) {
			throw new RPIException(ex);
		}
	}

	protected boolean checkTagStatus(String string) {
		if (isReaderThread()) {
			return true;
		}
		try {
			synchronized (waitList) {
				if (!resultList.containsKey(string)) {
					waitList.put(string, new Semaphore(1));
					waitList.get(string).acquire();
				}
			}

			if (!resultList.containsKey(string)) {
				waitList.get(string).acquire();
			}
			synchronized (waitList) {
				String result = resultList.get(string);
				waitList.remove(string);
				resultList.remove(string);
				return result.startsWith("+");
			}
		} catch (InterruptedException ex) {
			return false;
		}
	}

	protected boolean checkTagStatus(String string, double timeout) {
		if (isReaderThread()) {
			return true;
		}
		try {
			synchronized (waitList) {
				if (!resultList.containsKey(string)) {
					waitList.put(string, new Semaphore(1));
					waitList.get(string).acquire();
				}
			}

			if (!resultList.containsKey(string)) {
				waitList.get(string).tryAcquire((long) (timeout * 1000), TimeUnit.MILLISECONDS);
			}
			synchronized (waitList) {
				String result = resultList.containsKey(string) ? resultList.get(string) : "";
				waitList.remove(string);
				resultList.remove(string);
				return result.startsWith("+");
			}
		} catch (InterruptedException ex) {
			return false;
		}
	}

	public boolean startNet(String net) throws RPIException {
		if (!ready) {
			throw new RPIException("Connection to RCC failed.");
		}
		try {
			return checkTagStatus(writeCommand(new DIOCommand("nest", new DIOString(net))));
		} catch (IOException e) {
			throw new RPIException(e);
		}
	}

	public boolean cancelNet(String net) throws RPIException {
		if (!ready) {
			throw new RPIException("Connection to RCC failed.");
		}
		try {
			return checkTagStatus(writeCommand(new DIOCommand("neca", new DIOString(net))));
		} catch (IOException e) {
			throw new RPIException(e);
		}
	}

	public String createNet(Fragment fragment, String session, String description) throws RPIException {
		if (!ready) {
			throw new RPIException("Connection to RCC failed.");
		}
		try {
			return getTagStatus(
					writeCommand(new DIOCommand("nene", new DIOString(SoftRobotDirectIONet.toNewFormat(fragment)),
							new DIOString(session), new DIOString(description), new DIOFloat(0), new DIOInteger(1))));
		} catch (IOException e) {
			throw new RPIException(e);
		}
	}

	public String createSession(String name) throws RPIException {
		if (!ready) {
			throw new RPIException("Connection to RCC failed.");
		}
		try {
			return getTagStatus(writeCommand(new DIOCommand("nse", new DIOString(name))));
		} catch (IOException e) {
			throw new RPIException(e);
		}
	}

	public boolean abortNet(String net) throws RPIException {
		if (!ready) {
			throw new RPIException("Connection to RCC failed.");
		}
		try {
			return checkTagStatus(writeCommand(new DIOCommand("neab", new DIOString(net))));
		} catch (IOException e) {
			throw new RPIException(e);
		}
	}

	public boolean scheduleNet(String net, String takeoverNet) throws RPIException {
		if (!ready) {
			throw new RPIException("Connection to RCC failed.");
		}
		try {
			return checkTagStatus(writeCommand(new DIOCommand("nesc", new DIOString(net), new DIOString(takeoverNet))));
		} catch (IOException e) {
			throw new RPIException(e);
		}
	}

	public boolean unloadNet(String net) throws RPIException {
		if (!ready) {
			throw new RPIException("Connection to RCC failed.");
		}
		try {
			return checkTagStatus(writeCommand(new DIOCommand("neun", new DIOString(net))));
		} catch (IOException e) {
			throw new RPIException(e);
		}
	}

	public boolean observeDevices() throws RPIException {
		if (!ready) {
			throw new RPIException("Connection to RCC failed.");
		}
		try {
			devicetag = writeCommand(new DIOCommand("gde"));
			return checkTagStatus(devicetag);
		} catch (IOException e) {
			throw new RPIException(e);
		}
	}

	public boolean observeNet(String net, double frequency) throws RPIException {
		if (!ready) {
			throw new RPIException("Connection to RCC failed.");
		}
		try {
			String tag;
			synchronized (netMap) {
				if (netMap.containsValue(net)) {
					return true;
				}
				tag = writeCommand(new DIOCommand("gne", new DIOString(net), new DIOFloat(frequency)));
				netMap.put(tag, net);
			}
			boolean tagStatus = checkTagStatus(tag);
			if (!tagStatus) {
				netMap.remove(tag);
			}
			return tagStatus;
		} catch (IOException e) {
			throw new RPIException(e);
		}
	}

	protected abstract String writeCommand(DIOCommand command) throws IOException;

	public boolean writeNetcomm(Map<String, Map<String, NetcommValue>> netcommToRPI) throws RPIException {

		if (!ready) {
			throw new RPIException("Connection to RCC failed.");
		}

		if (netcommToRPI.size() > 0) {
			DIOCommand cmd = new DIOCommand("snc");

			DIOParameterMap netmap = new DIOParameterMap();

			for (Map.Entry<String, Map<String, NetcommValue>> nets : netcommToRPI.entrySet()) {
				String netName = nets.getKey();

				DIOParameterMap ncvalues = new DIOParameterMap();

				for (Map.Entry<String, NetcommValue> value : nets.getValue().entrySet()) {
					ncvalues.addParameter(value.getKey(), new DIOString(value.getValue().getString()));
				}

				netmap.addParameter(netName, ncvalues);
			}

			cmd.addParameter(netmap);

			try {
				String tag = writeCommand(cmd);
				return checkTagStatus(tag);
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}

	protected void parseDirectIO(String response) {
		// System.err.println("<< " + response.trim());

		DIOStatement statement = DIOProtocolParser.parse(response);

		// if null is returned, parsing failed
		if (statement == null) {
			return;
		}

		String tag = statement.getTag();
		DIOCommand command = statement.getCommand();
		String cmdstr = command.getCommand();
		try {
			if (cmdstr.equals("ok")) {
				// ok message
				String message = "";

				try {
					message = command.getParameter(0, DIOString.class).getString();
				} catch (IllegalArgumentException ex) {
					// ok, no parameter given ...
				}

				setTagStatus(tag, true, message);
			} else if (cmdstr.equals("err")) {
				// error message
				String message = "";

				try {
					message = command.getParameter(0, DIOString.class).getString();
				} catch (IllegalArgumentException ex) {
					// ok, no parameter given ...
				}

				setTagStatus(tag, false, message);
			} else if (cmdstr.equals("nc")) {
				// netcomm values
				DIOParameterMap pmap = command.getParameter(0, DIOParameterMap.class);

				for (String entry : pmap.getMap().keySet()) {

					String value = pmap.getParameter(entry, DIOString.class).getString();

					synchronized (netMap) {
						callback.setNetcommValue(netMap.get(tag), entry, value);
					}
					netcommsChanged = true;
				}
			} else if (cmdstr.equals("ns")) {
				// net status value
				String state = command.getParameter(0, DIOString.class).getString();
				synchronized (netMap) {
					if (netMap.containsKey(tag)) {
						callback.setNetStatus(netMap.get(tag), NetStatus.valueOf(state));
					}
					if (state.equals("UNLOADED")) {
						netMap.remove(tag);
					}
				}
			} else if (cmdstr.equals("da")) {
				// device added
				DIOParameterMap dmap = command.getParameter(0, DIOParameterMap.class);
				for (String entry : dmap.getMap().keySet()) {
					DIOParameterMap devinfo = dmap.getParameter(entry, DIOParameterMap.class);

					String type = devinfo.getParameter("type", DIOString.class).getString();
					List<String> interfaces = new ArrayList<String>();

					// At the moment, the interfaces map does not yet contain
					// any useful data
					interfaces.addAll(devinfo.getParameter("interfaces", DIOParameterMap.class).getMap().keySet());

					callback.deviceAdded(entry, type, interfaces);

				}
			} else if (cmdstr.equals("dr")) {
				// device removed
				DIOParameterList dlist = command.getParameter(0, DIOParameterList.class);

				for (int i = 0; i < dlist.getList().size(); i++) {
					String dev = dlist.getParameter(i, DIOString.class).getString();

					callback.deviceRemoved(dev);

				}
			} else if (cmdstr.equals("ds")) {
				// device status
				DIOParameterMap dmap = command.getParameter(0, DIOParameterMap.class);

				for (String entry : dmap.getMap().keySet()) {

					String diostate = dmap.getParameter(entry, DIOString.class).getString();

					SoftRobotDeviceStatus status = SoftRobotDeviceStatus.OFFLINE;
					if (diostate.equals("op")) {
						status = SoftRobotDeviceStatus.OPERATIONAL;
					}
					if (diostate.equals("off")) {
						status = SoftRobotDeviceStatus.OFFLINE;
					}
					if (diostate.equals("safeop")) {
						status = SoftRobotDeviceStatus.SAFE_OPERATIONAL;
					}
					callback.deviceStatusChanged(entry, status);
				}
			} else {
				System.err.println("Unknown message type " + cmdstr);
			}

		} catch (IllegalArgumentException iae) {
			if (DEBUG) {
				iae.printStackTrace();
			}
		}
	}

	protected void setTagStatus(String token, boolean status, String message) {
		synchronized (waitList) {
			resultList.put(token, (status ? "+" : "-") + message);
			if (waitList.containsKey(token)) {
				waitList.get(token).release();
			}
		}
	}

	public abstract boolean isReaderThread();

}
