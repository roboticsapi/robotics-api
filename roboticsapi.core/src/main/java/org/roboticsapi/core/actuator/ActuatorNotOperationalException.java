/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.actuator;

import org.roboticsapi.core.ActuatorDriver;

/**
 * Exception occurring when this {@link ActuatorDriver} is being controlled but
 * has no driving clearance.
 */
public class ActuatorNotOperationalException extends ActuatorDriverRealtimeException {

	private static final long serialVersionUID = 1L;

	public ActuatorNotOperationalException(ActuatorDriver actuatorDriver) {
		super(actuatorDriver);
	}
}