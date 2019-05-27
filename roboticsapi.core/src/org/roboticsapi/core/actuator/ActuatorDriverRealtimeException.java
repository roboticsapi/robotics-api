/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.runtime.CommandRealtimeException;

public class ActuatorDriverRealtimeException extends CommandRealtimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ActuatorDriver actuatorDriver;

	public ActuatorDriverRealtimeException(ActuatorDriver actuatorDriver) {
		super(actuatorDriver != null ? "Exception in " + actuatorDriver.toString() : "Exception in unknown driver.");
		this.actuatorDriver = actuatorDriver;
	}

	public ActuatorDriver getActuatorDriver() {
		return actuatorDriver;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		if (this.actuatorDriver == null || ((ActuatorDriverRealtimeException) obj).actuatorDriver == null) {
			return true;
		}
		return this.actuatorDriver.equals(((ActuatorDriverRealtimeException) obj).actuatorDriver);
	}

}
