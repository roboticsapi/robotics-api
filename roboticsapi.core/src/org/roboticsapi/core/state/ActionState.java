/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.state;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.State;
import org.roboticsapi.core.util.HashCodeUtil;

/**
 * An event triggered by an action
 */
public abstract class ActionState extends State {
	/**
	 * Action the event listens to
	 */
	protected Action action;

	/**
	 * Sets the context action to apply the event to
	 * 
	 * @param context action to apply the event to (or null to apply to all
	 *                applicable actions)
	 * @return parameterized action (this)
	 */
	public ActionState setAction(final Action context) {
		this.action = context;
		return this;
	}

	/**
	 * Gets the action context this event is applied to.
	 * 
	 * @return action context of this event
	 */
	public Action getAction() {
		return this.action;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj)
				&& (action == null || ((ActionState) obj).action == null || action == ((ActionState) obj).action);
	}

	@Override
	public boolean subclassEquals(State other) {
		return super.subclassEquals(other) && ((ActionState) other).action == this.action;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(super.hashCode(), action);
	}

	@Override
	public String toString() {
		return super.toString() + "<" + action + ">";
	}
}
