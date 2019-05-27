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
import org.roboticsapi.core.util.HashCodeUtil;

public class ExplicitState extends DerivedState {

	private final State activatingState;
	private final State deactivatingState;

	public ExplicitState(State activatingState, State deactivatingState) {
		this.activatingState = activatingState;
		this.deactivatingState = deactivatingState;

	}

	@Override
	public List<State> getStates() {
		List<State> states = new Vector<State>();

		states.add(activatingState);
		states.add(deactivatingState);

		return states;
	}

	public State getActivatingState() {
		return activatingState;
	}

	public State getDeactivatingState() {
		return deactivatingState;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		if (!(getActivatingState().equals(((ExplicitState) obj).getActivatingState()))) {
			return false;
		}
		if (!(getDeactivatingState().equals(((ExplicitState) obj).getDeactivatingState()))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = HashCodeUtil.hash(super.hashCode(), getActivatingState());
		return HashCodeUtil.hash(hash, getDeactivatingState());
	}

}
