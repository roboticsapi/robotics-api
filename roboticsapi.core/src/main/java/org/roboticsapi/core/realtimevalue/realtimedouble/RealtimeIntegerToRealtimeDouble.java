/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import org.roboticsapi.core.RealtimeValue;

/**
 * RealtimeDouble that converts values of a given RealtimeInteger.
 */
public final class RealtimeIntegerToRealtimeDouble extends UnaryFunctionRealtimeDouble<Integer> {

	RealtimeIntegerToRealtimeDouble(RealtimeValue<Integer> inner) {
		super(inner);
	}

	@Override
	protected Double computeCheapValue(Integer value) {
		return new Double(value);
	}

}
