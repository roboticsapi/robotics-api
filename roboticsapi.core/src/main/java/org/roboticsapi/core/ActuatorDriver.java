/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.List;

import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;

public interface ActuatorDriver extends DeviceDriver {

	/**
	 * Defines the errors of this actuator driver
	 *
	 * @return list of errors
	 */
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions();

	/**
	 * Retrieves the list of error types of this actuator driver
	 *
	 * @return list of error types
	 */
	public List<Class<? extends ActuatorDriverRealtimeException>> getExceptionTypes();

	@Override
	Actuator getDevice();

}
