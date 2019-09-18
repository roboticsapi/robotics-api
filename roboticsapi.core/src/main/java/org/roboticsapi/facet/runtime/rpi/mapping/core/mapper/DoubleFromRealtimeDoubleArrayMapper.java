/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.GetFromArrayRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleArrayGet;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class DoubleFromRealtimeDoubleArrayMapper
		extends TypedRealtimeValueFragmentFactory<Double, GetFromArrayRealtimeDouble> {

	public DoubleFromRealtimeDoubleArrayMapper() {
		super(GetFromArrayRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(GetFromArrayRealtimeDouble value)
			throws MappingException, RpiException {
		DoubleArrayGet get = new DoubleArrayGet(value.getDoubleArray().getSize(), value.getIndex());
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, get.getOutValue());
		ret.addDependency(value.getDoubleArray(), ret.addInPort("inArray", get.getInArray()));
		return ret;
	}

}
