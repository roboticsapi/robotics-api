/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.io;

import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.Sensor;

/**
 * Interface representing drivers of digital/analog inputs.
 */
public interface InputDriver<T> extends DeviceDriver {

	/**
	 * Returns the input's sensor.
	 * 
	 * @return the input's sensor.
	 */
	Sensor<T> getSensor();

	/**
	 * Returns the input's number.
	 * 
	 * @return the input's number.
	 */
	int getNumber();

}
