/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeinteger;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

/**
 * This class implements a {@link DerivedRealtimeInteger} that evaluates a
 * condition and passes on the appropriate of both inner {@link RealtimeInteger}s.
 */
public final class ConditionalRealtimeInteger extends DerivedRealtimeInteger {

	/**
	 * Constructs a new {@link ConditionalRealtimeInteger}.
	 *
	 * @param condition the condition to evaluate.
	 * @param ifTrue    sensor passed on if condition evaluates to
	 *                  <code>true</code>.
	 * @param ifFalse   sensor passed on if condition evaluates to
	 *                  <code>false</code>.
	 */
	ConditionalRealtimeInteger(RealtimeBoolean condition, RealtimeInteger ifTrue, RealtimeInteger ifFalse) {
		super(condition, ifTrue, ifFalse);
	}

	/**
	 * Constructs a new {@link ConditionalRealtimeInteger}.
	 *
	 * @param condition the condition to evaluate.
	 * @param ifTrue    sensor passed on if condition evaluates to
	 *                  <code>true</code>.
	 * @param ifFalse   value passed on if condition evaluates to
	 *                  <code>false</code>.
	 */
	public ConditionalRealtimeInteger(RealtimeBoolean condition, RealtimeInteger ifTrue, int ifFalse) {
		this(condition, ifTrue, new ConstantRealtimeInteger(ifFalse));
	}

	/**
	 * Constructs a new {@link ConditionalRealtimeInteger}.
	 *
	 * @param condition the condition to evaluate.
	 * @param ifTrue    value passed on if condition evaluates to <code>true</code>.
	 * @param ifFalse   sensor passed on if condition evaluates to
	 *                  <code>false</code>.
	 */

	public ConditionalRealtimeInteger(RealtimeBoolean condition, int ifTrue, RealtimeInteger ifFalse) {
		this(condition, new ConstantRealtimeInteger(ifTrue), ifFalse);
	}

	/**
	 * Constructs a new {@link ConditionalRealtimeInteger}.
	 *
	 * @param condition the condition to evaluate.
	 * @param ifTrue    value passed on if condition evaluates to <code>true</code>.
	 * @param ifFalse   value passed on if condition evaluates to
	 *                  <code>false</code>.
	 */
	public ConditionalRealtimeInteger(RealtimeBoolean condition, int ifTrue, int ifFalse) {
		this(condition, ifTrue, new ConstantRealtimeInteger(ifFalse));
	}

	/**
	 * Returns the {@link RealtimeBoolean} holding the condition.
	 *
	 * @return the condition.
	 */
	public RealtimeBoolean getCondition() {
		return (RealtimeBoolean) getInner(0);
	}

	/**
	 * Returns the {@link RealtimeInteger} that is passed on if condition evaluates to
	 * <code>true</code>.
	 *
	 * @return the sensor if condition evaluates to <code>true</code>.
	 */
	public RealtimeInteger getIfTrue() {
		return (RealtimeInteger) getInner(1);
	}

	/**
	 * Returns the {@link RealtimeInteger} that is passed on if condition evaluates to
	 * <code>false</code>.
	 *
	 * @return the sensor if condition evaluates to <code>false</code>.
	 */
	public RealtimeInteger getIfFalse() {
		return (RealtimeInteger) getInner(2);
	}

	@Override
	public String toString() {
		return "(" + getCondition() + " ? " + getIfTrue() + " : " + getIfFalse() + ")";
	}

	@Override
	protected Integer computeCheapValue(Object[] values) {
		return (Integer) (((Boolean) values[0]) ? values[1] : values[2]);
	}
}
