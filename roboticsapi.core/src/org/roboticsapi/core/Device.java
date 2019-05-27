/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.List;

import org.roboticsapi.core.entity.ComposedEntity;

/**
 * A robotics device (i.e. an entity that can cause events and provide data).
 */
public interface Device extends OnlineObject, ComposedEntity {

	public interface InterfaceFactoryListener {
		public void interfaceFactoryAdded(Device device, DeviceInterfaceFactory factory);
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
	 * Registers a device interface for the device
	 * 
	 * @param deviceInterface device interface to register
	 */
	public void addInterfaceFactory(DeviceInterfaceFactory factory);

	public void addInterfaceFactoryListener(InterfaceFactoryListener listener);

	public void removeInterfaceFactoryListener(InterfaceFactoryListener listener);

	/**
	 * Returns the driver of this device
	 * 
	 * @return the driver of this device
	 */
	public DeviceDriver getDriver();

}