/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.runtime.CommandRealtimeException;

public class SensorRealtimeException extends CommandRealtimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Sensor<?> sensor;

	// TODO May SensorRealtimeException instances have no/null Sensor instance?
	/**
	 * Instantiates a new SensorRealException with a given Sensor.
	 * 
	 * @param sensor the Sensor of this SensorRealtimeException, may be null.
	 */
	public SensorRealtimeException(Sensor<?> sensor) {
		super("Exception in Sensor " + sensor);
		this.sensor = sensor;
	}

	// TODO May SensorRealtimeException instances have no/null Sensor instance?
	/**
	 * Instantiates a new SensorRealException with a given Sensor and a message of
	 * type String.
	 * 
	 * @param sensor           the Sensor of this SensorRealtimeException, may be
	 *                         null.
	 * @param exceptionMessage the additional message (of type String) for this
	 *                         SensorRealtimeException.
	 */
	public SensorRealtimeException(Sensor<?> sensor, String exceptionMessage) {
		super("Exception in Sensor " + sensor + " (" + exceptionMessage + ")");
		this.sensor = sensor;
	}

	public Sensor<?> getSensor() {
		return sensor;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		if (this.sensor == null || ((SensorRealtimeException) obj).sensor == null) {
			return true;
		}
		return this.sensor.equals(((SensorRealtimeException) obj).sensor);
	}

}
