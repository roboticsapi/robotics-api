/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.io.action;

import org.roboticsapi.core.sensor.BooleanSensor;

/**
 * This action sets the value of an digital output to the value of a given
 * boolean or boolean sensor.
 */
public class SetDigitalValue extends OutputAction {

	private final BooleanSensor sensor;

	/**
	 * Constructs a new <code>SetDigitalValue</code> action.
	 * 
	 * @param value the value to set on the output.
	 */
	public SetDigitalValue(final boolean value) {
		this(BooleanSensor.fromValue(value));
	}

	/**
	 * Constructs a new <code>SetDigitalValue</code> action.
	 * 
	 * @param sensor the sensor whose value is set on the output.
	 */
	public SetDigitalValue(final BooleanSensor sensor) {
		this.sensor = sensor;
	}

	/**
	 * Returns the sensor.
	 * 
	 * @return the sensor.
	 */
	public BooleanSensor getSensor() {
		return sensor;
	}

}
