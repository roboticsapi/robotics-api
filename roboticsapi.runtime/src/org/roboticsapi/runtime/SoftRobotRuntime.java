/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.CommandStatusListener;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.NotPresentException;
import org.roboticsapi.core.OnlineObject;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.runtime.controlcore.SoftRobotDeviceListener;
import org.roboticsapi.runtime.controlcore.SoftRobotDeviceStatus;
import org.roboticsapi.runtime.controlcore.SoftRobotHTTP;
import org.roboticsapi.runtime.controlcore.javarcc.SoftRobotJavaControlCore;
import org.roboticsapi.runtime.controlcore.realtimercc.SoftRobotWebsocketControlCore;
import org.roboticsapi.runtime.core.javarcc.devices.JDevice;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtensionPoint;
import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MapperRegistry;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.rpi.ControlCore;
import org.roboticsapi.runtime.rpi.Fragment;
import org.roboticsapi.runtime.rpi.NetHandle;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.RPIException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A connector to the SoftRobot RCC
 */
public class SoftRobotRuntime extends AbstractMapperRuntime implements SoftRobotDeviceListener, JavaRCCExtensionPoint {

	/** URI to access the SoftRobot RCC */
	private String uri;
	private final List<String> availableModules = new ArrayList<String>();

	private final Set<String> registeredExtensions = new HashSet<String>();

	private double override = 1;

	private final List<SoftRobotCommandHandle> commands = new Vector<SoftRobotCommandHandle>();

	private ControlCore controlCore;
	private final Logger logger = RAPILogger.getLogger();

	// Logger.getLogger("org.roboticsapi.runtime.softrobot");

	/**
	 * Creates a new SoftRobot RCC connector
	 *
	 * @param uri  HTTP URI to access the SoftRobot RCC
	 * @param name
	 * @throws RoboticsException if an error occurs
	 */
	public SoftRobotRuntime(final String uri, String name) {
		setName(name);
		this.uri = uri;
	}

	public SoftRobotRuntime() {
		setName("SoftRobotRuntime");
		setUri(null);
	}

	@Override
	public void addExtension(final String extensionId) {
		synchronized (registeredExtensions) {
			registeredExtensions.add(extensionId);
			logger.log(RAPILogger.DEBUGLEVEL, "Extension registered: " + extensionId);

			if (getState() == OperationState.OPERATIONAL) {
				loadExtension(extensionId);
			}
		}
	}

	public void removeExtension(final String extensionId) {
		synchronized (registeredExtensions) {
			registeredExtensions.remove(extensionId);
			logger.log(RAPILogger.DEBUGLEVEL, "Extension unregistered: " + extensionId);
		}
	}

	private boolean loadExtension(String extensionId) {
		return controlCore.loadExtension(extensionId);
	}

	@Override
	public Set<String> getRegisteredExtensions() throws RoboticsException {
		synchronized (registeredExtensions) {
			return Collections.unmodifiableSet(registeredExtensions);
		}
	}

	@ConfigurationProperty
	public void setUri(String uri) {
		immutableWhenInitialized();
		this.uri = uri;
	}

	@Override
	public <T> T getSensorValue(Sensor<T> sensor) throws RoboticsException {
		final List<T> ret = new ArrayList<T>();
		try {
			NetFragment fragment = new NetFragment("Read sensor");
			SensorMapperResult<T> result = getMapperRegistry().mapSensor(this, sensor, fragment,
					new SensorMappingContext());
			result.addListener(null, new SensorListener<T>() {
				@Override
				public void onValueChanged(T newValue) {
					ret.add(newValue);
				}
			});

			fragment.buildLinks();
			Fragment frag = new Fragment();
			fragment.addToFragment(frag);
			frag.correctLinks();
			Map<String, String> results = controlCore.eval(frag);
			for (NetcommValue nc : frag.getNetcommFromRPI()) {
				if (results.containsKey("out" + nc.getName())) {
					nc.setString(results.get("out" + nc.getName()));
				}
			}
			for (NetcommValue nc : frag.getNetcommFromRPI()) {
				nc.notifyUpdatePerformed();
			}

			if (ret.isEmpty()) {
				return null;
			} else {
				return ret.get(0);
			}
		} catch (MappingException e) {
			throw new SensorReadException(e);
		} catch (RPIException e) {
			throw new SensorReadException(e);
		}
	}

