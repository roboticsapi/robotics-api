/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.runtime;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.sensor.SensorRealtimeException;

public class ActionSensorException extends ActionRealtimeException {
	private static final long serialVersionUID = 7415743204831325501L;
	private final SensorRealtimeException sensorException;

	public ActionSensorException(Action action, SensorRealtimeException sensorException) {
		super(action);

		if (sensorException == null) {
			throw new IllegalArgumentException("Argument sensorException may not be null.");
		}

		this.sensorException = sensorException;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		if (!sensorException.equals(((ActionSensorException) obj).sensorException)) {
			return false;
		}
		return true;
	}
}
