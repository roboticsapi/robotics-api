/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.List;

import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;

/**
 */
public interface ActuatorDriver extends DeviceDriver {

	/**
	 * Defines the errors of this actuator driver
	 * 
	 * @return list of errors
	 */
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions();

	/**
	 * Defines an error for this actuator driver
	 * 
	 * @param type type of exception
	 * @return exception of the given type, or null
	 */
	public <T extends ActuatorDriverRealtimeException> T defineActuatorDriverException(Class<T> type)
			throws CommandException;

	/**
	 * Retrieves the list of error types of this actuator driver
	 * 
	 * @return list of error types
	 */
	public List<Class<? extends ActuatorDriverRealtimeException>> getExceptionTypes();

}
