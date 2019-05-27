/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.List;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.State;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;

/**
 * An RtActivity is an Activity that is fully executed with real-time
 * guarantees.
 * 
 * All RtActivities are implemented internally by using the Robotics API
 * {@link Command} mechanisms.
 */
public interface RtActivity extends Activity {

	/**
	 * Adds a listener for the event that a certain {@link State} is entered during
	 * execution.
	 * 
	 * @param state    the {@link State} to listen to
	 * @param listener the listener that is to be notified
	 */
	public abstract void addStateListener(State state, StateEnteredListener listener);

	/**
	 * Adds a listener for the event that a certain {@link State} is left during
	 * execution.
	 * 
	 * @param state    the {@link State} to listen to
	 * @param listener the listener that is to be notified
	 */
	public abstract void addStateListener(State state, StateLeftListener listener);

	/**
	 * Defines given {@link State}s to be conditions for cancelling execution of
	 * this RtActivity. Execution is cancelled if any of the States defined to be
	 * cancelling States is entered.
	 * 
	 * @param state         the {@link State} that leads to cancelling
	 * @param furtherStates further {@link State}s that lead to cancelling
	 */
	public abstract void addCancelConditions(State state, State... furtherStates);

	/**
	 * Defines given {@link State}s to be conditions for aborting execution of this
	 * RtActivity. Execution is aborted if any of the States defined to be aborting
	 * States is entered.
	 * 
	 * @param state         the {@link State} that leads to aborting
	 * @param furtherStates further {@link State}s that lead to aborting
	 */
	// public abstract void addDoneConditions(State state, State...
	// furtherStates);

	/**
	 * Lets this RtActivity raise a given {@link CommandRealtimeException} when
	 * certain other types of CommandRealtimeExceptions occur.
	 * 
	 * @param thrownException the exception to raise
	 * @param cause           type of Exception that leads to raising the specified
	 *                        Exception
	 */
	public abstract void raiseException(CommandRealtimeException thrownException,
			Class<? extends CommandRealtimeException> cause);

	/**
	 * Lets this RtActivity raise a given {@link CommandRealtimeException} when
	 * certain other types of CommandRealtimeExceptions occur.
	 * 
	 * @param thrownException the exception to raise
	 * @param causes          types of Exceptions that lead to raising the specified
	 *                        Exception
	 */
	public abstract void raiseException(CommandRealtimeException thrownException,
			List<Class<? extends CommandRealtimeException>> causes);

	/**
	 * Declares a {@link CommandRealtimeException} to be thrown when certain
	 * {@link State}s become active.
	 * 
	 * @param thrownException the Exception to throw
	 * @param cause           State causing the Exception to be thrown
	 * @param furtherCauses   Further States causing the Exception to be thrown
	 */
	public abstract void declareException(CommandRealtimeException thrownException, State cause,
			State... furtherCauses);

	/**
	 * Declares a {@link CommandRealtimeException} to be thrown when certain
	 * {@link State}s become active.
	 * 
	 * @param thrownException the Exception to throw
	 * @param causes          States causing the Exception to be thrown (when any of
	 *                        them becomes active)
	 */
	public abstract void declareException(CommandRealtimeException thrownException, List<State> causes);

	/**
	 * Specifies a given type of CommandRealtimeException to be ignored during
	 * execution of this RtActivity.
	 * 
	 * @param type the type of exception to ignore
	 */
	public abstract void ignoreException(Class<? extends CommandRealtimeException> type);

	/**
	 * Gets the {@link Command} implementing this {@link RtActivity}.
	 * 
	 * @return the {@link Command}
	 */
	public abstract Command getCommand();

	public abstract State getMaintainingState();

	public abstract void addTrigger(State triggering, RtActivity toExecute);

	/**
	 * Gets the {@link State} indicating completion of this {@link RtActivity}.
	 * 
	 * @return the completion {@link State}.
	 */
	public abstract State getCompletedState();

	/**
	 * Gets the {@link State} indicating the start of this {@link RtActivity}.
	 * 
	 * @return the start {@link State}.
	 */
	public abstract State getStartedState();

	/**
	 * Gets the {@link State} indicating cancellation of this {@link RtActivity} .
	 * 
	 * @return the cancellation {@link State}.
	 */
	// public abstract State getCancelState();

	@Override
	public abstract RtActivity beginExecute() throws RoboticsException;

	/**
	 * Gets the {@link RoboticsRuntime} this RtActivity is executed on.
	 * 
	 * The RoboticsRuntime is determined by the Devices this RtActivity controls.
	 * 
	 * @return RoboticsRuntime of this RtActivity
	 */
	public abstract RoboticsRuntime getRuntime();
}
