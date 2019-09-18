/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.dioprotocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.facet.runtime.rpi.DeviceListener;
import org.roboticsapi.facet.runtime.rpi.DeviceStatus;
import org.roboticsapi.facet.runtime.rpi.Fragment;
import org.roboticsapi.facet.runtime.rpi.NetSerializer;
import org.roboticsapi.facet.runtime.rpi.NetStatus;
import org.roboticsapi.facet.runtime.rpi.NetSynchronizationRule.SynchronizationRuleStatus;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.parser.DIOProtocolParser;

public abstract class DIOProtocol {
	protected static final String DIO_VERSION = "2.0";
	protected final int DEBUG_PING_DELAY = 0;
	protected final String name;
	protected final ProtocolCallback callback;
	protected boolean ready = false;
	protected boolean netcommsChanged = false;

	private final Map<String, Semaphore> waitList = new Hashtable<String, Semaphore>();
	private final Map<String, String> resultList = new Hashtable<String, String>();
	private final Map<String, String> netMap = new Hashtable<String, String>();
	private final Map<String, String> ruleMap = new Hashtable<String, String>();
	private final Map<String, ResultListener> resultListeners = new Hashtable<String, DIOProtocol.ResultListener>();
	private String devicetag;
	private long lastResponseTime = System.currentTimeMillis();

	public interface ResultListener {
		void onSuccess(String message);

		void onError(String message);
	}

	public interface ProtocolCallback extends DeviceListener {
		void setNetStatus(String net, NetStatus status);

		void setRuleStatus(String rule, SynchronizationRuleStatus status);

		void setNetcommValue(String net, String netcomm, String value);

		void notifyNetcommUpdated();

	}

	public DIOProtocol(ProtocolCallback callback, String name) throws RpiException {
		this.name = name;
		this.callback = callback;
	}

