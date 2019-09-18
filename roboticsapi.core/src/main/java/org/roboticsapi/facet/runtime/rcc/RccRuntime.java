/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rcc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.NotPresentException;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.WaitCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.WritableRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.ConfiguredRcc;
import org.roboticsapi.facet.runtime.rpi.ControlCore;
import org.roboticsapi.facet.runtime.rpi.DeviceListener;
import org.roboticsapi.facet.runtime.rpi.DeviceStatus;
import org.roboticsapi.facet.runtime.rpi.Fragment;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.mapping.CommandFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiDeviceListener;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiDeviceStatusListener;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiRuntime;

/**
 * A connector to a robot control core
 */
public class RccRuntime extends RpiRuntime implements DeviceListener {

	private final Set<String> registeredExtensions = new HashSet<String>();

	private final WritableRealtimeDouble override = RealtimeDouble.createWritable(1);

	private final Logger logger = Logger.getLogger("org.roboticsapi.runtime.softrobot");

	private final Dependency<ConfiguredRcc> rcc;

	/**
	 * Creates a new Robot control core connector
	 *
	 * @throws RoboticsException if an error occurs
	 */
	public RccRuntime() {
		rcc = createDependency("Rcc");
	}

	public RccRuntime(ConfiguredRcc rcc) {
		this();
		setRcc(rcc);
	}

	@ConfigurationProperty
	public void setRcc(ConfiguredRcc controlCore) {
		this.rcc.set(controlCore);
	}

	public ConfiguredRcc getRcc() {
		return this.rcc.get();
	}

	private Thread t;
	private boolean shutdown = false;

	@Override
	protected void afterInitialization() {
		super.afterInitialization();
		rcc.get().getControlCore().addDeviceListener(this);

		t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!shutdown) {
					checkConnection();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		t.setName("RCCRuntime auto-reconnect");
		t.setDaemon(true);
		t.start();
	}

	@Override
	protected void beforeUninitialization() {
		shutdown = true;
		t.interrupt();
		while (true) {
			try {
				t.join();
				break;
			} catch (InterruptedException e) {
			}
		}
		rcc.get().getControlCore().removeDeviceListener(this);
		super.beforeUninitialization();
	}

	@Override
	public void addExtension(final String extensionId) {
		synchronized (registeredExtensions) {
			registeredExtensions.add(extensionId);
			logger.log(Level.INFO, "Extension registered: " + extensionId);
		}
	}

	public void removeExtension(final String extensionId) {
		synchronized (registeredExtensions) {
			registeredExtensions.remove(extensionId);
			logger.log(Level.INFO, "Extension unregistered: " + extensionId);
		}
	}

	@Override
	public Set<String> getRegisteredExtensions() throws RoboticsException {
		synchronized (registeredExtensions) {
			return Collections.unmodifiableSet(registeredExtensions);
		}
	}

	@Override
	public Set<String> getAvailableExtensions() throws RoboticsException {
		return registeredExtensions;
	}

	@Override
	public boolean isAvailable(String extensionId) throws RoboticsException {
		return getRegisteredExtensions().contains(extensionId);
	}

	@Override
	protected synchronized boolean checkPresent() {
		try {
			rcc.get().getControlCore().ping();
			return true;
		} catch (RpiException ex) {
			return false;
		}
	}

	@Override
	protected void onPresent() {
		goOperational();
	}

	@Override
	protected void onAbsent() {
		super.onAbsent();

		deviceStatus.clear();

		for (String deviceName : deviceStatusListeners.keySet()) {
			deviceStatusChanged(deviceName, deviceStatus.get(deviceName));
		}
	}

	@Override
	public double getOverride() {
		return override.getCheapValue();
	}

	@Override
	@ConfigurationProperty
	public void setOverride(final double newOverride) {
		if (newOverride < 0 || newOverride > 1) {
			throw new IllegalArgumentException("Override must be in range [0..1]");
		}
		override.setValue(newOverride);
	}

	private final MapperRegistry mapperRegistry = new MapperRegistry();

	@Override
	public MapperRegistry getMapperRegistry() {
		return mapperRegistry;
	}

	@Override
	public NetHandle loadFragment(Fragment net, String description, boolean realtime) throws RoboticsException {
		ControlCore controlCore = this.rcc.get().getControlCore();
		if (controlCore == null) {
			throw new NotPresentException("Runtime is not online");
		}

		// load net
		final NetHandle netHandle;
		try {
			netHandle = controlCore.load(net, description, realtime);
		} catch (final RpiException e) {
			throw new CommandException(e.getMessage(), e);
		}

		return netHandle;
	}

	@Override
	public WaitCommand createWaitCommand(String name) {
		return new RccWaitCommand(name, this);
	}

	@Override
	public ControlCore getControlCore() {
		return rcc.get().getControlCore();
	}

	@Override
	public WaitCommand createWaitCommand(String name, double duration) {
		return new RccWaitCommand(name, this, duration);
	}

	@Override
	public WaitCommand createWaitCommand(String name, RealtimeBoolean waitFor) {
		return new RccWaitCommand(name, this, waitFor);
	}

