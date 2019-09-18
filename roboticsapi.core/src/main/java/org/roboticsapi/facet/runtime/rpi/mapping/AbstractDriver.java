/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.Map;
import java.util.logging.Level;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.Dependency.Builder;
import org.roboticsapi.core.actuator.AbstractOnlineObject;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.exception.OperationStateUnknownException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;

public abstract class AbstractDriver extends AbstractOnlineObject implements NamedDriver {

	protected final Dependency<String> rpiDeviceName;
	protected final Dependency<RpiRuntime> runtime;
	Boolean present = null;
	private RpiDeviceStatusListener dsl;
	private static int rpiDeviceNum = 0;

	public AbstractDriver() {
		super();
		rpiDeviceName = createDependency("rpiDeviceName", () -> "d" + (rpiDeviceNum++));
		if (getRuntimeBuilder() == null)
			runtime = createDependency("runtime");
		else
			runtime = createDependency("runtime", getRuntimeBuilder());
	}

	/**
	 * Provider for a Builder of a RPIRuntime - when this Driver relies on another
	 * Driver that has its own Runtime, override this method with a Builder that
	 * extracts the RPIRuntime from the other driver.
	 * 
	 * @return a Builder for RPIRuntimes if the runtime can be derived from other
	 *         dependencies, or null if runtime should be a required property
	 */
	protected Builder<RpiRuntime> getRuntimeBuilder() {
		return null;
	}

	public final RpiRuntime getRuntime() {
		return runtime.get();
	}

	@ConfigurationProperty
	public final void setRuntime(RpiRuntime runtime) {
		this.runtime.set(runtime);
	}

	/**
	 * Returns the driver's device name (required for identifying the counterpart at
	 * the RCC).
	 *
	 * @return the device name at the RCC.
	 */
	public final String getRpiDeviceName() {
		return rpiDeviceName.get();
	}

	@ConfigurationProperty
	public final void setRpiDeviceName(String deviceName) {
		this.rpiDeviceName.set(deviceName);
	}

	/**
	 * Returns the driver's device type at the RCC. Can be <code>null</code>, if it
	 * is not necessary.
	 *
	 * @return the driver's device type at the RCC
	 */
	public String getRpiDeviceType() {
		return null;
	}

	private boolean getRpiDeviceParametersCalled = false;

	/**
	 * Collects parameters required to build the device driver on the RCC.
	 * 
	 * @return parameters required to build the device driver, should be based on
	 *         the value returned by super#getRpiDeviceParameters()
	 */
	protected RpiParameters getRpiDeviceParameters() {
		getRpiDeviceParametersCalled = true;
		return new RpiParameters();
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
	protected void beforeInitialization() {
		super.beforeInitialization();

		dsl = new RpiDeviceStatusListener() {
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
			public void isPresent(String deviceType, Map<String, RpiParameters> interfaces) {
				present2 = checkDevice(deviceType, interfaces);
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

	@Override
	protected void afterInitialization() {
		super.afterInitialization();

		addDependentObject(getRuntime());
		getRuntime().addDeviceStatusListener(getRpiDeviceName(), dsl);

		if (getRpiDeviceType() != null) {
			getRpiDeviceParametersCalled = false;
			loadDeviceDriver(getRpiDeviceParameters());
			if (!getRpiDeviceParametersCalled) {
				RAPILogger.getLogger(this).log(Level.WARNING,
						"Superclass method call for getRpiDeviceParameters() missing in " + getClass().getName());
			}
		}
	}

	@Override
	protected void beforeUninitialization() {
		if (getRpiDeviceType() != null) {
			deleteDeviceDriver();
		}

		getRuntime().removeDeviceStatusListener(getRpiDeviceName(), dsl);
		removeDependentObject(getRuntime());

		super.beforeUninitialization();
	}

	protected final boolean checkDevice(String deviceType, Map<String, RpiParameters> interfaces) {
		return checkDeviceType(deviceType) && checkRpiDeviceInterfaces(interfaces);
	}

	protected boolean checkDeviceType(String deviceType) {
		if (getRpiDeviceType() == null) {
			return true;
		}
		return getRpiDeviceType().equals(deviceType);
	}

	protected abstract boolean checkRpiDeviceInterfaces(Map<String, RpiParameters> interfaces);

	protected final void checkDeviceAndRuntimeOperational() throws CommunicationException {
		OperationState runtimeState = getRuntime().getState();

		if (runtimeState != OperationState.OPERATIONAL && runtimeState != OperationState.SAFEOPERATIONAL) {
			throw new CommunicationException("RoboticsRuntime is in state " + runtimeState);
		}

		OperationState deviceState = getState();

		if (deviceState != OperationState.OPERATIONAL && deviceState != OperationState.SAFEOPERATIONAL) {
			throw new CommunicationException("Device " + getRpiDeviceName() + " is in state " + deviceState);
		}
	}

	/**
	 * Loads this device driver at the runtime with its device name and type using
	 * the specified parameters.
	 *
	 * @param parameters further parameters required for loading this device driver
	 *                   at the runtime or <code>null</code> if none are required.
	 *
	 * @see #getRpiDeviceName()
	 * @see #getRpiDeviceType()
	 */
	private final void loadDeviceDriver(RpiParameters parameters) {
		if (parameters == null) {
			parameters = new RpiParameters();
		}
		getRuntime().getControlCore().createDevice(getRpiDeviceName(), getRpiDeviceType(), parameters);
	}

	/**
	 * Deletes this device driver at the runtime (identified using its device name).
	 *
	 * @see #getRpiDeviceType()
	 */
	private final void deleteDeviceDriver() {
		getRuntime().getControlCore().removeDevice(getRpiDeviceName());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " '" + getRpiDeviceName() + "'";
	}

}