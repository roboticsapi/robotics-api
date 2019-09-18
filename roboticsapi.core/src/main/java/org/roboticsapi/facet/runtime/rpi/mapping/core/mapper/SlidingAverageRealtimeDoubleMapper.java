/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.SlidingAverageRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleAverage;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class SlidingAverageRealtimeDoubleMapper
		extends TypedRealtimeValueFragmentFactory<Double, SlidingAverageRealtimeDouble> {

	public SlidingAverageRealtimeDoubleMapper() {
		super(SlidingAverageRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(SlidingAverageRealtimeDouble value)
			throws MappingException, RpiException {
		DoubleAverage average = new DoubleAverage(value.getDuration());
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, average.getOutValue());
		ret.addDependency(value.getOther(), ret.addInPort("inValue", average.getInValue()));
		return ret;
	}

}
