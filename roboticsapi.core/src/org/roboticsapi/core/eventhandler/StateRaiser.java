/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.eventhandler;

import org.roboticsapi.core.EventEffect;
import org.roboticsapi.core.State;

public class StateRaiser extends EventEffect {
	private final State raisedState;

	public StateRaiser(State raisedState) {
		super();
		this.raisedState = raisedState;

	}

	public State getRaisedState() {
		return raisedState;
	}
}
