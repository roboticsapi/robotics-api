/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.action;

import java.util.List;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActionRealtimeException;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActionRealtimeBoolean;

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
		super(wrappedAction.getWatchdogTimeout(), wrappedAction.getCompletionSupported(),
				wrappedAction.getCancelSupported());
		this.wrappedAction = wrappedAction;
	}

	@Override
	public List<ActionRealtimeException> defineActionExceptions(Command scope) {
		return getWrappedAction().defineActionExceptions(scope);
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
	public boolean supportsState(ActionRealtimeBoolean event) {
		return super.supportsState(event) || getWrappedAction().supportsState(event);
	}

	@Override
	public String toString() {
		return super.toString() + "<" + getWrappedAction().toString() + ">";
	}

}
