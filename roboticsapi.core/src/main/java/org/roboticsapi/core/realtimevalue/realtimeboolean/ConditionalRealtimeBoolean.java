/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;

public class ConditionalRealtimeBoolean extends RealtimeBoolean {

	@Override
	protected RealtimeBoolean performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new ConditionalRealtimeBoolean(condition.substitute(substitutionMap), ifTrue.substitute(substitutionMap),
				ifFalse.substitute(substitutionMap));
	}

	private final RealtimeBoolean condition;
	private final RealtimeBoolean ifTrue;
	private final RealtimeBoolean ifFalse;

	ConditionalRealtimeBoolean(RealtimeBoolean condition, RealtimeBoolean ifTrue, RealtimeBoolean ifFalse) {
		super(condition, ifTrue, ifFalse);
		this.condition = condition;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;

	}

	ConditionalRealtimeBoolean(RealtimeBoolean condition, RealtimeBoolean ifTrue, boolean ifFalse) {
		this(condition, ifTrue, RealtimeBoolean.createFromConstant(ifFalse));
	}

	ConditionalRealtimeBoolean(RealtimeBoolean condition, boolean ifTrue, RealtimeBoolean ifFalse) {
		this(condition, RealtimeBoolean.createFromConstant(ifTrue), ifFalse);
	}

	ConditionalRealtimeBoolean(RealtimeBoolean condition, boolean ifTrue, boolean ifFalse) {
		this(condition, ifTrue, RealtimeBoolean.createFromConstant(ifFalse));
	}

	public RealtimeBoolean getCondition() {
		return condition;
	}

	public RealtimeBoolean getIfFalse() {
		return ifFalse;
	}

	public RealtimeBoolean getIfTrue() {
		return ifTrue;
	}

	@Override
	protected Boolean calculateCheapValue() {
		Boolean cCheap = getCondition().getCheapValue();

		if (cCheap != null) {
			if (cCheap == true) {
				Boolean trueCheap = getIfTrue().getCheapValue();

				return trueCheap != null ? trueCheap : null;
			} else {
				Boolean falseCheap = getIfFalse().getCheapValue();

				return falseCheap != null ? falseCheap : null;
			}
		} else {
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && condition.equals(((ConditionalRealtimeBoolean) obj).condition)
				&& ifTrue.equals(((ConditionalRealtimeBoolean) obj).ifTrue)
				&& ifFalse.equals(((ConditionalRealtimeBoolean) obj).ifFalse);
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
