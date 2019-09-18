/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

/**
 * A Robotics API action (description of something a device should do)
 */
public abstract class Action {

	/** The watchdog timeout. 0 means no watchdog. */
	private double watchdogTimeout = 0;

	private final boolean supportsCompletion;
	private final boolean supportsCancel;

	/**
	 * Instantiates a new action with the given timeout after which the connection
	 * to the Robotics API is assumed to have failed.
	 *
	 * @param watchdogTimeout the watchdog timeout in [s]. 0 means infinite, i.e. no
	 *                        timeout.
	 */
	public Action(double watchdogTimeout, boolean supportsCompletion, boolean supportsCancel) {
		if (watchdogTimeout < 0) {
			throw new IllegalArgumentException("watchdogTimeout");
		}
		this.watchdogTimeout = watchdogTimeout;
		this.supportsCancel = supportsCancel;
		this.supportsCompletion = supportsCompletion;
	}

	public boolean getCancelSupported() {
		return supportsCancel;
	}

	public boolean getCompletionSupported() {
		return supportsCompletion;
	}

	/**
	 * RealtimeBoolean evaluating to true when an action has been started
	 */
	public static class ActiveRealtimeBoolean extends ActionRealtimeBoolean {
		public ActiveRealtimeBoolean(Command scope, Action action) {
			super(scope, action);
		}
	}

	/**
	 * Gets a RealtimeBoolean evaluating to true when this Action is active
	 *
	 * @return event
	 */
	public RealtimeBoolean getActiveState(Command scope) {
		return new ActiveRealtimeBoolean(scope, this);
	}

	/**
	 * RealtimeBoolean evaluating to true when an Action has finished
	 */
	public static class CompletedRealtimeBoolean extends ActionRealtimeBoolean {
		public CompletedRealtimeBoolean(Command scope, Action action) {
			super(scope, action);
		}
	}

	/**
	 * Gets a RealtimeBoolean evaluating to true when this Action has finished
	 *
	 * @return event for current action
	 */
	public ActionRealtimeBoolean getCompletedState(Command scope) {
		return new CompletedRealtimeBoolean(scope, this);
	}

	/**
	 * Defines the errors of this Action
	 *
	 * @return list of errors
	 */
	public List<ActionRealtimeException> defineActionExceptions(Command scope) {
		ArrayList<ActionRealtimeException> exceptions = new ArrayList<ActionRealtimeException>();
		return exceptions;
	}

	/**
	 * Retrieves the list of error types of this Action
	 *
	 * @return list of error types
	 */
	public List<Class<? extends ActionRealtimeException>> getExceptionTypes() {
		List<Class<? extends ActionRealtimeException>> types = new ArrayList<Class<? extends ActionRealtimeException>>();

		for (ActionRealtimeException e : defineActionExceptions(null)) {
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
			throw new IllegalArgumentException("watchdogTimeout");
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

	public boolean supportsState(ActionRealtimeBoolean event) {
		return event.getAction() == this;
	}
}
