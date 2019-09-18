/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.activity;

import org.roboticsapi.core.SensorInterface;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public interface DigitalOutputSensorInterface extends SensorInterface {

	/**
	 * Sensor for the raw value of the digital input.
	 *
	 * @return the boolean sensor for the digital input's value
	 */
	RealtimeBoolean getSensor();

}
