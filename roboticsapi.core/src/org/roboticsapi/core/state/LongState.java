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
import org.roboticsapi.core.util.HashCodeUtil;

/**
 * An event that fires when another event occurs for a certain period of time.
 */
public class LongState extends DerivedState {
	private final State other;
	private final double seconds;

	/**
	 * Creates a long event
	 * 
	 * @param other   base event
	 * @param seconds duration the other event has to occur
	 */
	public LongState(final State other, final double seconds) {
		this.other = other;
		this.seconds = seconds;
	}

	public State getOther() {
		return other;
	}

	public double getSeconds() {
		return seconds;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && seconds == ((LongState) obj).seconds && other.equals(((LongState) obj).other);
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(super.hashCode(), seconds);
	}

	@Override
	public List<State> getStates() {
		return Arrays.asList(other);
	}
}