	protected String getTagStatus(String string) throws RpiException {
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
			Thread.sleep(DEBUG_PING_DELAY);
			synchronized (waitList) {
				String result = resultList.get(string);
				waitList.remove(string);
				resultList.remove(string);
				if (result.startsWith("-")) {
					throw new RpiException(result.substring(1));
				}
				return result.substring(1);
			}
		} catch (InterruptedException ex) {
			throw new RpiException(ex);
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
			Thread.sleep(DEBUG_PING_DELAY);
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
		long startTime = System.currentTimeMillis();
		try {
			synchronized (waitList) {
				if (!resultList.containsKey(string)) {
					waitList.put(string, new Semaphore(1));
					waitList.get(string).acquire();
				}
			}
			while (System.currentTimeMillis() < lastResponseTime + timeout * 1000
					|| System.currentTimeMillis() < startTime + timeout * 100) {
				try {
					if (!resultList.containsKey(string)) {
						waitList.get(string).tryAcquire((long) (timeout * 1000), TimeUnit.MILLISECONDS);
					}
					break;
				} catch (InterruptedException ex) {
					return false;
				}
			}
			Thread.sleep(DEBUG_PING_DELAY);
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

	public boolean startNet(String net) throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}
		try {
			return checkTagStatus(writeCommand(new DIOCommand("nest", new DIOString(net))));
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	public String startNets(List<String> startNets) throws RpiException {
		return scheduleNets("t", new ArrayList<String>(), new ArrayList<String>(), startNets);
	}

	public boolean cancelNet(String net) throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}
		try {
			return checkTagStatus(writeCommand(new DIOCommand("neca", new DIOString(net))));
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	public String createNet(Fragment fragment, String session, String description, boolean realtime)
			throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}
		try {
			return getTagStatus(writeCommand(new DIOCommand("nene", new DIOString(NetSerializer.serialize(fragment)),
					new DIOString(session), new DIOString(description), new DIOFloat(realtime ? 0 : 0.05),
					new DIOInteger(realtime ? 1 : 0))));
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	public void createNetAsync(Fragment fragment, String session, String description, boolean realtime,
			ResultListener listener) throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}
		try {
			String net = NetSerializer.serialize(fragment);
			String tag = writeCommand(new DIOCommand("nene", new DIOString(net), new DIOString(session),
					new DIOString(description), new DIOFloat(realtime ? 0 : 0.05), new DIOInteger(realtime ? 1 : 0)));
			registerResultListener(tag, listener);
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	public String createSession(String name) throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}
		try {
			return getTagStatus(writeCommand(new DIOCommand("nse", new DIOString(name))));
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	public boolean abortNet(String net) throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}
		try {
			return checkTagStatus(writeCommand(new DIOCommand("neab", new DIOString(net))));
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	public String scheduleNets(String condition, List<String> stopNets, List<String> cancelNets, List<String> startNets)
			throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}

		try {
			DIOString cond = new DIOString(condition);
			DIOParameterList stop = new DIOParameterList();
			for (String s : stopNets) {
				stop.addParameter(new DIOString(s));
			}
			DIOParameterList cancel = new DIOParameterList();
			for (String s : cancelNets) {
				cancel.addParameter(new DIOString(s));
			}
			DIOParameterList start = new DIOParameterList();
			for (String s : startNets) {
				start.addParameter(new DIOString(s));
			}
			return getTagStatus(writeCommand(new DIOCommand("nesc", cond, stop, cancel, start)));
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	public void scheduleNetsAsync(String condition, List<String> stopNets, List<String> cancelNets,
			List<String> startNets, ResultListener listener) throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}

		try {
			DIOString cond = new DIOString(condition);
			DIOParameterList stop = new DIOParameterList();
			for (String s : stopNets) {
				stop.addParameter(new DIOString(s));
			}
			DIOParameterList cancel = new DIOParameterList();
			for (String s : cancelNets) {
				cancel.addParameter(new DIOString(s));
			}
			DIOParameterList start = new DIOParameterList();
			for (String s : startNets) {
				start.addParameter(new DIOString(s));
			}
			String tag = writeCommand(new DIOCommand("nesc", cond, stop, cancel, start));
			registerResultListener(tag, listener);
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	private void registerResultListener(String tag, ResultListener listener) {
		resultListeners.put(tag, listener);
		if (resultList.containsKey(tag)) {
			String result = resultList.remove(tag);
			if (result.startsWith("+")) {
				listener.onSuccess(result.substring(1));
			} else {
				listener.onError(result.substring(1));
			}
			resultListeners.remove(tag);
		}
	}

	public boolean unloadNet(String net) throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}
		try {
			return checkTagStatus(writeCommand(new DIOCommand("neun", new DIOString(net))));
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	public void unloadNetAsync(String net, ResultListener listener) throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}
		try {
			registerResultListener(writeCommand(new DIOCommand("neun", new DIOString(net))), listener);
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	public boolean observeDevices() throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}
		try {
			devicetag = writeCommand(new DIOCommand("gde"));
			return checkTagStatus(devicetag);
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	public boolean observeNet(String net, double frequency) throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
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
			throw new RpiException(e);
		}
	}

