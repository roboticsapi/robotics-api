/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.exception.ActionCancelledException;
import org.roboticsapi.core.state.ActionState;

/**
 * A Robotics API action (description of something a device should do)
 */
public abstract class Action {

	/** The watchdog timeout. 0 means no watchdog. */
	private double watchdogTimeout = 0;

	/**
	 * Instantiates a new action with the given timeout after which the connection
	 * to the Robotics API is assumed to have failed.
	 * 
	 * @param watchdogTimeout the watchdog timeout in [s]. 0 means infinite, i.e. no
	 *                        timeout.
	 */
	public Action(double watchdogTimeout) {
		setWatchdogTimeout(watchdogTimeout);
	}

	/**
	 * Event when an action is started
	 */
	public static class ActiveState extends ActionState {
	}

	/**
	 * Fired when an action is active
	 * 
	 * @return event
	 */
	public State getActiveState() {
		return active().setAction(this);
	}

	/**
	 * Fired when an action is active
	 * 
	 * @return event
	 */
	public static ActionState active() {
		return new ActiveState();
	}

	/**
	 * Event when an action has finished
	 */
	public static class CompletedState extends ActionState {
	}

	/**
	 * Fired when an action has finished
	 * 
	 * @return event
	 */
	public static ActionState completed() {
		return new CompletedState();
	}

	/**
	 * Fired when an action has finished
	 * 
	 * @return event for current action
	 */
	public ActionState getCompletedState() {
		return completed().setAction(this);
	}

	/**
	 * Defines the errors of this Action
	 * 
	 * @return list of errors
	 */
	public List<ActionRealtimeException> defineActionExceptions() {
		ArrayList<ActionRealtimeException> exceptions = new ArrayList<ActionRealtimeException>();

		exceptions.add(new ActionCancelledException(this));

		return exceptions;
	}

	/**
	 * Defines an error of this Action
	 * 
	 * @param type type of the error
	 * @return the error, or null
	 */
	public <T extends ActionRealtimeException> T defineActionExceptions(Class<T> type) throws CommandException {
		for (ActionRealtimeException ex : defineActionExceptions()) {
			if (type.isAssignableFrom(ex.getClass())) {
				return type.cast(ex);
			}
		}
		throw new CommandException("Found no action exception of the given type: " + type.getName());
	}

	/**
	 * Retrieves the list of error types of this Action
	 * 
	 * @return list of error types
	 */
	public List<Class<? extends ActionRealtimeException>> getExceptionTypes() {
		List<Class<? extends ActionRealtimeException>> types = new ArrayList<Class<? extends ActionRealtimeException>>();

		for (ActionRealtimeException e : defineActionExceptions()) {
			types.add(e.getClass());
		}

		return types;
	}

	/**
	 * Gets whether this command needs a stable connection to the Robotics API
	 * during execution
	 * 
	 * @return if keep alive mechanism is necessary
	 */
	public boolean getNeedsWatchdog() {
		return watchdogTimeout > 0;
	}

	/**
	 * Gets the timeout (in s) after which the connection to the Robotics API is
	 * assumed to have failed during execution of this Action.
	 * 
	 * @return the watchdog timeout
	 */
	public double getWatchdogTimeout() {
		return watchdogTimeout;
	}

	/**
	 * Sets the timeout (in s) after which the connection to the Robotics API is
	 * assumed to have failed during execution of this Action.
	 * 
	 * A value of 0 (zero) has the meaning of an infinite timeout (i.e., no check).
	 * 
	 * @param watchdogTimeout the timeout for the watchdog in s
	 */
	public void setWatchdogTimeout(double watchdogTimeout) {
		if (watchdogTimeout < 0) {
			throw new IllegalArgumentException("watchdogTimeout must be >= 0");
		}

		this.watchdogTimeout = watchdogTimeout;
	}

	public void relaxWatchdogTimeout(double watchdogTimeout) {
		if (this.watchdogTimeout != 0 && this.watchdogTimeout < watchdogTimeout) {
			setWatchdogTimeout(watchdogTimeout);
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	public boolean supportsState(ActionState event) {
		return event.getAction() == this;
	}
}
