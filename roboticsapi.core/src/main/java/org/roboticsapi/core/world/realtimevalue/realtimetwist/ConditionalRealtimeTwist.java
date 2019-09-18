/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetwist;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.world.Twist;

public final class ConditionalRealtimeTwist extends RealtimeTwist {

	private final RealtimeValue<Boolean> condition;
	private final RealtimeValue<Twist> ifTrue;
	private final RealtimeValue<Twist> ifFalse;

	ConditionalRealtimeTwist(RealtimeValue<Boolean> condition, RealtimeValue<Twist> ifTrue,
			RealtimeValue<Twist> ifFalse) {
		super(condition, ifTrue, ifFalse);
		this.condition = condition;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;

	}

	public ConditionalRealtimeTwist(RealtimeValue<Boolean> condition, RealtimeValue<Twist> ifTrue, Twist ifFalse) {
		this(condition, ifTrue, new ConstantRealtimeTwist(ifFalse));
	}

	public ConditionalRealtimeTwist(RealtimeValue<Boolean> condition, Twist ifTrue, RealtimeValue<Twist> ifFalse) {
		this(condition, new ConstantRealtimeTwist(ifTrue), ifFalse);
	}

	public ConditionalRealtimeTwist(RealtimeValue<Boolean> condition, Twist ifTrue, Twist ifFalse) {
		this(condition, ifTrue, new ConstantRealtimeTwist(ifFalse));
	}

	public RealtimeValue<Boolean> getCondition() {
		return condition;
	}

	public RealtimeValue<Twist> getIfFalse() {
		return ifFalse;
	}

	public RealtimeValue<Twist> getIfTrue() {
		return ifTrue;
	}

	@Override
	protected Twist calculateCheapValue() {
		Boolean booleanValue = condition.getCheapValue();

		if (booleanValue == null) {
			return null;
		}

		if (booleanValue) {
			return ifTrue.getCheapValue();
		} else {
			return ifFalse.getCheapValue();
		}
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && condition.equals(((ConditionalRealtimeTwist) obj).condition)
				&& ifTrue.equals(((ConditionalRealtimeTwist) obj).ifTrue)
				&& ifFalse.equals(((ConditionalRealtimeTwist) obj).ifFalse);
	}

	@Override
	public int hashCode() {
		return classHash(condition, ifTrue, ifFalse);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(condition, ifTrue, ifFalse);
	}

	@Override
	public String toString() {
		return "(" + condition + " ? " + ifTrue + " : " + ifFalse + ")";
	}
}
