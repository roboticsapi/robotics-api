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
 * A state that becomes active when any of the child states becomes active
 */
public class OrState extends DerivedState {
	/** states sufficient for this state to become active */
	private final List<State> states = new Vector<State>();

	public OrState(final State... states) {
		for (final State state : states) {
			addState(state);
		}
	}

	@Override
	public State or(final State other) {
		OrState ret = new OrState();
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
	 * Add another state
	 * 
	 * @param state state sufficient for this state to become active
	 * @return this
	 */
	public OrState addState(final State state) {
		states.add(state);
		return this;
	}
}
