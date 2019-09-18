/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

/**
 * Interface for device drivers
 */
public interface DeviceDriver extends OnlineObject, Driver {

	/**
	 * Returns the driver's controlled device
	 *
	 * @return the device.
	 */
	Device getDevice();
}