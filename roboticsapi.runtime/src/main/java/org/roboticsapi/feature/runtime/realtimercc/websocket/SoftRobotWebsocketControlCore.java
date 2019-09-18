/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.websocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.facet.runtime.rpi.ControlCore;
import org.roboticsapi.facet.runtime.rpi.DeviceListener;
import org.roboticsapi.facet.runtime.rpi.DeviceStatus;
import org.roboticsapi.facet.runtime.rpi.Fragment;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.NetResult;
import org.roboticsapi.facet.runtime.rpi.NetSerializer;
import org.roboticsapi.facet.runtime.rpi.NetStartException;
import org.roboticsapi.facet.runtime.rpi.NetStatus;
import org.roboticsapi.facet.runtime.rpi.NetSynchronizationRule;
import org.roboticsapi.facet.runtime.rpi.NetSynchronizationRule.SynchronizationRuleListener;
import org.roboticsapi.facet.runtime.rpi.NetSynchronizationRule.SynchronizationRuleStatus;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.feature.runtime.realtimercc.RccNetResult;
import org.roboticsapi.feature.runtime.realtimercc.SoftRobotHTTP;
import org.roboticsapi.feature.runtime.realtimercc.SoftRobotHTTP.HTTPResponse;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOProtocol;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOProtocol.ProtocolCallback;
import org.roboticsapi.feature.runtime.realtimercc.dioprotocol.DIOProtocol.ResultListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A connector to the SoftRobot RCC
 */
