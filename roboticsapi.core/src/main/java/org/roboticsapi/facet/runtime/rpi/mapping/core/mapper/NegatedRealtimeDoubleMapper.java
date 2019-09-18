/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.NegatedRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleMultiply;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class NegatedRealtimeDoubleMapper extends TypedRealtimeValueFragmentFactory<Double, NegatedRealtimeDouble> {

	public NegatedRealtimeDoubleMapper() {
		super(NegatedRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(NegatedRealtimeDouble value)
			throws MappingException, RpiException {
		DoubleMultiply neg = new DoubleMultiply(0.0, -1.0);
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, neg.getOutValue());
		ret.addDependency(value.getInnerValue(), ret.addInPort("inValue", neg.getInFirst()));
		return ret;
	}
}
