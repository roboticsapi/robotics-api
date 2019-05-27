/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.state;

import java.util.List;
import java.util.Vector;

import org.roboticsapi.core.State;

/**
 * An event that fires when all child events are true
 */
public class AndState extends DerivedState {
	/** child events required */
	private final List<State> states = new Vector<State>();

	public AndState(final State... events) {
		for (final State event : events) {
			addState(event);
		}
	}

	@Override
	public State and(final State other) {
		AndState ret = new AndState();
		for (State e : getStates()) {
			ret.addState(e);
		}
		ret.addState(other);
		return ret;
	}

	@Override
	public List<State> getStates() {
		return states;
	}

	/**
	 * Add another event
	 * 
	 * @param event event required for this event to fire
	 * @return this
	 */
	public AndState addState(final State event) {
		states.add(event);
		return this;
	}
}
