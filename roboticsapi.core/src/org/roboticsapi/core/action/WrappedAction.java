/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.action;

import java.util.List;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.state.ActionState;

/**
 * A WrappedAction modifies the execution of a given Action.
 */
public abstract class WrappedAction extends Action {

	/** The Action that is wrapped. */
	private final Action wrappedAction;

	/**
	 * Instantiates a new WrappedAction.
	 * 
	 * @param wrappedAction the wrapped Action
	 */
	public WrappedAction(Action wrappedAction) {
		// super(wrappedAction.getWatchdogTimeout());
		super(getWatchdogTimeoutFromValidWrappedAction(wrappedAction));
		this.wrappedAction = wrappedAction;
	}

	private static double getWatchdogTimeoutFromValidWrappedAction(Action wrappedAction) {
		if (wrappedAction == null) {
			throw new IllegalArgumentException("The wrapped Action must not be null.");
		}

		return wrappedAction.getWatchdogTimeout();
	}

	@Override
	public List<ActionRealtimeException> defineActionExceptions() {
		return getWrappedAction().defineActionExceptions();
	}

	/**
	 * Gets the wrapped Action.
	 * 
	 * @return the wrapped Action
	 */
	public Action getWrappedAction() {
		return wrappedAction;
	}

	@Override
	public boolean supportsState(ActionState event) {
		return super.supportsState(event) || getWrappedAction().supportsState(event);
	}

	@Override
	public String toString() {
		return super.toString() + "<" + getWrappedAction().toString() + ">";
	}

}
