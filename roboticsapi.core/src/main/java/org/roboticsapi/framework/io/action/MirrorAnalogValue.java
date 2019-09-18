/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.action;

import org.roboticsapi.core.action.ProcessAction;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

/**
 * This action mirrors the value of a given double sensor to an analog output
 * until cancelled.
 */
public class MirrorAnalogValue extends OutputAction implements ProcessAction {

	private final RealtimeDouble sensor;

	/**
	 * Constructs a new <code>MirrorAnalogValue</code> action.
	 *
	 * @param sensor the sensor whose value is mirrored to the output.
	 */
	public MirrorAnalogValue(final RealtimeDouble sensor) {
		super(false, true);
		this.sensor = sensor;
	}

	/**
	 * Returns the mirrored sensor.
	 *
	 * @return the mirrored sensor.
	 */
	public RealtimeDouble getSensor() {
		return sensor;
	}

}
