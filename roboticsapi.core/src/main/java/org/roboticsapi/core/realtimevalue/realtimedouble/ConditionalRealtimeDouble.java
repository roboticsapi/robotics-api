/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public final class ConditionalRealtimeDouble extends RealtimeDouble {

	@Override
	protected RealtimeDouble performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new ConditionalRealtimeDouble(condition.substitute(substitutionMap), ifTrue.substitute(substitutionMap),
				ifFalse.substitute(substitutionMap));
	}

	private final RealtimeBoolean condition;
	private final RealtimeDouble ifTrue;
	private final RealtimeDouble ifFalse;

	ConditionalRealtimeDouble(RealtimeBoolean condition, RealtimeDouble ifTrue, RealtimeDouble ifFalse) {
		super(condition, ifTrue, ifFalse);
		this.condition = condition;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;

	}

	ConditionalRealtimeDouble(RealtimeBoolean condition, RealtimeDouble ifTrue, double ifFalse) {
		this(condition, ifTrue, RealtimeDouble.createFromConstant(ifFalse));
	}

	ConditionalRealtimeDouble(RealtimeBoolean condition, double ifTrue, RealtimeDouble ifFalse) {
		this(condition, RealtimeDouble.createFromConstant(ifTrue), ifFalse);
	}

	ConditionalRealtimeDouble(RealtimeBoolean condition, double ifTrue, double ifFalse) {
		this(condition, ifTrue, RealtimeDouble.createFromConstant(ifFalse));
	}

	public RealtimeBoolean getCondition() {
		return condition;
	}

	public RealtimeDouble getIfFalse() {
		return ifFalse;
	}

	public RealtimeDouble getIfTrue() {
		return ifTrue;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && condition.equals(((ConditionalRealtimeDouble) obj).condition)
				&& ifTrue.equals(((ConditionalRealtimeDouble) obj).ifTrue)
				&& ifFalse.equals(((ConditionalRealtimeDouble) obj).ifFalse);
	}

	@Override
	public int hashCode() {
		return classHash(condition, ifTrue, ifFalse);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(condition, ifFalse, ifTrue);
	}

	@Override
	public String toString() {
		return "(" + condition + " ? " + ifTrue + " : " + ifFalse + ")";
	}
}
