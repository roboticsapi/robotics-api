/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimevector;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.world.Vector;

/**
 * This class implements a {@link BinaryFunctionRealtimeVector} that adds its
 * two inner {@link RealtimeVectors}s.
 */
public final class AddedRealtimeVector extends BinaryFunctionRealtimeVector {

	/**
	 * Constructor
	 *
	 * @param value1 the first operand.
	 * @param value2 the second operand.
	 */
	AddedRealtimeVector(RealtimeVector value1, RealtimeVector value2) {
		super(value1, value2);
	}

	@Override
	protected RealtimeVector performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new AddedRealtimeVector(getFirstValue().substitute(substitutionMap),
				getSecondValue().substitute(substitutionMap));
	}

	@Override
	public String toString() {
		return "(" + getFirstValue() + " + " + getSecondValue() + ")";
	}

	@Override
	protected Vector computeCheapValue(Vector value1, Vector value2) {
		return value1.add(value2);
	}
}
