/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimedouble.AbsRealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueAliasFactory;

public class AbsRealtimeDoubleMapper extends TypedRealtimeValueAliasFactory<Double, AbsRealtimeDouble> {

	public AbsRealtimeDoubleMapper() {
		super(AbsRealtimeDouble.class);
	}

	@Override
	protected RealtimeValue<Double> createAlias(AbsRealtimeDouble value) {
		RealtimeDouble inner = (RealtimeDouble) value.getInnerValue();
		return RealtimeDouble.createConditional(inner.greater(0), inner, inner.negate());
	}
}
