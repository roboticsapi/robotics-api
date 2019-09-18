/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.action;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

/**
 * This action sets the value of an digital output to the value of a given
 * boolean or boolean sensor.
 */
public class SetDigitalValue extends OutputAction {

	private final RealtimeBoolean sensor;

	/**
	 * Constructs a new <code>SetDigitalValue</code> action.
	 *
	 * @param value the value to set on the output.
	 */
	public SetDigitalValue(final boolean value) {
		this(RealtimeBoolean.createFromConstant(value));
	}

	/**
	 * Constructs a new <code>SetDigitalValue</code> action.
	 *
	 * @param sensor the sensor whose value is set on the output.
	 */
	public SetDigitalValue(final RealtimeBoolean sensor) {
		super(true, false);
		this.sensor = sensor;
	}

	/**
	 * Returns the sensor.
	 *
	 * @return the sensor.
	 */
	public RealtimeBoolean getSensor() {
		return sensor;
	}

}
