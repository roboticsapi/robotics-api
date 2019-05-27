/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.controlcore.javarcc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.runtime.controlcore.SoftRobotDeviceListener;
import org.roboticsapi.runtime.controlcore.SoftRobotDeviceStatus;
import org.roboticsapi.runtime.core.javarcc.devices.DeviceRegistry;
import org.roboticsapi.runtime.core.javarcc.devices.JDevice;
import org.roboticsapi.runtime.core.javarcc.devices.DeviceRegistry.DeviceRegistryListener;
import org.roboticsapi.runtime.javarcc.JFragmentOutPort;
import org.roboticsapi.runtime.javarcc.JNet;
import org.roboticsapi.runtime.javarcc.JNetCreator;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.javarcc.executor.SimpleExecutor;
import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtensionPoint;
import org.roboticsapi.runtime.rpi.ControlCore;
import org.roboticsapi.runtime.rpi.Fragment;
import org.roboticsapi.runtime.rpi.NetHandle;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotJavaControlCore implements ControlCore, JavaRCCExtensionPoint, DeviceRegistryListener {

	private ThreadPoolExecutor netcommExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>());

	private Executor nonRejectingExecutor = new Executor() {
		@Override
		public void execute(Runnable command) {
			while (!netcommExecutor.isShutdown()) {
				try {
					netcommExecutor.execute(command);
					return;
				} catch (RejectedExecutionException e) {
				}
			}
		}
	};

	private SimpleExecutor executor = new SimpleExecutor(this);
	private DeviceRegistry devices = new DeviceRegistry(this);
	private JNetCreator netCreator = new JNetCreator();

	private Map<String, DeviceFactory> deviceFactories = new HashMap<>();
	private Map<String, Class<? extends JDevice>> interfaces = new HashMap<>();
	private Map<String, SoftRobotJavaNetHandle> netHandles = new HashMap<>();
	private Map<String, JNet> nets = new HashMap<>();
	private Map<String, Fragment> fragments = new HashMap<>();
	int netNr = 0;

	private SoftRobotDeviceListener deviceListener;

	public SoftRobotJavaControlCore(SoftRobotDeviceListener listener) {
		this.deviceListener = listener;
	}

	@Override
	public String toString() {
		return "JavaControlCore";
	}

	@Override
	public List<NetHandle> getNetHandles() {
		return new ArrayList<NetHandle>(netHandles.values());
	}

	@Override
	public void shutdown() {
		executor.shutdown();
		netcommExecutor.shutdown();
	}

	@Override
	public void ping() throws RPIException {
	}

	@Override
	public void checkBlockEventHandlerThread() throws RPIException {
		executor.checkBlockEventHandlerThread();
	}

	@Override
	public Map<String, String> eval(Fragment fragment) throws RPIException {
		Map<String, String> ret = new HashMap<String, String>();
		JNet net = netCreator.createNet("eval", "eval", fragment, devices, task -> task.run(), nets);
		net.readSensor();
		net.updateData();
		net.sendNetcomm();

		for (OutPort o : fragment.getOutPorts()) {
			ret.put(o.getName(),
					((JFragmentOutPort<?>) net.getFragment().getOutPort(o.getName())).getInnerPort().get().toString());
		}

		for (NetcommValue v : fragment.getNetcommFromRPI()) {
			v.notifyUpdatePerformed();
		}
		return ret;
	}

	private SoftRobotJavaNetHandle nh(NetHandle handle) {
		return (SoftRobotJavaNetHandle) handle;
	}

	@Override
	public void registerPrimitive(String name, Class<? extends JPrimitive> primitive) {
		netCreator.reg(name, primitive);
	}

	@Override
	public void registerDevice(String type, DeviceFactory factory) {
		deviceFactories.put(type, factory);
	}

	@Override
	public void registerInterface(String name, Class<? extends JDevice> extractor) {
		interfaces.put(name, extractor);
	}

	public void unloadNet(SoftRobotJavaNetHandle net) {
		String name = net.getName();
		net.getNet().unload();
		fragments.remove(name);
		netHandles.remove(name);
		nets.remove(name);
	}

	@Override
	public NetHandle load(Fragment root, String description) throws RPIException {
		netNr++;
		String name = "rpinet" + netNr;
		JNet net = netCreator.createNet(name, description, root, devices, nonRejectingExecutor, nets);
		nets.put(name, net);
		fragments.put(name, root);
		SoftRobotJavaNetHandle handle = new SoftRobotJavaNetHandle(this, net, name);
		netHandles.put(name, handle);
		return handle;
	}

	@Override
	public void deviceAdded(String name) {
		notifyAboutDevice(name, devices.list().get(name), devices.get(name, JDevice.class));
	}

	@Override
	public void deviceRemoved(String name) {
		deviceListener.deviceRemoved(name);
	}

	@Override
	public void deviceChanged(String name) {
		notifyAboutDevice(name, devices.list().get(name), devices.get(name, JDevice.class));
	}

	private void notifyAboutDevice(String name, String type, JDevice device) {
		List<String> ifs = new ArrayList<>();
		for (Entry<String, Class<? extends JDevice>> ife : interfaces.entrySet()) {
			if(ife.getValue().isAssignableFrom(device.getClass()))
				ifs.add(ife.getKey());
		}
		deviceListener.deviceAdded(name, type, ifs);
		deviceListener.deviceStatusChanged(name, SoftRobotDeviceStatus.OPERATIONAL);
	}

	protected boolean start(SoftRobotJavaNetHandle netHandle) {
		return executor.schedule(netHandle, null);
	}

	protected boolean schedule(SoftRobotJavaNetHandle netHandle, NetHandle after) {
		return executor.schedule(netHandle, nh(after));
	}

	@Override
	public List<String> getDeviceInterfaces(String deviceName) {
		List<String> ifs = new ArrayList<>();
		for (Entry<String, Class<? extends JDevice>> ife : interfaces.entrySet()) {
			if(ife.getValue().isAssignableFrom(devices.get(deviceName, JDevice.class).getClass()))
				ifs.add(ife.getKey());
		}
		return ifs;
	}

	@Override
	public boolean createDevice(String deviceName, String deviceType, Map<String, String> parameters) {
		try {
			if (deviceFactories.containsKey(deviceType)) {
				JDevice device = deviceFactories.get(deviceType).create(parameters, devices);
				try {
					device.start();
				} catch (Exception e) {
					RAPILogger.getLogger().warning(device + ": " + e);
					return false;
				}
				devices.register(deviceName, deviceType, device);
				return true;
			} else {
				RAPILogger.getLogger().warning(
						"Can not create device '" + deviceName + "' of type '" + deviceType + "': Unsupported type.");
			}
		} catch (Throwable e) {
			RAPILogger.getLogger().warning(
					"Can not create device '" + deviceName + "' of type '" + deviceType + "': " + e.getMessage());
		}
		return false;
	}

	@Override
	public boolean deleteDevice(String name) {
		JDevice device = devices.get(name, JDevice.class);
		if (device == null)
			return false;
		devices.remove(name);
		return true;
	}

	@Override
	public boolean loadExtension(String extensionId) {
		// nothing to do here...
		return true;
	}

}
