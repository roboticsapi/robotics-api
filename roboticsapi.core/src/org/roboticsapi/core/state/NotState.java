/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.state;

import java.util.Arrays;
import java.util.List;

import org.roboticsapi.core.State;

/**
 * An event that fires when another event does not fire
 */
public class NotState extends DerivedState {
	/** inverse event */
	private final State state;

	public NotState(final State other) {
		state = other;
	}

	public State getState() {
		return state;
	}

	@Override
	public State not() {
		return getState();
	}

	@Override
	public List<State> getStates() {
		return Arrays.asList(state);
	}
}
