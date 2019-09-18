/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.activity;

import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

/**
 * This device interface creates real-time activities for setting values on
 * field-bus analog outputs.
 */
public interface AnalogOutputInterface extends ActuatorInterface {

	/**
	 * Creates an {@link RtActivity} that sets a specified value on the according
	 * analog output.
	 *
	 * @param value the value to set, ranging from 0 to 1
	 * @return the activity that sets the specified value
	 * @throws RoboticsException        if the activity creation fails
	 * @throws IllegalArgumentException if the given output value is not between the
	 *                                  0 and 1.
	 */
	Activity setValue(double value) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that sets the value of a specified double
	 * sensor on the according analog output.
	 *
	 * @param sensor the sensor whose value is set on the output.
	 * @return the corresponding activity
	 * @throws RoboticsException if the activity creation fails
	 */
	Activity setValue(RealtimeDouble sensor) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that sets a specified value on the according
	 * analog output and waits a given time for the value to settle.
	 *
	 * @param value the value to set
	 * @param delay the time for which the RtActivity waits
	 * @return the activity that sets the value
	 * @throws RoboticsException        if the activity could not be created
	 * @throws IllegalArgumentException if the given output value is not between the
	 *                                  0 and 1.
	 * @throws IllegalArgumentException if the given delay is less than 0.
	 */
	Activity setValue(double value, double delay) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that sets the value of a specified double
	 * sensor on the according analog output and waits a given time for the value to
	 * settle.
	 *
	 * @param sensor the sensor whose value is set on the output.
	 * @param delay  the time for which the RtActivity waits
	 * @return the corresponding activity
	 * @throws RoboticsException        if the activity creation fails
	 * @throws IllegalArgumentException if the given delay is less than 0.
	 */
	Activity setValue(RealtimeDouble sensor, double delay) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that sets a specified voltage on the according
	 * analog output.
	 *
	 * @param voltage the voltage to set
	 * @return the activity that sets the specified voltage
	 * @throws RoboticsException        if the activity creation fails
	 * @throws IllegalArgumentException if the given voltage is not between the
	 *                                  output's minimal and its maximum signal
	 *                                  voltage.
	 */
	Activity setVoltage(double voltage) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that sets the value (interpreted as signal
	 * voltage) of a specified double sensor on the according analog output.
	 *
	 * @param voltageSensor the sensor whose value (interpreted as signal voltage)
	 *                      is set on the output.
	 * @return the corresponding activity
	 * @throws RoboticsException if the activity creation fails
	 */
	Activity setVoltage(RealtimeDouble voltageSensor) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that sets a specified voltage on the according
	 * analog output and waits a given time for the voltage to settle.
	 *
	 * @param voltage the voltage to set
	 * @param delay   the time for which the activity waits
	 * @return the activity that sets the value
	 * @throws RoboticsException        if the activity could not be created
	 * @throws IllegalArgumentException if the given voltage is not between the
	 *                                  output's minimal and its maximum signal
	 *                                  voltage.
	 * @throws IllegalArgumentException if the given delay is less than 0.
	 */
	Activity setVoltage(double voltage, double delay) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that sets the value (interpreted as signal
	 * voltage) of a specified double sensor on the according analog output and
	 * waits a given time for the voltage to settle.
	 *
	 * @param voltageSensor the sensor whose value (interpreted as signal voltage)
	 *                      is set on the output.
	 * @param delay         the time for which the activity waits
	 * @return the corresponding activity
	 * @throws RoboticsException        if the activity creation fails
	 * @throws IllegalArgumentException if the given delay is less than 0.
	 */
	Activity setVoltage(RealtimeDouble voltageSensor, double delay) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that mirrors the value of a specified double
	 * sensor to an analog output until the activity is cancelled.
	 *
	 * @param sensor the sensor whose value is mirrored to the output.
	 * @return the corresponding activity
	 * @throws RoboticsException if the activity creation fails
	 */
	Activity mirrorValue(RealtimeDouble sensor) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} that mirrors the value (interpreted as signal
	 * voltage) of a specified double sensor to an analog output until the activity
	 * is cancelled.
	 *
	 * @param sensor the sensor whose value (interpreted as signal voltage) is
	 *               mirrored to the output.
	 * @return the corresponding activity
	 * @throws RoboticsException if the activity creation fails
	 */
	Activity mirrorVoltage(RealtimeDouble voltageSensor) throws RoboticsException;

}
