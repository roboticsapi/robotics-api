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
 * A State that serves as an alias for another State. When this State is used in
 * EventHandlers, the EventHandler behaves as if it were parameterized with the
 * other State.
 * 
 * AliasState can e.g. be used as a 'template' for supplying a State at a time
 * at which the correct State is not yet available. The correct State can later
 * be set for the AliasState via setOther(...).
 */
public class AliasState extends DerivedState {
	private State other;

	/**
	 * Instantiates a new AliasState.
	 */
	public AliasState() {
	}

	/**
	 * Instantiates a new AliasState.
	 * 
	 * @param other the State which this State is an alias for.
	 */
	public AliasState(State other) {
		this.setOther(other);

	}

	/**
	 * Gets the State which this State is an alias for.
	 * 
	 * @return the aliased State
	 */
	public State getOther() {
		return other;
	}

	/**
	 * Sets the State which this State is an alias for.
	 * 
	 * @param other the new aliased State
	 */
	public void setOther(State other) {
		this.other = other;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && getOther() != null && getOther().equals(((AliasState) obj).getOther());
	}

	@Override
	public List<State> getStates() {
		return Arrays.asList(other);
	}

}
