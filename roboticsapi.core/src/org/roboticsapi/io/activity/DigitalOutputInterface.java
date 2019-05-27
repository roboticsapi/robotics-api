/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.io.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanSensor;

/**
 * This device interface creates real-time activities for setting values on
 * field-bus digital outputs.
 */
public interface DigitalOutputInterface extends DeviceInterface {

	/**
	 * Creates an {@link RtActivity} that mirrors the value of a specified boolean
	 * sensor to an digital output until the activity is cancelled.
	 * 
	 * @param sensor the sensor whose value is mirrored to the output.
	 * @return the corresponding activity
	 * @throws RoboticsException if the activity creation fails
	 */
	RtActivity mirrorValue(BooleanSensor sensor) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that sets a given value on the according
	 * output.
	 * 
	 * @param value the value to set
	 * @return the RtActivity that sets the value
	 */
	RtActivity setValue(boolean value) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that sets the value of a specified boolean
	 * sensor on the according digital output.
	 * 
	 * @param sensor the sensor whose value is set on the output.
	 * @return the corresponding activity
	 * @throws RoboticsException if the activity creation fails
	 */
	RtActivity setValue(BooleanSensor sensor) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that sets a given value on the according output
	 * and waits for a certain time for the value to settle.
	 * 
	 * @param value the value to set
	 * @param delay the time to wait after setting the value in [ms]
	 * @return the RtActivity that sets the value
	 * @throws RoboticsException thrown if RtActivity creation fails
	 */
	RtActivity setValue(boolean value, double delay) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that sets the value of a specified boolean
	 * sensor on the according digital output and waits for a certain time for the
	 * value to settle.
	 * 
	 * @param sensor the sensor whose value is set on the output.
	 * @param delay  the time to wait after setting the value in [ms]
	 * @return the corresponding activity
	 * @throws RoboticsException if the activity creation fails
	 */
	RtActivity setValue(BooleanSensor sensor, double delay) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that sets the given value on the according
	 * output for a given time, and then resets the opposite value on that output.
	 * 
	 * @param value    the value to set for the given time
	 * @param duration the duration for which to set the value
	 * @return the RtActivity that sets and resets the value
	 * @throws RoboticsException thrown if RtActivity creation fails
	 */
	RtActivity pulse(boolean value, double duration) throws RoboticsException;

}
