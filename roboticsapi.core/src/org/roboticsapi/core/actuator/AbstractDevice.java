/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceInterfaceFactory;
import org.roboticsapi.core.OnlineObject;
import org.roboticsapi.core.entity.AbstractComposedEntity;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.RoboticsException;

/**
 * Abstract implementation for a {@link Device}.
 *
 * @param <DD> the generic driver type
 */
public abstract class AbstractDevice<DD extends DeviceDriver> extends AbstractComposedEntity implements Device {

	private DD driver;

	private final List<DeviceInterfaceFactory> interfaceFactories = new Vector<DeviceInterfaceFactory>();
	private final List<OperationStateListener> operationStateListeners = new ArrayList<OperationStateListener>();
	private final List<InterfaceFactoryListener> interfaceFactoryListeners = new ArrayList<Device.InterfaceFactoryListener>();
	private final OperationStateListener operationStateListener = new OperationStateListener() {
		@Override
		public void operationStateChanged(OnlineObject object, OperationState newState) {
			for (OperationStateListener listener : new ArrayList<OperationStateListener>(operationStateListeners)) {
				listener.operationStateChanged(AbstractDevice.this, newState);
			}
		}
	};

	@Override
	protected final void beforeInitialization() throws RoboticsException {
		super.beforeInitialization();

		setupDriver(getDriver());
		beforeInitializationOfDerivedDevices();
	}

	protected void beforeInitializationOfDerivedDevices() throws RoboticsException {
		// empty
	}

	/**
	 * This method should setup the driver, that means set all initial data which is
	 * needed by the driver
	 *
	 * @param driver the driver to setup
	 */
	protected abstract void setupDriver(DD driver);

	@Override
	protected final void afterInitialization() throws RoboticsException {
		getDriver().addOperationStateListener(operationStateListener);
		afterInitializationOfDerivedDevices();
	}

	protected void afterInitializationOfDerivedDevices() throws RoboticsException {
		// empty
	}

	@Override
	protected final void beforeUninitialization() throws RoboticsException {
		getDriver().removeOperationStateListener(operationStateListener);
		undoInitializationOfDerivedDevices();
	}

	protected void undoInitializationOfDerivedDevices() throws RoboticsException {
		// empty
	}

	@Override
	public final List<Class<? extends DeviceInterface>> getInterfaces() {
		List<Class<? extends DeviceInterface>> ret = new ArrayList<Class<? extends DeviceInterface>>();

		for (DeviceInterfaceFactory i : interfaceFactories) {
			ret.addAll(i.getProvidedInterfaces());
		}

		// TODO: this is actually not the truth, determine actual interfaces
		// somehow
		return ret;
	}

	@Override
	public final <T extends DeviceInterface> T use(Class<T> deviceInterface) {
		List<T> candidates = new ArrayList<T>();

		for (DeviceInterfaceFactory dif : interfaceFactories) {
			if (dif.canBuild(deviceInterface)) {
				candidates.add(dif.build(deviceInterface));
			}

		}
		if (candidates.size() == 1) {
			return candidates.get(0);
		} else if (candidates.size() > 1) {
			return selectBestCandidate(candidates);
		}

		return null;
	}

	private final <T extends DeviceInterface> T selectBestCandidate(List<T> candidates) {
		T best = candidates.get(0);
		for (T candidate : candidates) {
			if (best.getClass().isAssignableFrom(candidate.getClass())) {
				best = candidate;
			}
		}
		return best;
	}

	@Override
	public final void addInterfaceFactory(DeviceInterfaceFactory factory) {
		interfaceFactories.add(factory);

		for (InterfaceFactoryListener l : new ArrayList<InterfaceFactoryListener>(interfaceFactoryListeners)) {
			l.interfaceFactoryAdded(this, factory);
		}
	}

	@Override
	public void addInterfaceFactoryListener(org.roboticsapi.core.Device.InterfaceFactoryListener listener) {

		interfaceFactoryListeners.add(listener);

		for (int i = 0; i < interfaceFactories.size(); i++) {
			listener.interfaceFactoryAdded(this, interfaceFactories.get(i));
		}
	}

	@Override
	public void removeInterfaceFactoryListener(org.roboticsapi.core.Device.InterfaceFactoryListener listener) {
		interfaceFactoryListeners.remove(listener);
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		checkNotNullAndInitialized("driver", driver);
	}

	/**
	 * Sets the driver for this device
	 *
	 * @param driver the driver
	 */
	@ConfigurationProperty(Optional = false)
	public final void setDriver(DD driver) {
		immutableWhenInitialized();
		this.driver = driver;
	}

	@Override
	public final DD getDriver() {
		return driver;
	}

	@Override
	public final void addOperationStateListener(final OperationStateListener listener) {
		operationStateListeners.add(listener);
		listener.operationStateChanged(this, getState());
	}

	@Override
	public final void removeOperationStateListener(OperationStateListener listener) {
		operationStateListeners.remove(listener);
	}

	@Override
	public final OperationState getState() {
		return isInitialized() ? getDriver().getState() : OperationState.NEW;
	}

	@Override
	public final boolean isPresent() {
		return getState().isPresent();
	}

}