	public void observeNetAsync(String net, double frequency, final ResultListener listener) throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}
		try {
			String tag;
			synchronized (netMap) {
				if (netMap.containsValue(net)) {
					if (listener != null) {
						listener.onSuccess("");
					}
					return;
				}
				tag = writeCommand(new DIOCommand("gne", new DIOString(net), new DIOFloat(frequency)));
				netMap.put(tag, net);
			}
			final String mtag = tag;
			ResultListener resultListener = new ResultListener() {
				@Override
				public void onSuccess(String result) {
					if (listener != null) {
						listener.onSuccess(result);
					}
				}

				@Override
				public void onError(String message) {
					if (listener != null) {
						listener.onError(message);
					}
					netMap.remove(mtag);
				}
			};
			registerResultListener(tag, resultListener);
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	public boolean observeRule(String rule) throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}
		try {
			String tag;
			synchronized (ruleMap) {
				if (ruleMap.containsValue(rule)) {
					return true;
				}
				tag = writeCommand(new DIOCommand("gsy", new DIOString(rule)));
				ruleMap.put(tag, rule);
			}
			boolean tagStatus = checkTagStatus(tag);
			if (!tagStatus) {
				ruleMap.remove(tag);
			}
			return tagStatus;
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	public void observeRuleAsync(String rule, final ResultListener listener) throws RpiException {
		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
		}
		try {
			String tag;
			synchronized (ruleMap) {
				if (ruleMap.containsValue(rule)) {
					if (listener != null) {
						listener.onSuccess("");
					}
					return;
				}
				tag = writeCommand(new DIOCommand("gsy", new DIOString(rule)));
				ruleMap.put(tag, rule);
			}
			final String mtag = tag;
			ResultListener resultListener = new ResultListener() {
				@Override
				public void onSuccess(String result) {
					if (listener != null) {
						listener.onSuccess(result);
					}
				}

				@Override
				public void onError(String message) {
					if (listener != null) {
						listener.onError(message);
					}
					synchronized (ruleMap) {
						ruleMap.remove(mtag);
					}
				}
			};
			registerResultListener(tag, resultListener);
		} catch (IOException e) {
			throw new RpiException(e);
		}
	}

	protected abstract String writeCommand(DIOCommand command) throws IOException;

	public boolean writeNetcomm(Map<String, Map<String, NetcommValue>> netcommToRPI) throws RpiException {

		if (!ready) {
			throw new RpiException("Connection to RCC failed.");
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

	long lastPrint;
	long notifierCount;

	protected void parseDirectIO(String response) {
		// RAPILogger.getLogger().finest("<< " + response.trim());

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

				if (command.getParameterSize() > 0) {
					message = command.getParameter(0, DIOString.class).getString();
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
				if (System.currentTimeMillis() - lastPrint > 1000) {
					RAPILogger.getLogger(this).fine("Executed " + notifierCount + " updates");
					notifierCount = 0;
					lastPrint = System.currentTimeMillis();
				}

				DIOParameterMap pmap = command.getParameter(0, DIOParameterMap.class);

				for (String entry : pmap.getMap().keySet()) {
					notifierCount++;
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
			} else if (cmdstr.equals("rs")) {
				// rule status value
				String state = command.getParameter(0, DIOString.class).getString();
				synchronized (ruleMap) {
					if (ruleMap.containsKey(tag)) {
						callback.setRuleStatus(ruleMap.get(tag), SynchronizationRuleStatus.valueOf(state));
					}
					if (!state.equals("ACTIVE")) {
						ruleMap.remove(tag);
					}
				}
			} else if (cmdstr.equals("da")) {
				// device added
				DIOParameterMap dmap = command.getParameter(0, DIOParameterMap.class);
				for (String entry : dmap.getMap().keySet()) {
					DIOParameterMap devinfo = dmap.getParameter(entry, DIOParameterMap.class);

					String type = devinfo.getParameter("type", DIOString.class).getString();
					Map<String, RpiParameters> interfaces = new HashMap<String, RpiParameters>();

					for (Entry<String, DIOParameter> intf : devinfo.getParameter("interfaces", DIOParameterMap.class)
							.getMap().entrySet()) {
						RpiParameters params = new RpiParameters();
						interfaces.put(intf.getKey(), params);
						DIOParameterMap pmap = (DIOParameterMap) intf.getValue();
						for (String key : pmap.getMap().keySet()) {
							params.put(key, pmap.getParameter(key, DIOString.class).getString());
						}
					}
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

					DeviceStatus status = DeviceStatus.OFFLINE;
					if (diostate.equals("op")) {
						status = DeviceStatus.OPERATIONAL;
					}
					if (diostate.equals("off")) {
						status = DeviceStatus.OFFLINE;
					}
					if (diostate.equals("safeop")) {
						status = DeviceStatus.SAFE_OPERATIONAL;
					}
					callback.deviceStatusChanged(entry, status);
				}
			} else {
				RAPILogger.getLogger(this).warning("Unknown message type " + cmdstr);
			}

		} catch (

		IllegalArgumentException iae) {
			iae.printStackTrace();
		}
	}

	protected void setTagStatus(String token, boolean status, String message) {
		synchronized (waitList) {
			lastResponseTime = System.currentTimeMillis();
			resultList.put(token, (status ? "+" : "-") + message);
			if (waitList.containsKey(token)) {
				waitList.get(token).release();
			}
			if (resultListeners.containsKey(token)) {
				if (status) {
					resultListeners.get(token).onSuccess(message);
				} else {
					resultListeners.get(token).onError(message);
				}
				resultListeners.remove(token);
				resultList.remove(token);
			}
		}
	}

	public abstract boolean isReaderThread();

}
