/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.List;

/**
 * A robotics device (i.e. an entity that can cause events and provide data).
 */
public interface Device extends RoboticsEntity, OnlineObject {

	public interface InterfaceListener {
		public void interfaceAdded(Device device, Class<? extends DeviceInterface> type);

		public void interfaceRemoved(Device device, Class<? extends DeviceInterface> type);
	}

	/**
	 * Gets a List of DeviceInterface types this Device can provide.
	 *
	 * @return List of types of DeviceInterfaces
	 */
	public List<Class<? extends DeviceInterface>> getInterfaces();

	/**
	 * Retrieves the device interface
	 *
	 * @param deviceInterface device interface type to access
	 * @return the device interface for the device
	 */
	public <U extends DeviceInterface> U use(Class<U> deviceInterface);

	/**
	 * Retrieves a device interface compatible with a given DeviceInterface
	 *
	 * @param deviceInterface device interface type to access
	 * @param compatibleWith  the other interface this interface should be
	 *                        compatible with
	 * @return the device interface for the device
	 */
	public <U extends DeviceInterface> U use(Class<U> deviceInterface, DeviceInterface compatibleWith);

	/**
	 * Retrieves a device interface compatible with a given RoboticsRuntime
	 *
	 * @param deviceInterface device interface type to access
	 * @param compatibleWith  the RoboticsRuntime this interface should be
	 *                        compatible with
	 * @return the device interface for the device
	 */
	public <U extends DeviceInterface> U use(Class<U> deviceInterface, RoboticsRuntime compatibleWith);

	/**
	 * Registers a device interface factory for the device
	 *
	 * @param type    type of device interface
	 * @param factory device interface factory to register
	 */
	public <T extends DeviceInterface> void addInterfaceFactory(Class<T> type, DeviceInterfaceFactory<T> factory);

	/**
	 * Unregisters a device interface factory from the device
	 *
	 * @param type    type of device interface
	 * @param factory device interface factory to unregister
	 */
	public <T extends DeviceInterface> void removeInterfaceFactory(Class<T> type, DeviceInterfaceFactory<T> factory);

	/**
	 * Registers a device interface modifier for the device
	 *
	 * @param deviceInterface device interface to register
	 */
	public <T extends DeviceInterface> void addInterfaceModifier(Class<T> type, DeviceInterfaceModifier<T> modifier);

	/**
	 * Unregisters a device interface modifier from the device
	 *
	 * @param deviceInterface device interface to unregister
	 */
	public <T extends DeviceInterface> void removeInterfaceModifier(Class<T> type, DeviceInterfaceModifier<T> modifier);

	public void addInterfaceListener(InterfaceListener listener);

	public void removeInterfaceListener(InterfaceListener listener);

	/**
	 * Returns the driver of this device
	 *
	 * @param runtime runtime to get the driver for
	 * @return the driver of this device for the given runtime
	 */
	public DeviceDriver getDriver(RoboticsRuntime runtime);

}