	@Override
	protected synchronized boolean checkPresent() {
		if (controlCore != null) {
			try {
				controlCore.ping();
				return true;
			} catch (RPIException ex) {
				// controlCore.shutdown();
				// controlCore = null;
				// setPresent(false);
				return false;
			}
		} else {

			if (uri == null) {
				controlCore = new SoftRobotJavaControlCore(this);

			} else {

				String url = SoftRobotHTTP.getFinalUri(uri, 100);
				if (url == null) {
					return false;
				}
				uri = url;
				try {
					controlCore = new SoftRobotWebsocketControlCore(url, this);
				} catch (RPIException ex) {
					return false;
				}
			}
			availableModules.clear();

			Document modules = SoftRobotHTTP.getXML(uri + "/modules/");
			if (modules == null) {
				return false;
			}
			NodeList moduleNodes = modules.getElementsByTagName("module");
			for (int i = 0; i < moduleNodes.getLength(); i++) {
				availableModules.add(((Element) moduleNodes.item(i)).getAttribute("name"));
			}

			for (String deviceName : deviceStatusListeners.keySet()) {

				deviceStatusChanged(deviceName, deviceStatus.get(deviceName));

			}

			return true;
		}
	};

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
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		if (defaultOverride < 0 || defaultOverride > 1) {
			throw new ConfigurationException("defaultOverride", "Default override must be in range [0..1]");
		}
	}

	@Override
	protected void beforeInitialization() throws RoboticsException {
		super.beforeInitialization();

		this.override = defaultOverride;

		addOperationStateListener(new OperationStateListener() {

			@Override
			public void operationStateChanged(OnlineObject object, OperationState newState) {
				if (newState == OperationState.OPERATIONAL) {
					synchronized (registeredExtensions) {
						for (String e : registeredExtensions) {
							loadExtension(e);
						}
					}
				}
			}

		});

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						checkConnection();
						Thread.sleep(2000);
						if (!isInitialized()) {
							return;
						}
					}
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}

	public boolean supportsPrimitive(String name) {
		return availableModules.contains(name);
	}

	@Override
	public double getOverride() {
		return override;
	}

	protected void unloadCommand(final SoftRobotCommandHandle cmd) {
		try {
			cmd.unload();
			commands.remove(cmd);
		} catch (final CommandException e) {
		}
	}

	@Override
	public List<CommandHandle> getCommandHandles() {
		return new Vector<CommandHandle>(commands);
	}

	@Override
	public void setOverride(final double newOverride) {
		if (newOverride < 0 || newOverride > 1) {
			throw new IllegalArgumentException("Override must be in range [0..1]");
		}

		override = newOverride;

		for (final SoftRobotCommandHandle cmd : new Vector<SoftRobotCommandHandle>(commands)) {
			// set override where possible
			try {
				cmd.setOverride(override);
			} catch (final CommandException e) {
			}
		}
	}

	@Override
	public Command createWaitCommand() {
		return createWaitCommand("");
	}

	private final MapperRegistry mapperRegistry = new MapperRegistry();

	@Override
	public MapperRegistry getMapperRegistry() {
		return mapperRegistry;
	}

	public String getUri() {
		return uri;
	}

	@Override
	public CommandHandle loadNet(Fragment net, String description) throws RoboticsException {
		ControlCore controlCore = this.controlCore;
		if (controlCore == null) {
			throw new NotPresentException("Runtime is not online");
		}

		// load net
		NetHandle netHandle;
		try {
			netHandle = controlCore.load(net, description);
		} catch (final RPIException e) {
			throw new CommandException(e.getMessage(), e);
		}

		// find override
		NetcommValue override = null;
		for (final NetcommValue ncv : net.getNetcommToRPI()) {
			if (ncv.getName().equals("Override")) {
				override = ncv;
			}
		}

		// build command handle
		final SoftRobotCommandHandle commandHandle = new SoftRobotCommandHandle(netHandle, override);

		commandHandle.addStatusListener(new CommandStatusListener() {
			@Override
			public void statusChanged(CommandStatus newStatus) {
				if (newStatus == CommandStatus.ERROR || newStatus == CommandStatus.TERMINATED) {
					unloadCommand(commandHandle);
				}
			}
		});

		// remember command
		commands.add(commandHandle);

		return commandHandle;
	}

	@Override
	public Command createWaitCommand(String name) {
		return new SoftRobotWaitCommand(name, this);
	}

	public ControlCore getControlCore() {
		return controlCore;
	}

	@Override
	public Command createWaitCommand(String name, double duration) {
		return new SoftRobotWaitCommand(name, this, duration);
	}

	@Override
	public RuntimeCommand createRuntimeCommandInternal(Actuator actuator, Action action, DeviceParameterBag parameters)
			throws RoboticsException {
		return new SoftRobotRuntimeCommand(action, actuator, parameters);
	}

	@Override
	public TransactionCommand createTransactionCommand(String name) {
		return new SoftRobotTransactionCommand(name, this);
	}

	@Override
	protected void beforeUninitialization() throws RoboticsException {
		super.beforeUninitialization();

		if (controlCore != null) {
			controlCore.shutdown();
			controlCore = null;
		}
	}

	@Override
	public void checkBlockEventHandlerThread() throws RoboticsException {
		try {
			if (controlCore != null) {
				controlCore.checkBlockEventHandlerThread();
			}
		} catch (RPIException e) {
			throw new RoboticsException(e);
		}
	}

	Map<String, List<SoftRobotDeviceStatusListener>> deviceStatusListeners = new HashMap<String, List<SoftRobotDeviceStatusListener>>();
	Map<String, List<SoftRobotRuntimeDeviceListener>> deviceListeners = new HashMap<String, List<SoftRobotRuntimeDeviceListener>>();

	public void addDeviceListener(String type, SoftRobotRuntimeDeviceListener listener) {
		if (!deviceListeners.containsKey(type)) {
			deviceListeners.put(type, new ArrayList<SoftRobotRuntimeDeviceListener>());
		}
		deviceListeners.get(type).add(listener);
		for (Map.Entry<String, String> entry : deviceType.entrySet()) {
			if (entry.getValue().equals(type)) {
				listener.deviceAdded(entry.getKey());
			}
		}
	}

	public void addDeviceStatusListener(String deviceName, SoftRobotDeviceStatusListener listener) {
		if (!deviceStatusListeners.containsKey(deviceName)) {
			deviceStatusListeners.put(deviceName, new ArrayList<SoftRobotDeviceStatusListener>());
		}
		deviceStatusListeners.get(deviceName).add(listener);

		String type = deviceType.get(deviceName);
		if (type != null) {
			listener.isPresent(type, deviceInterfaces.get(deviceName));
		} else {
			listener.isAbsent();
		}
		SoftRobotDeviceStatus currentStatus = deviceStatus.get(deviceName);
		if (currentStatus != null) {
			notifyStatusListener(listener, currentStatus);
		}
	}

	public void removeDeviceStatusListener(String deviceName, SoftRobotDeviceStatusListener listener) {
		if (!deviceStatusListeners.containsKey(deviceName)) {
			return;
		}
		deviceStatusListeners.get(deviceName).remove(listener);
	}

	Map<String, SoftRobotDeviceStatus> deviceStatus = new HashMap<String, SoftRobotDeviceStatus>();
	Map<String, String> deviceType = new HashMap<String, String>();
	Map<String, List<String>> deviceInterfaces = new HashMap<String, List<String>>();

	@Override
	public void deviceAdded(String device, String type, List<String> interfaces) {
		deviceType.put(device, type);
		deviceInterfaces.put(device, interfaces);
		if (deviceListeners.containsKey(type)) {
			for (SoftRobotRuntimeDeviceListener listener : new ArrayList<SoftRobotRuntimeDeviceListener>(
					deviceListeners.get(type))) {
				listener.deviceAdded(device);
			}
		}
		if (deviceStatusListeners.containsKey(device)) {
			for (SoftRobotDeviceStatusListener listener : new ArrayList<SoftRobotDeviceStatusListener>(
					deviceStatusListeners.get(device))) {
				listener.isPresent(type, interfaces);
			}
		}
	}

	@Override
	public void deviceStatusChanged(String device, SoftRobotDeviceStatus status) {
		deviceStatus.put(device, status);
		if (deviceStatusListeners.containsKey(device)) {
			for (SoftRobotDeviceStatusListener listener : new ArrayList<SoftRobotDeviceStatusListener>(
					deviceStatusListeners.get(device))) {
				notifyStatusListener(listener, status);
			}
		}
	}

	private void notifyStatusListener(SoftRobotDeviceStatusListener listener, SoftRobotDeviceStatus status) {
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
			for (SoftRobotDeviceStatusListener listener : new ArrayList<SoftRobotDeviceStatusListener>(
					deviceStatusListeners.get(device))) {
				notifyStatusListener(listener, null);
			}
		}
	}

	public List<String> getDeviceInterfaces(String deviceName) {
		return controlCore.getDeviceInterfaces(deviceName);
	}

	public boolean createDevice(String deviceName, String deviceType, Map<String, String> parameters) {
		return (controlCore != null) ? controlCore.createDevice(deviceName, deviceType, parameters) : false;
	}

	public boolean deleteDevice(String deviceName) {
		return (controlCore != null) ? controlCore.deleteDevice(deviceName) : false;
	}

	@Override
	public void registerPrimitive(String name, Class<? extends JPrimitive> primitive) {
		if (controlCore instanceof JavaRCCExtensionPoint)
			((JavaRCCExtensionPoint) controlCore).registerPrimitive(name, primitive);
	}

	@Override
	public void registerDevice(String type, DeviceFactory factory) {
		if (controlCore instanceof JavaRCCExtensionPoint)
			((JavaRCCExtensionPoint) controlCore).registerDevice(type, factory);
	}

	@Override
	public void registerInterface(String name, Class<? extends JDevice> type) {
		if (controlCore instanceof JavaRCCExtensionPoint)
			((JavaRCCExtensionPoint) controlCore).registerInterface(name, type);
	}
}