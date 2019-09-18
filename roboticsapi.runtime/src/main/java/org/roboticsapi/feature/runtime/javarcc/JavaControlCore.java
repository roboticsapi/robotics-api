/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.javarcc;

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
import org.roboticsapi.facet.javarcc.JCondition;
import org.roboticsapi.facet.javarcc.JFragmentOutPort;
import org.roboticsapi.facet.javarcc.JNet;
import org.roboticsapi.facet.javarcc.JNetCreator;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.javarcc.JSchedulingRule;
import org.roboticsapi.facet.javarcc.devices.DeviceRegistry;
import org.roboticsapi.facet.javarcc.devices.DeviceRegistry.DeviceRegistryListener;
import org.roboticsapi.facet.javarcc.devices.JDevice;
import org.roboticsapi.facet.javarcc.executor.SimpleExecutor;
import org.roboticsapi.facet.javarcc.extension.JavaRccExtensionPoint;
import org.roboticsapi.facet.runtime.rpi.ControlCore;
import org.roboticsapi.facet.runtime.rpi.DeviceListener;
import org.roboticsapi.facet.runtime.rpi.DeviceStatus;
import org.roboticsapi.facet.runtime.rpi.Fragment;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.NetResult;
import org.roboticsapi.facet.runtime.rpi.NetSynchronizationRule;
import org.roboticsapi.facet.runtime.rpi.NetSynchronizationRule.SynchronizationRuleListener;
import org.roboticsapi.facet.runtime.rpi.NetSynchronizationRule.SynchronizationRuleStatus;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.feature.runtime.javarcc.condition.JAndCondition;
import org.roboticsapi.feature.runtime.javarcc.condition.JTrueCondition;

public class JavaControlCore implements ControlCore, JavaRccExtensionPoint, DeviceRegistryListener {

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

	private SimpleExecutor executor = new SimpleExecutor(this, nonRejectingExecutor);
	private DeviceRegistry devices = new DeviceRegistry(this);
	private JNetCreator netCreator = new JNetCreator();

	private Map<String, DeviceFactory> deviceFactories = new HashMap<>();
	private Map<String, InterfaceExtractor> interfaces = new HashMap<>();
	private Map<String, JNetHandle> netHandles = new HashMap<>();
	private Map<String, JNet> nets = new HashMap<>();
	private Map<String, Fragment> fragments = new HashMap<>();
	int netNr = 0;
	String name;

	public JavaControlCore(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "JavaControlCore[" + name + "]";
	}

	@Override
	public JNetHandle load(Fragment root, String description, boolean realtime) throws RpiException {
		netNr++;
		String name = "rpinet" + netNr;
		JNet net = netCreator.createNet(name, description, root, devices, nonRejectingExecutor, nets);
		nets.put(name, net);
		fragments.put(name, root);
		JNetHandle handle = new JNetHandle(this, net, name);
		netHandles.put(name, handle);
		return handle;
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
	public void ping() throws RpiException {
	}

	@Override
	public void checkBlockEventHandlerThread() throws RpiException {
		executor.checkBlockEventHandlerThread();
	}

	@Override
	public Map<String, String> eval(Fragment fragment) throws RpiException {
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

	@Override
	public boolean start(List<NetHandle> handles) throws RpiException {
		JSchedulingRule rule = new JSchedulingRule(new JTrueCondition());
		for (NetHandle handle : handles)
			rule.starting(nh(handle).getNet());
		executor.addRule(rule);
		return true;
	}

	private JNetHandle nh(NetHandle handle) {
		return (JNetHandle) handle;
	}

	private JNetResult nr(NetResult result) {
		return (JNetResult) result;
	}

	@Override
	public NetSynchronizationRule schedule(List<NetResult> results, List<NetHandle> stopNets,
			List<NetHandle> cancelNets, List<NetHandle> startNets) throws RpiException {
		List<JCondition> conds = new ArrayList<JCondition>();
		conds.add(new JTrueCondition());
		for (NetResult result : results)
			conds.add(nr(result).getCondition());
		JSchedulingRule rule = new JSchedulingRule(new JAndCondition(conds));
		for (NetHandle cancel : cancelNets)
			rule.cancelling(nh(cancel).getNet());
		for (NetHandle stop : stopNets)
			rule.killing(nh(stop).getNet());
		for (NetHandle start : startNets)
			rule.starting(nh(start).getNet());
		executor.addRule(rule);
		return rule;
	}

	@Override
	public void schedule(List<NetResult> results, List<NetHandle> stopNets, List<NetHandle> cancelNets,
			List<NetHandle> startNets, SynchronizationRuleListener listener) throws RpiException {
		NetSynchronizationRule rule = schedule(results, stopNets, cancelNets, startNets);
		if (listener != null) {
			if (rule != null) {
				rule.addStatusListener(listener);
			} else {
				listener.ruleStatusChanged(SynchronizationRuleStatus.INVALID);
			}
		}
	}

	private List<DeviceListener> listeners = new ArrayList<DeviceListener>();

	@Override
	public void addDeviceListener(DeviceListener listener) {
		listeners.add(listener);
		for (Map.Entry<String, String> device : devices.list().entrySet()) {
			notifyAboutDevice(listener, device.getKey(), device.getValue(), devices.get(name, JDevice.class));
		}
	}

	@Override
	public void removeDeviceListener(DeviceListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void createDevice(String name, String type, RpiParameters parameters) {
		try {
			if (deviceFactories.containsKey(type)) {
				JDevice device = deviceFactories.get(type).create(parameters, devices);
				try {
					device.start();
				} catch (Exception e) {
					RAPILogger.logException(device, e);
					return;
				}
				devices.register(name, type, device);
			} else {
				RAPILogger.getLogger(this)
						.warning("Can not create device '" + name + "' of type '" + type + "': Unsupported type.");
			}
		} catch (Throwable e) {
			RAPILogger.getLogger(this)
					.warning("Can not create device '" + name + "' of type '" + type + "': " + e.getMessage());
		}
	}

	private void notifyAboutDevice(DeviceListener listener, String name, String type, JDevice device) {
		Map<String, RpiParameters> ifs = new HashMap<String, RpiParameters>();
		for (Entry<String, InterfaceExtractor> ife : interfaces.entrySet()) {
			RpiParameters params = ife.getValue().getInterfaceParameters(device);
			if (params != null)
				ifs.put(ife.getKey(), params);
		}
		listener.deviceAdded(name, type, ifs);
		listener.deviceStatusChanged(name, DeviceStatus.OPERATIONAL);
	}

	@Override
	public void removeDevice(String name) {
		JDevice device = devices.get(name, JDevice.class);
		if (device == null)
			return;
		devices.remove(name);
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
	public void registerInterface(String name, InterfaceExtractor extractor) {
		interfaces.put(name, extractor);
	}

	@Override
	public void deviceAdded(String name) {
		for (DeviceListener l : listeners) {
			notifyAboutDevice(l, name, devices.list().get(name), devices.get(name, JDevice.class));
		}
	}

	@Override
	public void deviceRemoved(String name) {
		for (DeviceListener l : listeners) {
			l.deviceRemoved(name);
		}
	}

	@Override
	public void deviceChanged(String name) {
		for (DeviceListener l : listeners) {
			notifyAboutDevice(l, name, devices.list().get(name), devices.get(name, JDevice.class));
		}
	}

	public void unloadNet(JNetHandle net) {
		String name = net.getName();
		net.getNet().unload();
		fragments.remove(name);
		netHandles.remove(name);
		nets.remove(name);
	}
}
