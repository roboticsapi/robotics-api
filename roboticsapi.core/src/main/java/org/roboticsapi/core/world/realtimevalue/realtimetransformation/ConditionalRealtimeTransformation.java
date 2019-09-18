/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.world.Transformation;

public final class ConditionalRealtimeTransformation extends RealtimeTransformation {

	private final RealtimeValue<Boolean> condition;
	private final RealtimeValue<Transformation> ifTrue;
	private final RealtimeValue<Transformation> ifFalse;

	ConditionalRealtimeTransformation(RealtimeValue<Boolean> condition, RealtimeValue<Transformation> ifTrue,
			RealtimeValue<Transformation> ifFalse) {
		super(condition, ifTrue, ifFalse);
		this.condition = condition;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;

	}

	public ConditionalRealtimeTransformation(RealtimeValue<Boolean> condition, RealtimeValue<Transformation> ifTrue,
			Transformation ifFalse) {
		this(condition, ifTrue, new ConstantRealtimeTransformation(ifFalse));
	}

	public ConditionalRealtimeTransformation(RealtimeValue<Boolean> condition, Transformation ifTrue,
			RealtimeValue<Transformation> ifFalse) {
		this(condition, new ConstantRealtimeTransformation(ifTrue), ifFalse);
	}

	public ConditionalRealtimeTransformation(RealtimeValue<Boolean> condition, Transformation ifTrue,
			Transformation ifFalse) {
		this(condition, ifTrue, new ConstantRealtimeTransformation(ifFalse));
	}

	public RealtimeValue<Boolean> getCondition() {
		return condition;
	}

	public RealtimeValue<Transformation> getIfFalse() {
		return ifFalse;
	}

	public RealtimeValue<Transformation> getIfTrue() {
		return ifTrue;
	}

	@Override
	protected Transformation calculateCheapValue() {
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
		return classEqual(obj) && condition.equals(((ConditionalRealtimeTransformation) obj).condition)
				&& ifTrue.equals(((ConditionalRealtimeTransformation) obj).ifTrue)
				&& ifFalse.equals(((ConditionalRealtimeTransformation) obj).ifFalse);
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
