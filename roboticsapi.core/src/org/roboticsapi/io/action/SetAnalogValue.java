/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.io.action;

import org.roboticsapi.core.action.GoalAction;
import org.roboticsapi.core.sensor.DoubleSensor;

/**
 * This action sets the value of an analog output to the value of a given double
 * or double sensor.
 */
public class SetAnalogValue extends OutputAction implements GoalAction {

	private final DoubleSensor sensor;

	/**
	 * Constructs a new <code>SetAnalogValue</code> action.
	 * 
	 * @param sensor the sensor whose value is set on the output.
	 */
	public SetAnalogValue(final DoubleSensor sensor) {
		this.sensor = sensor;
	}

	/**
	 * Constructs a new <code>SetAnalogValue</code> action.
	 * 
	 * @param value the value to set on the output.
	 */
	public SetAnalogValue(final double value) {
		this(DoubleSensor.fromValue(value));
	}

	/**
	 * Returns the value.
	 * 
	 * @return the value.
	 */
	public DoubleSensor getSensor() {
		return sensor;
	}

}
