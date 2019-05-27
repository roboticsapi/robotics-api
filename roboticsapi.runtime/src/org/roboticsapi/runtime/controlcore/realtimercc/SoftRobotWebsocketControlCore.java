/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.controlcore.realtimercc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.runtime.controlcore.SoftRobotDeviceListener;
import org.roboticsapi.runtime.controlcore.SoftRobotDeviceStatus;
import org.roboticsapi.runtime.controlcore.SoftRobotDirectIONet;
import org.roboticsapi.runtime.controlcore.SoftRobotHTTP;
import org.roboticsapi.runtime.controlcore.SoftRobotHTTP.HTTPResponse;
import org.roboticsapi.runtime.controlcore.dioprotocol.DIOProtocol.ProtocolCallback;
import org.roboticsapi.runtime.rpi.ControlCore;
import org.roboticsapi.runtime.rpi.Fragment;
import org.roboticsapi.runtime.rpi.NetHandle;
import org.roboticsapi.runtime.rpi.NetStartException;
import org.roboticsapi.runtime.rpi.NetStatus;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.RPIException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
	private final List<SoftRobotDeviceListener> deviceListeners = new ArrayList<SoftRobotDeviceListener>();
	private final Hashtable<String, SoftRobotWebsocketNetHandle> netHandles = new Hashtable<String, SoftRobotWebsocketNetHandle>();
	private final Map<String, Map<String, NetcommValue>> netcommFromRPI = new ConcurrentHashMap<String, Map<String, NetcommValue>>();

	/** URI to access the SoftRobot RCC */
	private final String uri;
	private WebsocketProtocol dataProtocol, controlProtocol;
	private String appName = null;
	private String session = null;
	private double frequency = 0.05;
	private final Object sessionLock = new Object();

	/**
	 * Creates a new SoftRobot control core
	 *
	 * @param uri HTTP URI to access the SoftRobot control core
	 * @throws RoboticsException if an error occurs
	 */
	public SoftRobotWebsocketControlCore(String uri) throws RPIException {
		if (!uri.endsWith("/")) {
			uri += "/";
		}
		this.uri = uri;
		appName = getAppName();
		startSession();
	}

	/**
	 * Creates a new SoftRobot control core
	 *
	 * @param uri HTTP URI to access the SoftRobot control core
	 * @throws RoboticsException if an error occurs
	 */
	public SoftRobotWebsocketControlCore(String uri, SoftRobotDeviceListener listener) throws RPIException {
		if (!uri.endsWith("/")) {
			uri += "/";
		}
		addDeviceListener(listener);
		this.uri = uri;
		appName = getAppName();
		startSession();
	}

	/**
	 * Creates a new SoftRobot control core
	 *
	 * @param uri       HTTP URI to access the SoftRobot control core
	 * @param frequency update frequency in seconds
	 * @throws RoboticsException if an error occurs
	 */
	public SoftRobotWebsocketControlCore(String uri, double frequency) throws RPIException {
		this(uri);
		this.frequency = frequency;
	}

	public void addDeviceListener(SoftRobotDeviceListener listener) {
		deviceListeners.add(listener);
	}

	@Override
	public void ping() throws RPIException {
		try {
			controlProtocol.ping();
		} catch (IOException e) {
			if (!reconnect()) {
				throw new RPIException(e);
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
			dataProtocol.observeDevices();
		} catch (RPIException e) {
			return false;
		}
		return true;
	}

	@Override
	public void shutdown() {
		cleanupProtocol();
		notifierPool.shutdown();
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
	public NetHandle load(final Fragment fragment, String description) throws RPIException {
		checkBlockControlThread();

		synchronized (sessionLock) {
			if (session == null) {
				startSession();
			}
		}

		String netName = controlProtocol.createNet(fragment, session, description.replace('|', ' '));

		final SoftRobotWebsocketNetHandle netHandle = new SoftRobotWebsocketNetHandle(this, netName);
		for (final NetcommValue netcomm : fragment.getNetcommFromRPI()) {
			addNetcommFromRPI(netHandle, netcomm);
		}
		for (final NetcommValue netcomm : fragment.getNetcommToRPI()) {
			netHandle.addNetcommToRPI(netcomm);
		}

		netHandle.setNet(fragment);
		synchronized (netHandles) {
			netHandles.put(netName, netHandle);
			observedNets.add(netHandle.getName());
		}
		dataProtocol.observeNet(netHandle.getName(), frequency);

		return netHandle;
	}

	private final List<String> observedNets = new ArrayList<String>();

	private void startSession() throws RPIException {
		synchronized (sessionLock) {
			cleanupSession();
			cleanupProtocol();
			observedNets.clear();
			setupProtocol();
			setupSession();
			dataProtocol.observeDevices();
		}
	}

	private void setupSession() throws RPIException {
		String desc = appName + "@" + getHostName();
		session = controlProtocol.createSession(desc);
	}

	private void setupProtocol() throws RPIException {
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
		return new Vector<NetHandle>(netHandles.values());
	}

	protected boolean startNet(SoftRobotWebsocketNetHandle net) throws RPIException {
		checkBlockControlThread();

		if (net.getStatus() != NetStatus.READY) {
			throw new NetStartException("Net is not ready (Net status: " + net.getStatus() + ")");
		}

		if (!controlProtocol.startNet(net.getName())) {
			throw new NetStartException(
					"Could not start net, though it was ready. Probably some net resources are unavailable.");
		}

		net.setStatus(NetStatus.RUNNING);
		return true;
	}

	protected boolean abortNet(SoftRobotWebsocketNetHandle net) throws RPIException {
		final NetStatus status = net.getStatus();
		if (status != NetStatus.RUNNING && status != NetStatus.CANCELLING) {
			return false;
		}

		return controlProtocol.abortNet(net.getName());
	}

	protected boolean cancelNet(SoftRobotWebsocketNetHandle net) throws RPIException {
		return controlProtocol.cancelNet(net.getName());
	}

	protected void unloadNet(SoftRobotWebsocketNetHandle net) throws RPIException {
		synchronized (netcommFromRPI) {
			netcommFromRPI.remove(net.getName());
		}
		net.setResultStatus(net.getStatus());

		controlProtocol.unloadNet(net.getName());
		synchronized (netHandles) {
			netHandles.remove(net.getName());
			observedNets.remove(net.getName());
		}
	}

	protected boolean scheduleNet(SoftRobotWebsocketNetHandle net, NetHandle previousNet) throws RPIException {
		checkBlockControlThread();

		if (net.getStatus() != NetStatus.READY) {
			throw new NetStartException("Net is not ready (Net status: " + net.getStatus().toString() + ")");
		}

		if (previousNet.getStatus() == NetStatus.REJECTED) {
			throw new NetStartException("The previous net is rejected.");
		}

		if (!controlProtocol.scheduleNet(net.getName(), ((SoftRobotWebsocketNetHandle) previousNet).getName())) {
			throw new NetStartException(
					"Could not schedule net, though it was ready. Probably some net resources are unavailable.");
		}

		return true;
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
	public void checkBlockEventHandlerThread() throws RPIException {
		if (dataProtocol.isReaderThread() || poolThreads.contains(Thread.currentThread())) {
			throw new RPIException(
					"Illegal thread access - don't use blocking RAPI calls in synchronous EventHandlers / Listeners!");
		}
	}

	public void checkBlockControlThread() throws RPIException {
		if (controlProtocol.isReaderThread()) {
			throw new RPIException(
					"Illegal thread access - don't use blocking RAPI calls in synchronous EventHandlers / Listeners!");
		}
	}

	public void queueNetcommToRPI(Map<String, NetcommValue> values) throws RPIException {
		Map<String, Map<String, NetcommValue>> netcommToRPI = new HashMap<String, Map<String, NetcommValue>>();
		for (Map.Entry<String, NetcommValue> entry : values.entrySet()) {
			if (!netcommToRPI.containsKey(entry.getKey())) {
				netcommToRPI.put(entry.getKey(), new HashMap<String, NetcommValue>());
			}
			netcommToRPI.get(entry.getKey()).put("in" + entry.getValue().getName(), entry.getValue());
		}
		dataProtocol.writeNetcomm(netcommToRPI);
	}

	public void queueNetcommToRPI(String net, NetcommValue value) throws RPIException {
		Map<String, Map<String, NetcommValue>> netcommToRPI = new HashMap<String, Map<String, NetcommValue>>();
		netcommToRPI.put(net, new HashMap<String, NetcommValue>());
		netcommToRPI.get(net).put("in" + value.getName(), value);
		dataProtocol.writeNetcomm(netcommToRPI);
	}

	@Override
	public void setNetStatus(String net, NetStatus status) {
		synchronized (netHandles) {
			if (netHandles.containsKey(net)) {
				netHandles.get(net).setStatus(status);
			}
		}
	}

	@Override
	public void setNetcommValue(String net, String netcomm, final String value) {
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

	public void queueNotify(Runnable runnable) {
		notifierPool.execute(runnable);
	}

	@Override
	public void deviceAdded(String device, String type, List<String> interfaces) {
		for (SoftRobotDeviceListener listener : deviceListeners) {
			listener.deviceAdded(device, type, interfaces);
		}
	}

	@Override
	public void deviceStatusChanged(String device, SoftRobotDeviceStatus status) {
		for (SoftRobotDeviceListener listener : deviceListeners) {
			listener.deviceStatusChanged(device, status);
		}
	}

	@Override
	public void deviceRemoved(String device) {
		for (SoftRobotDeviceListener listener : deviceListeners) {
			listener.deviceRemoved(device);
		}
	}

	@Override
	public Map<String, String> eval(Fragment fragment) throws RPIException {
		Map<String, String> ret = new HashMap<String, String>();
		String net = SoftRobotDirectIONet.toNewFormat(fragment);
		Document xml = SoftRobotHTTP.postXML(uri + "eval/", "dionet=" + SoftRobotHTTP.encode(net));
		NodeList data = xml.getElementsByTagName("data");
		for (int i = 0; i < data.getLength(); i++) {
			String key = ((Element) data.item(i)).getAttribute("key");
			ret.put(key, data.item(i).getTextContent());
		}
		return ret;
	}

	@Override
	public List<String> getDeviceInterfaces(String deviceName) {
		Document xml = SoftRobotHTTP.getXML(uri + "devices/");
		List<String> ret = new ArrayList<String>();
		if (xml == null) {
			return ret;
		}
		NodeList devices = xml.getDocumentElement().getChildNodes();
		for (int i = 0; i < devices.getLength(); i++) {
			Node device = devices.item(i);
			if (device instanceof Element && ((Element) device).getAttribute("name").equals(deviceName)) {
				NodeList interfaces = device.getChildNodes();
				for (int j = 0; j < interfaces.getLength(); j++) {
					Node node = interfaces.item(j);
					if (node instanceof Element && "interface".equals(node.getNodeName())) {
						ret.add(((Element) node).getAttribute("name"));
					}
				}
			}
		}
		return ret;
	}

	@Override
	public boolean createDevice(String deviceName, String deviceType, Map<String, String> parameters) {

		StringBuilder configString = new StringBuilder();

		configString.append("name=");
		configString.append(deviceName);

		configString.append("&type=");
		configString.append(deviceType);

		if (parameters != null) {
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				configString.append("&");
				configString.append(entry.getKey());
				configString.append("=");
				configString.append((entry.getValue()));
			}
		}
		String data = configString.toString();

		RAPILogger.getLogger().log(RAPILogger.DEBUGLEVEL, "Loading driver: " + data);

		HTTPResponse response = SoftRobotHTTP.post(uri + "devices/", data);

		if (response.getResponseCode() >= 400) {
			RAPILogger.getLogger().warning("Loading of device failed: " + deviceName);
			return false;
		}
		RAPILogger.getLogger().fine("Device loaded: " + deviceName);

		return true;

	}

	@Override
	public boolean deleteDevice(String deviceName) {
		StringBuilder configString = new StringBuilder();

		configString.append("name=");
		configString.append(deviceName);
		configString.append("&action=remove");

		HTTPResponse response = SoftRobotHTTP.post(uri + "devices/", configString.toString());

		if (response == null || response.getResponseCode() >= 400) {
			return false;
		}
		return true;
	}

	@Override
	public boolean loadExtension(String extensionId) {
		HTTPResponse response = SoftRobotHTTP.post(uri + "/extensions/", "extension=" + extensionId);

		if (response == null) { // TODO: this sometimes happens when executing
			// rapi application with fatjar on android
			RAPILogger.getLogger().warning("Extension response code is null: " + extensionId);
			return false;
		} else if (response.getResponseCode() >= 400) {
			RAPILogger.getLogger().warning("Extension refused: " + extensionId);
			return false;
		} else if (response.getResponseCode() >= 300) {
			RAPILogger.getLogger().fine("Extension already loaded: " + extensionId);
			return false;
		} else {
			RAPILogger.getLogger().fine("Extension loaded: " + extensionId);
			return true;
		}
	}
}
