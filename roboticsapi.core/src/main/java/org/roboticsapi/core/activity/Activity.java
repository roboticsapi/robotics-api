/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.runtime.CommandRealtimeException;

public interface Activity {

	/**
	 * Starts execution of this {@link Activity} and waits until execution
	 * completes.
	 *
	 * @throws RoboticsException if an error occurred during execution of the
	 *                           Activity
	 */
	void execute() throws RoboticsException;

	/**
	 * Asynchronously starts execution of this {@link Activity}. This method returns
	 * as soon as execution is started completely, i.e. the Activity is running.
	 *
	 * @return an ActivityHandle for the execution
	 * @throws RoboticsException if an error occurred during starting the Activity
	 */
	ActivityHandle beginExecute() throws RoboticsException;

	/**
	 * Gets the {@link Device}s that are controlled by this {@link Activity}, i.e.
	 * those {@link Device}s that perform actions during execution of the
	 * {@link Activity}.
	 *
	 * @return a list of controlled devices
	 */
	Set<Device> getDevices();

	/**
	 * Creates an ActivityHandle controlling an execution of this Activity
	 * 
	 * @return an ActivityHandle for executing this Activity
	 * @throws RoboticsException if the creation fails
	 */
	ActivityHandle createHandle() throws RoboticsException;

	/**
	 * Defines given {@link RealtimeBoolean}s to be conditions for cancelling
	 * execution of this RtActivity. Execution is cancelled if any of the States
	 * defined to be cancelling States is entered.
	 *
	 * @param state the {@link RealtimeBoolean} that leads to cancelling
	 */
	void addCancelCondition(RealtimeBoolean state);

	List<RealtimeBoolean> getCancelConditions();

	/**
	 * Lets this RtActivity raise a given {@link CommandRealtimeException} when
	 * certain other types of CommandRealtimeExceptions occur.
	 *
	 * @param exception the exception to raise
	 * @param cause     type of Exception that leads to raising the specified
	 *                  Exception
	 */
	void addErrorCondition(CommandRealtimeException exception, Class<? extends CommandRealtimeException> cause);

	Map<CommandRealtimeException, List<Class<? extends CommandRealtimeException>>> getExceptionErrorConditions();

	/**
	 * Declares a {@link CommandRealtimeException} to be thrown when certain
	 * {@link RealtimeBoolean}s become active.
	 *
	 * @param exception the Exception to throw
	 * @param cause     State causing the Exception to be thrown
	 */
	void addErrorCondition(CommandRealtimeException exception, RealtimeBoolean cause);

	Map<CommandRealtimeException, List<RealtimeBoolean>> getRealtimeBooleanErrorConditions();

	/**
	 * Specifies a given type of CommandRealtimeException to be ignored during
	 * execution of this RtActivity.
	 *
	 * @param type the type of exception to ignore
	 */
	void ignoreError(Class<? extends CommandRealtimeException> type);

	List<Class<? extends CommandRealtimeException>> getIgnoredErrors();

	void addPropertyProvider(ActivityPropertyProvider<?> propertyProvider);

	List<ActivityPropertyProvider<?>> getPropertyProviders();

}