public class SoftRobotWebsocketControlCore implements ControlCore, ProtocolCallback {
	private final List<Thread> poolThreads = new ArrayList<Thread>();
	ThreadPoolExecutor notifierPool = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r);
					poolThreads.add(t);
					return t;
				}
			});
	private final List<DeviceListener> deviceListeners = new ArrayList<DeviceListener>();
	private final Hashtable<String, SoftRobotWebsocketNetHandle> netHandles = new Hashtable<String, SoftRobotWebsocketNetHandle>();
	private final Map<String, SoftRobotSynchronizationRule> rules = new HashMap<String, SoftRobotSynchronizationRule>();
	private final Map<String, Map<String, NetcommValue>> netcommFromRPI = new ConcurrentHashMap<String, Map<String, NetcommValue>>();

	/** URI to access the SoftRobot RCC */
	private final String uri;
	private WebsocketProtocol dataProtocol, controlProtocol;
	private String appName = null;
	private String session = null;
	private double frequency = 0.02;
	private final Object sessionLock = new Object();

	/**
	 * Creates a new SoftRobot control core
	 *
	 * @param uri HTTP URI to access the SoftRobot control core
	 * @throws RoboticsException if an error occurs
	 */
	public SoftRobotWebsocketControlCore(String uri) throws RpiException {
		if (!uri.endsWith("/")) {
			uri += "/";
		}
		this.uri = uri;
		appName = getAppName();
		try {
			startSession();
		} catch (RpiException e) {
			RAPILogger.getLogger(this).warning("Failed to connect to " + uri);
		}
	}

	/**
	 * Creates a new SoftRobot control core
	 *
	 * @param uri       HTTP URI to access the SoftRobot control core
	 * @param frequency update frequency in seconds
	 * @throws RoboticsException if an error occurs
	 */
	public SoftRobotWebsocketControlCore(String uri, double frequency) throws RpiException {
		this(uri);
		this.frequency = frequency;
	}

	@Override
	public void addDeviceListener(DeviceListener listener) {
		deviceListeners.add(listener);
		for (String device : deviceTypes.keySet()) {
			listener.deviceAdded(device, deviceTypes.get(device), deviceInterfaces.get(device));
		}
		for (String device : deviceStatus.keySet()) {
			listener.deviceStatusChanged(device, deviceStatus.get(device));
		}
	}

	@Override
	public void removeDeviceListener(DeviceListener listener) {
		deviceListeners.remove(listener);
	}

	@Override
	public void ping() throws RpiException {
		try {
			controlProtocol.ping();
		} catch (IOException | NullPointerException e) {
			if (!reconnect()) {
				throw new RpiException(e);
			}
		}
	}

	private boolean reconnect() {
		try {
			cleanupProtocol();
			setupProtocol();
			List<String> unavailableNets = new ArrayList<String>();
			for (String name : observedNets) {
				if (!dataProtocol.observeNet(name, frequency)) {
					unavailableNets.add(name);
					if (netHandles.get(name) != null && netHandles.get(name).getStatus() != NetStatus.TERMINATED) {
						netHandles.get(name).setStatus(NetStatus.ERROR);
					}
				}
			}
			observedNets.removeAll(unavailableNets);

			deviceInterfaces.clear();
			deviceTypes.clear();
			deviceStatus.clear();
			dataProtocol.observeDevices();

			RAPILogger.getLogger(this).info("Reconnected to " + uri);

		} catch (RpiException e) {
			return false;
		}
		return true;
	}

	@Override
	public void shutdown() {
		try {
			for (SoftRobotWebsocketNetHandle h : new ArrayList<SoftRobotWebsocketNetHandle>(unloads)) {
				controlProtocol.unloadNet(h.getName());
			}
			cleanupProtocol();
			notifierPool.shutdown();
		} catch (RpiException e) {
		}
	}

	private String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "localhost";
		}
	}

	private String getAppName() {
		String mainClass = "";
		for (StackTraceElement[] stack : Thread.getAllStackTraces().values()) {
			if (stack.length == 0) {
				continue;
			}
			StackTraceElement main = stack[stack.length - 1];
			if (main.getMethodName().toLowerCase().equals("main")) {
				mainClass = main.getClassName();
			}
		}
		if (mainClass.equals("")) {
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			if (stack.length != 0) {
				mainClass = stack[stack.length - 1].getClassName();
			}
		}
		return mainClass;
	}

	@Override
	public NetHandle load(final Fragment fragment, final String description, boolean realtime) throws RpiException {
		checkBlockControlThread();

		synchronized (sessionLock) {
			if (session == null) {
				startSession();
			}
		}

		final SoftRobotWebsocketNetHandle netHandle = new SoftRobotWebsocketNetHandle(this);
		String name = description.replace('|', ' ');
		if (name.length() > 40) {
			name = name.substring(0, 40) + "...";
		}
		controlProtocol.createNetAsync(fragment, session, name, realtime, new ResultListener() {
			@Override
			public void onSuccess(String netName) {
				try {
					if (netHandle.isUnloaded() || unloads.contains(netHandle)) {
						netHandle.setResultStatus(NetStatus.READY);
						netHandle.setStatus(NetStatus.UNLOADED);
						controlProtocol.unloadNetAsync(netName, new ResultListener() {
							@Override
							public void onSuccess(String message) {
							}

							@Override
							public void onError(String message) {
							}
						});
						return;
					}
					netHandle.setName(netName);
					RAPILogger.getLogger(this).fine("Net " + netHandle.hashCode() + " loaded as " + netName);
					if (!unloads.contains(netHandle)) {
						RAPILogger.getLogger(this).fine("Load: " + description + " as " + netName);
						for (final NetcommValue netcomm : fragment.getNetcommFromRPI()) {
							addNetcommFromRPI(netHandle, netcomm);
						}
						for (final NetcommValue netcomm : fragment.getNetcommToRPI()) {
							netHandle.addNetcommToRPI(netcomm);
						}
						synchronized (netHandles) {
							netHandles.put(netName, netHandle);
						}
						observedNets.add(netName);
						dataProtocol.observeNetAsync(netName, frequency, null);
					} else {
						RAPILogger.getLogger(this).fine("Net " + netHandle.hashCode() + " already unloaded...");
					}
					processSchedules();
				} catch (RpiException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(String message) {
				RAPILogger.getLogger(this).fine("Failed: To load " + description + ": " + message);
				netHandle.setName("REJECTED");
				RAPILogger.getLogger(this).fine("Net " + netHandle.hashCode() + " failed");
				netHandle.setStatus(NetStatus.ERROR);
				try {
					processSchedules();
				} catch (RpiException e) {
					e.printStackTrace();
				}
			}
		});
		RAPILogger.getLogger(this).fine("Net " + netHandle.hashCode() + " loading");
		netHandle.setNet(fragment);
		return netHandle;
	}

	private final List<String> observedNets = new ArrayList<String>();

	private void startSession() throws RpiException {
		synchronized (sessionLock) {
			cleanupSession();
			cleanupProtocol();
			observedNets.clear();
			setupProtocol();
			setupSession();
			dataProtocol.observeDevices();
		}
	}

	private void setupSession() throws RpiException {
		String desc = appName + "@" + getHostName();
		session = controlProtocol.createSession(desc);
	}

	private void setupProtocol() throws RpiException {
		controlProtocol = new WebsocketProtocol(uri, this, "Control");
		dataProtocol = new WebsocketProtocol(uri, this, "Data");
	}

	private void cleanupProtocol() {
		if (dataProtocol != null) {
			dataProtocol.quit();
		}
		if (controlProtocol != null) {
			controlProtocol.quit();
		}
	}

	private void cleanupSession() {
		if (session != null) {
			SoftRobotHTTP.post(session, "action=TERMINATE");
			session = null;
			for (SoftRobotWebsocketNetHandle nh : netHandles.values()) {
				nh.setStatus(NetStatus.ERROR);
			}
			netHandles.clear();
		}
	}

	@Override
	public List<NetHandle> getNetHandles() {
		return new ArrayList<NetHandle>(netHandles.values());
	}

	protected boolean startNets(List<SoftRobotWebsocketNetHandle> nets) throws RpiException {
		checkBlockControlThread();

		List<String> netnames = new ArrayList<String>();
		for (SoftRobotWebsocketNetHandle net : nets) {
			if (net.getStatus() != NetStatus.READY) {
				throw new NetStartException("Net is not ready (Net status: " + net.getStatus() + ")");
			}
			netnames.add(net.getName());
		}

		if (controlProtocol.startNets(netnames) == null) {
			throw new NetStartException(
					"Could not start net, though it was ready. Probably some net resources are unavailable.");
		}

		for (SoftRobotWebsocketNetHandle net : nets) {
			net.setStatus(NetStatus.RUNNING);
		}
		return true;
	}

	protected boolean abortNet(SoftRobotWebsocketNetHandle net) throws RpiException {
		final NetStatus status = net.getStatus();
		if (status != NetStatus.RUNNING && status != NetStatus.CANCELLING) {
			return false;
		}

		return controlProtocol.abortNet(net.getName());
	}

	protected boolean cancelNet(SoftRobotWebsocketNetHandle net) throws RpiException {
		return controlProtocol.cancelNet(net.getName());
	}

	protected void unloadNet(SoftRobotWebsocketNetHandle net) throws RpiException {
		if (!unloads.contains(net)) {
			RAPILogger.getLogger(this).fine("Net " + net.hashCode() + " unloaded");
			unloads.add(net);
		}
		processSchedules();
	}

	@Override
	public boolean start(List<NetHandle> handles) throws RpiException {
		return schedule(new ArrayList<NetResult>(), new ArrayList<NetHandle>(), new ArrayList<NetHandle>(),
				handles) != null;
	}

	@Override
	public NetSynchronizationRule schedule(List<NetResult> results, List<NetHandle> stopNets,
			List<NetHandle> cancelNets, List<NetHandle> startNets) throws RpiException {
		checkBlockControlThread();
		List<String> stop = new ArrayList<String>();
		List<String> cancel = new ArrayList<String>();
		List<String> start = new ArrayList<String>();
		String cond = createScheduleArgs(results, stopNets, cancelNets, startNets, stop, cancel, start, false);
		if (cond.equals("f")) {
			throw new NetStartException("Could not schedule net, because it does not exist.");
		}
		String rule = controlProtocol.scheduleNets(cond, stop, cancel, start);
		if (rule == null) {
			throw new NetStartException(
					"Could not schedule net, though it was ready. Probably some net resources are unavailable.");
		}
		scheduleDebugOutput(results, startNets, rule);

		SoftRobotSynchronizationRule ret = new SoftRobotSynchronizationRule();
		rules.put(rule, ret);
		dataProtocol.observeRule(rule);
		return ret;
	}

	class ScheduleParameters {
		public ScheduleParameters(List<NetResult> results, List<NetHandle> stopNets, List<NetHandle> cancelNets,
				List<NetHandle> startNets, SynchronizationRuleListener listener) {
			super();
			this.results = results;
			this.stopNets = stopNets;
			this.cancelNets = cancelNets;
			this.startNets = startNets;
			this.listener = listener;
		}

		public final List<NetResult> results;
		public final List<NetHandle> stopNets;
		public final List<NetHandle> cancelNets;
		public final List<NetHandle> startNets;
		public final NetSynchronizationRule.SynchronizationRuleListener listener;
	}

	private final List<ScheduleParameters> schedules = new ArrayList<ScheduleParameters>();
	private final List<SoftRobotWebsocketNetHandle> unloads = new ArrayList<SoftRobotWebsocketNetHandle>();

	@Override
	public synchronized void schedule(final List<NetResult> results, List<NetHandle> stopNets,
			List<NetHandle> cancelNets, final List<NetHandle> startNets,
			final NetSynchronizationRule.SynchronizationRuleListener listener) throws RpiException {
		ScheduleParameters sp = new ScheduleParameters(results, stopNets, cancelNets, startNets, listener);
		schedules.add(sp);
		RAPILogger.getLogger(this).fine("Rule " + sp.hashCode() + " scheduled");
		processSchedules();
	}

	private synchronized void processSchedules() throws RpiException {
		RAPILogger.getLogger(this).fine("Processing schedules");
		for (SoftRobotWebsocketNetHandle net : new ArrayList<SoftRobotWebsocketNetHandle>(unloads)) {
			if (net.getStatus() == NetStatus.LOADING) {
				continue;
			} else if (net.isUnloaded()) {
				RAPILogger.getLogger(this).fine("Net " + net.hashCode() + " not being unloaded...");
			} else {
				RAPILogger.getLogger(this).fine("Net " + net.hashCode() + " being unloaded...");
				synchronized (netcommFromRPI) {
					netcommFromRPI.remove(net.getName());
				}
				net.setResultStatus(net.getStatus());
				net.setStatus(NetStatus.UNLOADED);
				controlProtocol.unloadNetAsync(net.getName(), new ResultListener() {
					@Override
					public void onSuccess(String message) {
					}

					@Override
					public void onError(String message) {
					}
				});
				synchronized (netHandles) {
					netHandles.remove(net.getName());
				}
			}
			unloads.remove(net);
		}

		while (!schedules.isEmpty()) {
			final ScheduleParameters sp = schedules.get(0);
			RAPILogger.getLogger(this).fine("Rule " + sp.hashCode() + " being processed...");

			List<String> stop = new ArrayList<String>();
			List<String> cancel = new ArrayList<String>();
			List<String> start = new ArrayList<String>();
			String cond = createScheduleArgs(sp.results, sp.stopNets, sp.cancelNets, sp.startNets, stop, cancel, start,
					true);
			if (cond == null) {
				RAPILogger.getLogger(this).fine(schedules.size() + " schedules remaining.");
				return;
			} else if (cond.equals("f")) {
				RAPILogger.getLogger(this).fine("Rule " + sp.hashCode() + " talking about unloaded net.");
			} else {
				controlProtocol.scheduleNetsAsync(cond, stop, cancel, start, new DIOProtocol.ResultListener() {

					@Override
					public void onSuccess(String key) {
						RAPILogger.getLogger(this).fine("Rule " + sp.hashCode() + " successfully loaded...");
						scheduleDebugOutput(sp.results, sp.startNets, key);
						SoftRobotSynchronizationRule rule = new SoftRobotSynchronizationRule();
						rules.put(key, rule);
						try {
							dataProtocol.observeRuleAsync(key, new ResultListener() {
								@Override
								public void onSuccess(String result) {
								}

								@Override
								public void onError(String message) {
									if (sp.listener != null) {
										sp.listener.ruleStatusChanged(SynchronizationRuleStatus.INVALID);
									}
								}
							});
						} catch (RpiException e) {
							if (sp.listener != null) {
								sp.listener.ruleStatusChanged(SynchronizationRuleStatus.INVALID);
							}
						}
						if (sp.listener != null) {
							rule.addStatusListener(sp.listener);
						}
					}

					@Override
					public void onError(String message) {
						if (sp.listener != null) {
							sp.listener.ruleStatusChanged(SynchronizationRuleStatus.INVALID);
							RAPILogger.getLogger(this).fine("Rule " + sp.hashCode() + " failed: " + message);
						}
					}

				});
			}
			schedules.remove(sp);
		}
		RAPILogger.getLogger(this).fine("Ended processing schedules");
	}

	private SoftRobotWebsocketNetHandle nh(NetHandle handle) {
		return (SoftRobotWebsocketNetHandle) handle;
	}

	private RccNetResult nr(NetResult result) {
		return (RccNetResult) result;
	}

	private String createScheduleArgs(List<NetResult> results, List<NetHandle> stopNets, List<NetHandle> cancelNets,
			List<NetHandle> startNets, List<String> stop, List<String> cancel, List<String> start, boolean async)
			throws RpiException, NetStartException {
		for (NetHandle net : stopNets) {
			if (nh(net).isUnloaded()) {
				return "f";
			}
			if (async && net.getStatus() == NetStatus.LOADING) {
				RAPILogger.getLogger(this).fine("Net " + net.hashCode() + " not yet loaded");
				return null;
			}
			stop.add(net.getName());
		}

		for (NetHandle net : cancelNets) {
			if (nh(net).isUnloaded()) {
				return "f";
			}
			if (async && net.getStatus() == NetStatus.LOADING) {
				RAPILogger.getLogger(this).fine("Net " + net.hashCode() + " not yet loaded");
				return null;
			}
			cancel.add(net.getName());
		}

		for (NetHandle net : startNets) {
			if (nh(net).isUnloaded()) {
				return "f";
			}
			if (async && net.getStatus() == NetStatus.LOADING) {
				RAPILogger.getLogger(this).fine("Net " + net.hashCode() + " not yet loaded");
				return null;
			}
			start.add(net.getName());
		}

		String cond = "t";
		for (NetResult result : results) {
			if (result.getHandle().getStatus() == NetStatus.REJECTED) {
				throw new NetStartException("The previous net is rejected.");
			}
			if (nr(result).getHandle().isUnloaded() && "true".equals(nr(result).getNetcomm().getString())) {
				continue;
			} else if (nr(result).getHandle().isUnloaded()) {
				return "f";
			}
			if (async && result.getHandle().getStatus() == NetStatus.LOADING) {
				return null;
			}

			// if (result.getHandle().getStatus() == NetStatus.TERMINATED &&
			// result.getNetcomm() != null) {
			// if ("false".equals(result.getNetcomm().getString())) {
			// return "f";
			// } else {
			// // fine, result has been true
			// }
			// } else {
			cond += " & " + result.getHandle().getName() + ".out" + nr(result).getKey();
			// }
		}
		if (cond.length() > 1) {
			cond = cond.substring(4);
		}
		return cond;
	}

	private void scheduleDebugOutput(List<NetResult> results, List<NetHandle> startNets, String key) {
		String debug = "Schedule " + key + ": ";
		for (NetResult result : results) {
			debug += result.getHandle().getName() + "." + nr(result).getKey() + " ";
		}
		debug += ": ";
		for (NetHandle handle : startNets) {
			debug += handle.getName() + " ";
		}
		RAPILogger.getLogger(this).fine(debug);
	}

	protected String getNetXml(SoftRobotWebsocketNetHandle net) {
		HTTPResponse ret = SoftRobotHTTP.get(uri + "/nets/" + net.getName() + "/xml");
		if (ret != null) {
			return ret.getContent();
		}
		return null;
	}

	public void addNetcommFromRPI(SoftRobotWebsocketNetHandle net, final NetcommValue value) {
		synchronized (netcommFromRPI) {
			if (!netcommFromRPI.containsKey(net.getName())) {
				netcommFromRPI.put(net.getName(), new ConcurrentHashMap<String, NetcommValue>());
			}
			netcommFromRPI.get(net.getName()).put("out" + value.getName(), value);
		}
	}

	@Override
	public void checkBlockEventHandlerThread() throws RpiException {
		if (dataProtocol.isReaderThread() || poolThreads.contains(Thread.currentThread())) {
			throw new RpiException(
					"Illegal thread access - don't use blocking RAPI calls in synchronous EventHandlers / Listeners!");
		}
	}

	public void checkBlockControlThread() throws RpiException {
		if (controlProtocol.isReaderThread()) {
			throw new RpiException(
					"Illegal thread access - don't use blocking RAPI calls in synchronous EventHandlers / Listeners!");
		}
	}

	public void queueNetcommToRPI(Map<String, NetcommValue> values) throws RpiException {
		Map<String, Map<String, NetcommValue>> netcommToRPI = new HashMap<String, Map<String, NetcommValue>>();
		for (Map.Entry<String, NetcommValue> entry : values.entrySet()) {
			if (!netcommToRPI.containsKey(entry.getKey())) {
				netcommToRPI.put(entry.getKey(), new HashMap<String, NetcommValue>());
			}
			netcommToRPI.get(entry.getKey()).put("in" + entry.getValue().getName(), entry.getValue());
		}
		dataProtocol.writeNetcomm(netcommToRPI);
	}

	public void queueNetcommToRPI(String net, NetcommValue value) throws RpiException {
		Map<String, Map<String, NetcommValue>> netcommToRPI = new HashMap<String, Map<String, NetcommValue>>();
		netcommToRPI.put(net, new HashMap<String, NetcommValue>());
		netcommToRPI.get(net).put("in" + value.getName(), value);
		dataProtocol.writeNetcomm(netcommToRPI);
	}

	@Override
	public void setNetStatus(String net, NetStatus status) {
		RAPILogger.getLogger(this).fine("Status: " + net + ": " + status);
		synchronized (netHandles) {
			if (netHandles.containsKey(net)) {
				netHandles.get(net).setStatus(status);
				RAPILogger.getLogger(this).fine("Net " + netHandles.get(net).hashCode() + " ready.");
			}
		}
		try {
			if (status == NetStatus.READY) {
				processSchedules();
			}
		} catch (RpiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setNetcommValue(String net, String netcomm, final String value) {
		RAPILogger.getLogger(this).fine("Netcomm: " + net + "." + netcomm + " = " + value);
		synchronized (netcommFromRPI) {
			if (netcommFromRPI.containsKey(net) && netcommFromRPI.get(net).containsKey(netcomm)) {
				final NetcommValue nc = netcommFromRPI.get(net).get(netcomm);
				queueNotify(new Runnable() {
					@Override
					public void run() {
						nc.setString(value);
					}
				});
			}
		}
	}

	@Override
	public void notifyNetcommUpdated() {
		synchronized (netcommFromRPI) {
			for (Map<String, NetcommValue> netcomms : netcommFromRPI.values()) {
				for (final NetcommValue netcomm : netcomms.values()) {
					queueNotify(new Runnable() {
						@Override
						public void run() {
							netcomm.notifyUpdatePerformed();
						}
					});
				}
			}
		}
	}

	int queueNum = 0;

	public void queueNotify(final Runnable runnable) {
		notifierPool.execute(runnable);
	}

	private final Map<String, String> deviceTypes = new HashMap<>();
	private final Map<String, Map<String, RpiParameters>> deviceInterfaces = new HashMap<>();
	private final Map<String, DeviceStatus> deviceStatus = new HashMap<>();

	@Override
	public void deviceAdded(String device, String type, Map<String, RpiParameters> interfaces) {
		deviceTypes.put(device, type);
		deviceInterfaces.put(device, interfaces);
		for (DeviceListener listener : deviceListeners) {
			listener.deviceAdded(device, type, interfaces);
		}
	}

	@Override
	public void deviceStatusChanged(String device, DeviceStatus status) {
		deviceStatus.put(device, status);
		for (DeviceListener listener : deviceListeners) {
			listener.deviceStatusChanged(device, status);
		}
	}

	@Override
	public void deviceRemoved(String device) {
		deviceStatus.remove(device);
		deviceTypes.remove(device);
		deviceInterfaces.remove(device);
		for (DeviceListener listener : deviceListeners) {
			listener.deviceRemoved(device);
		}
	}

	@Override
	public Map<String, String> eval(Fragment fragment) throws RpiException {
		Map<String, String> ret = new HashMap<String, String>();
		String net = NetSerializer.serialize(fragment);
		Document xml = SoftRobotHTTP.postXML(uri + "eval/", "dionet=" + SoftRobotHTTP.encode(net));
		NodeList data = xml.getElementsByTagName("data");
		for (int i = 0; i < data.getLength(); i++) {
			String key = ((Element) data.item(i)).getAttribute("key");
			ret.put(key, data.item(i).getTextContent());
		}
		for (NetcommValue netcomm : fragment.getNetcommFromRPI()) {
			String key = "out" + netcomm.getName();
			if (ret.containsKey(key)) {
				netcomm.setString(ret.get(key));
			}
		}
		for (NetcommValue netcomm : fragment.getNetcommFromRPI()) {
			netcomm.notifyUpdatePerformed();
		}
		return ret;
	}

	@Override
	public void setRuleStatus(String rule, final SynchronizationRuleStatus status) {
		RAPILogger.getLogger(this).fine("Rule " + rule + ": " + status);
		final SoftRobotSynchronizationRule r = rules.get(rule);
		if (r != null) {
			queueNotify(new Runnable() {
				@Override
				public void run() {
					r.changeStatus(status);
				}
			});
		}
		if (status != SynchronizationRuleStatus.ACTIVE) {
			rules.remove(rule);
		}
	}

	@Override
	public void createDevice(String name, String type, RpiParameters parameters) {

		StringBuilder configString = new StringBuilder();

		configString.append("name=");
		configString.append(name);

		configString.append("&type=");
		configString.append(type);

		if (parameters != null) {
			for (String entry : parameters.keySet()) {
				configString.append("&");
				configString.append(entry);
				configString.append("=");
				configString.append(parameters.get(entry));
			}
		}
		String data = configString.toString();

		HTTPResponse response = SoftRobotHTTP.post(uri + "/devices/", data);

		if (response.getResponseCode() >= 400) {
			// TODO: something failed.
			return;
		}

		try {
			// wait up to five seconds for the device to become ready
			for (int i = 0; i < 100; i++) {
				if (deviceStatus.get(name) == DeviceStatus.OPERATIONAL
						|| deviceStatus.get(name) == DeviceStatus.SAFE_OPERATIONAL)
					return;
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
		}

	}

	@Override
	public void removeDevice(String name) {
		StringBuilder configString = new StringBuilder();

		configString.append("name=");
		configString.append(name);
		configString.append("&action=remove");

		HTTPResponse response = SoftRobotHTTP.post(uri + "/devices/", configString.toString());

		if (response == null || response.getResponseCode() >= 400) {
			// TODO: something failed.
			// return false;
		}
		// return true;
	}

}