	@Override
	public RuntimeCommand createRuntimeCommandInternal(ActuatorDriver actuator, Action action,
			DeviceParameterBag parameters) throws RoboticsException {
		return new RccRuntimeCommand(action, actuator, this, parameters);
	}

	@Override
	public void checkBlockEventHandlerThread() throws RoboticsException {
		try {
			rcc.get().getControlCore().checkBlockEventHandlerThread();
		} catch (RpiException e) {
			throw new RoboticsException(e);
		}
	}

	Map<String, List<RpiDeviceStatusListener>> deviceStatusListeners = new HashMap<String, List<RpiDeviceStatusListener>>();
	Map<String, List<RpiDeviceListener>> deviceListeners = new HashMap<String, List<RpiDeviceListener>>();

	public void addDeviceListener(String type, RpiDeviceListener listener) {
		if (!deviceListeners.containsKey(type)) {
			deviceListeners.put(type, new ArrayList<RpiDeviceListener>());
		}
		deviceListeners.get(type).add(listener);
		for (Map.Entry<String, String> entry : deviceType.entrySet()) {
			if (entry.getValue().equals(type)) {
				listener.deviceAdded(entry.getKey());
			}
		}
	}

	@Override
	public void addDeviceStatusListener(String deviceName, RpiDeviceStatusListener listener) {
		if (!deviceStatusListeners.containsKey(deviceName)) {
			deviceStatusListeners.put(deviceName, new ArrayList<RpiDeviceStatusListener>());
		}
		deviceStatusListeners.get(deviceName).add(listener);

		String type = deviceType.get(deviceName);
		if (type != null) {
			listener.isPresent(type, deviceInterfaces.get(deviceName));
			if (deviceStatus.get(deviceName) != null) {
				notifyStatusListener(listener, deviceStatus.get(deviceName));
			}
		} else {
			listener.isAbsent();
		}
	}

	@Override
	public void removeDeviceStatusListener(String deviceName, RpiDeviceStatusListener listener) {
		if (!deviceStatusListeners.containsKey(deviceName)) {
			return;
		}
		deviceStatusListeners.get(deviceName).remove(listener);
	}

	Map<String, DeviceStatus> deviceStatus = new HashMap<String, DeviceStatus>();
	Map<String, String> deviceType = new HashMap<String, String>();
	Map<String, Map<String, RpiParameters>> deviceInterfaces = new HashMap<String, Map<String, RpiParameters>>();

	@Override
	public void deviceAdded(String device, String type, Map<String, RpiParameters> interfaces) {
		deviceType.put(device, type);
		deviceInterfaces.put(device, interfaces);
		if (deviceListeners.containsKey(type)) {
			for (RpiDeviceListener listener : new ArrayList<RpiDeviceListener>(deviceListeners.get(type))) {
				listener.deviceAdded(device);
			}
		}
		if (deviceStatusListeners.containsKey(device)) {
			for (RpiDeviceStatusListener listener : new ArrayList<RpiDeviceStatusListener>(
					deviceStatusListeners.get(device))) {
				listener.isPresent(type, interfaces);
			}
		}
	}

	@Override
	public void deviceStatusChanged(String device, DeviceStatus status) {
		deviceStatus.put(device, status);
		if (deviceStatusListeners.containsKey(device)) {
			for (RpiDeviceStatusListener listener : new ArrayList<RpiDeviceStatusListener>(
					deviceStatusListeners.get(device))) {
				notifyStatusListener(listener, status);
			}
		}
	}

	private void notifyStatusListener(RpiDeviceStatusListener listener, DeviceStatus status) {
		if (status == null) {
			listener.isAbsent();
		} else {
			switch (status) {
			case OFFLINE:
				listener.isOffline();
				break;
			case OPERATIONAL:
				listener.isOperational();
				break;
			case SAFE_OPERATIONAL:
				listener.isSafeOperational();
				break;
			}
		}
	}

	@Override
	public void deviceRemoved(String device) {
		deviceStatus.remove(device);
		deviceType.remove(device);
		deviceInterfaces.remove(device);
		if (deviceStatusListeners.containsKey(device)) {
			for (RpiDeviceStatusListener listener : new ArrayList<RpiDeviceStatusListener>(
					deviceStatusListeners.get(device))) {
				notifyStatusListener(listener, null);
			}
		}
	}

	@Override
	public <T> T getRealtimeValue(RealtimeValue<T> value) throws RealtimeValueReadException {
		try {
			final List<T> retList = new ArrayList<T>();
			CommandFragment fragment = new CommandFragment(createWaitCommand());
			fragment.addRealtimeValueSource(getMapperRegistry());
			fragment.addObserver(value.createObserver(new RealtimeValueListener<T>() {
				@Override
				public void onValueChanged(T newValue) {
					retList.add(newValue);
				}
			}, null, false));

			rcc.get().getControlCore().eval(fragment);

			if (retList.isEmpty()) {
				return null;
			} else {
				return retList.get(0);
			}
		} catch (RoboticsException e) {
			throw new RealtimeValueReadException(e);
		} catch (RpiException e) {
			throw new RealtimeValueReadException(e);
		}
	}

	@Override
	public RealtimeDouble getOverrideSensor() {
		return override;
	}

}