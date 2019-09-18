/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.action;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

/**
 * This action mirrors the value of a given a Boolean sensor to a digital output
 * (until cancelled).
 */
public class MirrorDigitalValue extends OutputAction {

	private final RealtimeBoolean sensor;

	/**
	 * Constructs a new <code>MirrorDigitalValue</code> action.
	 *
	 * @param sensor the sensor whose value is mirrored to the output.
	 */
	public MirrorDigitalValue(final RealtimeBoolean sensor) {
		super(false, true);
		this.sensor = sensor;
	}

	/**
	 * Returns the mirrored sensor.
	 *
	 * @return the mirrored sensor.
	 */
	public RealtimeBoolean getSensor() {
		return sensor;
	}

}
