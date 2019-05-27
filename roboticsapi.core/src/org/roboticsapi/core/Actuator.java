/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.state.ActuatorState;

/**
 * A controllable robotics {@link Device} (i.e. something that can execute
 * actions).
 */
public interface Actuator extends Device {

	/**
	 * The listener interface for receiving actuatorBusy events. The class that is
	 * interested in processing a actuatorBusy event implements this interface, and
	 * the object created with that class is registered with a component using the
	 * component's <code>addBusyListener<code> method. When the actuatorBusy event
	 * occurs, that object's appropriate method is invoked.
	 * 
	 */
	public interface ActuatorBusyListener {

		/**
		 * Busy state changed.
		 * 
		 * @param busy if Actuator is busy, i.e. executing any Command
		 */
		public void busyStateChanged(boolean busy);
	}

	/**
	 * Registers an {@link ActuatorBusyListener} for this Actuator.
	 * 
	 * @param listener the listener
	 */
	void addBusyListener(ActuatorBusyListener listener);

	/**
	 * Removes an {@link ActuatorBusyListener} for this Actuator.
	 * 
	 * @param listener the listener
	 */
	void removeBusyListener(ActuatorBusyListener listener);

	/**
	 * State indicating that an Actuator has finished performing a certain Action.
	 */
	public static class CompletedState extends ActuatorState {
	}

	/**
	 * Gets the CompletedState of this Actuator.
	 * 
	 * @return the CompletedState
	 */
	ActuatorState getCompletedState();

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
	ActuatorDriver getDriver();

}