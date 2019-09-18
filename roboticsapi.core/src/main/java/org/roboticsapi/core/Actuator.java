/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.realtimevalue.realtimeboolean.ActuatorRealtimeBoolean;

/**
 * A controllable robotics {@link Device} (i.e. something that can execute
 * actions).
 */
public interface Actuator extends Device {

	/**
	 * RealtimeBoolean evaluating to true when an Actuator has finished performing a
	 * certain Action.
	 */
	public static class CompletedRealtimeBoolean extends ActuatorRealtimeBoolean {
		public CompletedRealtimeBoolean(Command scope, Actuator actuator) {
			super(scope, actuator);
		}
	}

	/**
	 * Gets RealtimeBoolean evaluating to true when this Actuator has finished
	 * performing a certain Action.
	 *
	 * @return the CompletedState
	 */
	ActuatorRealtimeBoolean getCompletedState(Command scope);

	/**
	 * Validates the given device parameters.
	 *
	 * @param parameters parameters to check
	 * @throws InvalidParametersException when the parameters are invalid
	 */
	void validateParameters(DeviceParameters parameters) throws InvalidParametersException;

	/**
	 * Sets the default parameters of this Actuator (to be used by all commands that
	 * don't have their own).
	 *
	 * @param newParameters default parameters for this Actuator
	 * @throws InvalidParametersException if the parameters are invalid for this
	 *                                    Actuator20
	 */
	void addDefaultParameters(final DeviceParameters newParameters) throws InvalidParametersException;

	/**
	 * Gets the default parameters of this Actuator (used by all commands that don't
	 * have their own).
	 *
	 * @return DeviceParameterBag containing the default DeviceParameters
	 */
	DeviceParameterBag getDefaultParameters();

	/**
	 * Check parameter bounds.
	 *
	 * @param parameters the parameters
	 * @throws InvalidParametersException the invalid parameters exception
	 */
	void checkParameterBounds(DeviceParameters parameters) throws InvalidParametersException;

	/**
	 * Check parameter bounds.
	 *
	 * @param parameters the parameters
	 * @throws InvalidParametersException the invalid parameters exception
	 */
	void checkParameterBounds(DeviceParameters... parameters) throws InvalidParametersException;

	/**
	 * Check parameter bounds.
	 *
	 * @param parameters the parameters
	 * @throws InvalidParametersException the invalid parameters exception
	 */
	void checkParameterBounds(DeviceParameterBag parameters) throws InvalidParametersException;

	@Override
	ActuatorDriver getDriver(RoboticsRuntime runtime);

	ActuatorDriver getDriver();

}