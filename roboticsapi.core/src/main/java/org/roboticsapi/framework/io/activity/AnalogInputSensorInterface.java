/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.activity;

import org.roboticsapi.core.SensorInterface;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public interface AnalogInputSensorInterface extends SensorInterface {
	/**
	 * Sensor for the signal voltage of the analog input. The sensor value is
	 * ranging from the input's minimal signal voltage to the maximum signal
	 * voltage.
	 *
	 * @return the double sensor for the analog input's voltage
	 */
	RealtimeDouble getVoltageSensor();

	/**
	 * Sensor for the value of the analog input. The sensor value is ranging from 0
	 * to 1.
	 *
	 * @return the double sensor for the analog input's value
	 */
	RealtimeDouble getRawValueSensor();

}
