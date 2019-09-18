/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.util.HashCodeUtil;

/**
 * A RealtimeBoolean depending on an Action.
 */
public abstract class ActionRealtimeBoolean extends RealtimeBoolean {
	/**
	 * Action the event listens to
	 */
	protected final Action action;

	public ActionRealtimeBoolean(Command scope, Action action) {
		super(scope);
		this.action = action;
	}

	/**
	 * Gets the action context this state is applied to.
	 *
	 * @return action context of this state
	 */
	public Action getAction() {
		return this.action;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && action == ((ActionRealtimeBoolean) obj).action;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(super.hashCode(), action);
	}

	@Override
	public String toString() {
		return super.toString() + "<" + action + ">";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}
}
