/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime;

import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.actuator.AbstractOnlineObject;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.OperationStateUnknownException;
import org.roboticsapi.core.exception.RoboticsException;

/**
 * Abstract implementation for a {@link DeviceDriver} targeting the RCC
 * reference implementation.
 *
 */
public abstract class AbstractSoftRobotDeviceDriver extends AbstractOnlineObject implements DeviceDriver {

	private String deviceName;
	private SoftRobotRuntime runtime;

	Boolean present = null;
	private SoftRobotDeviceStatusListener dsl;

	/**
	 * Default constructor.
	 */
	protected AbstractSoftRobotDeviceDriver() {
		super();
	}

	/**
	 * Convenience constructor.
	 *
	 * @param runtime    the driver's runtime.
	 * @param deviceName the driver's device name at the RCC.
	 */
	protected AbstractSoftRobotDeviceDriver(SoftRobotRuntime runtime, String deviceName) {
		this();

		this.runtime = runtime;
		this.deviceName = deviceName;
	}

	@Override
	public final SoftRobotRuntime getRuntime() {
		return runtime;
	}

	@ConfigurationProperty
	public final void setRuntime(SoftRobotRuntime runtime) {
		immutableWhenInitialized();
		this.runtime = runtime;
	}

	/**
	 * Returns the driver's device name (required for identifying the counterpart at
	 * the RCC).
	 *
	 * @return the device name at the RCC.
	 */
	public final String getDeviceName() {
		return deviceName;
	}

	@ConfigurationProperty
	public final void setDeviceName(String deviceName) {
		immutableWhenInitialized();
		this.deviceName = deviceName;
	}

	/**
	 * Returns the driver's device type at the RCC. Can be <code>null</code>, if it
	 * is not necessary.
	 *
	 * @return the driver's device type at the RCC
	 */
	public String getDeviceType() {
		return null;
	}

	private final String getRuntimeUri() {
		return getRuntime().getUri();
	}

	protected final String getDeviceUri() {
		return getRuntimeUri() + "/devices/" + getDeviceName() + "/";
	}

	@Override
	protected final boolean checkPresent() throws OperationStateUnknownException {
		if (present == null) {
			throw new OperationStateUnknownException();
		}

		return present;
	}

	private final void goPresent(boolean present) {
		this.present = present;
		checkConnection();
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		if (deviceName == null) {
			throw new ConfigurationException("deviceName", "deviceName has to be set");
		}
		checkNotNullAndInitialized("runtime", runtime);
	}

	@Override
	protected void beforeInitialization() throws RoboticsException {
		super.beforeInitialization();

		dsl = new SoftRobotDeviceStatusListener() {
			private boolean present2;

			@Override
			public void isSafeOperational() {
				if (!present2) {
					return;
				}
				goPresent(true);
				goSafeoperational();
			}

			@Override
			public void isOperational() {
				if (!present2) {
					return;
				}
				goPresent(true);
				goOperational();
			}

			@Override
			public void isOffline() {
				if (!present2) {
					return;
				}
				goPresent(true);
				goOffline();
			}

			@Override
			public void isPresent(String deviceType, List<String> interfaces) {
				present2 = checkDevice(deviceType, (interfaces != null) ? interfaces : getInterfaces());
				if (present2) {
					goPresent(true);
				}
			}

			@Override
			public void isAbsent() {
				present2 = false;
				goPresent(false);
			}
		};
	}

	protected List<String> getInterfaces() {
		return runtime.getDeviceInterfaces(getDeviceName());
	}

	@Override
	protected void afterInitialization() throws RoboticsException {
		super.afterInitialization();

		addDependentObject(getRuntime());
		getRuntime().addDeviceStatusListener(getDeviceName(), dsl);
	}

	@Override
	protected void afterUninitialization() throws RoboticsException {
		getRuntime().removeDeviceStatusListener(getDeviceName(), dsl);
		removeDependentObject(getRuntime());

		super.afterUninitialization();
	}

	protected final boolean checkDevice(String deviceType, List<String> interfaces) {
		return checkDeviceType(deviceType) && checkDeviceInterfaces(interfaces);
	}

	protected boolean checkDeviceType(String deviceType) {
		if (getDeviceType() == null) {
			return true;
		}
		return getDeviceType().equals(deviceType);
	}

	protected abstract boolean checkDeviceInterfaces(List<String> interfaces);

	protected final void checkDeviceAndRuntimeOperational() throws CommunicationException {
		OperationState runtimeState = getRuntime().getState();

		if (runtimeState != OperationState.OPERATIONAL && runtimeState != OperationState.SAFEOPERATIONAL) {
			throw new CommunicationException("RoboticsRuntime is in state " + runtimeState);
		}

		OperationState deviceState = getState();

		if (deviceState != OperationState.OPERATIONAL && deviceState != OperationState.SAFEOPERATIONAL) {
			throw new CommunicationException("Device " + getDeviceName() + " is in state " + deviceState);
		}
	}

	/**
	 * Loads this device driver at the runtime with its device name and type using
	 * the specified parameters.
	 *
	 * @param parameters further parameters required for loading this device driver
	 *                   at the runtime or <code>null</code> if none are required.
	 *
	 * @see #getDeviceName()
	 * @see #getDeviceType()
	 */
	protected final boolean loadDeviceDriver(Map<String, String> parameters) {
		return runtime.createDevice(getDeviceName(), getDeviceType(), parameters);
	}

	/**
	 * Deletes this device driver at the runtime (identified using its device name).
	 *
	 * @see #getDeviceType()
	 */
	protected final boolean deleteDeviceDriver() {
		return runtime.deleteDevice(getDeviceName());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " '" + getDeviceName() + "'";
	}

	protected static String toString(Double d) {
		return String.valueOf(d);
	}

	protected static String toString(Integer i) {
		return String.valueOf(i);
	}

}
