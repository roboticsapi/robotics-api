/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.SquareRootRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleSquareRoot;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class SquareRootRealtimeDoubleMapper
		extends TypedRealtimeValueFragmentFactory<Double, SquareRootRealtimeDouble> {

	public SquareRootRealtimeDoubleMapper() {
		super(SquareRootRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(SquareRootRealtimeDouble value)
			throws MappingException, RpiException {
		DoubleSquareRoot sqrt = new DoubleSquareRoot();
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, sqrt.getOutValue());
		ret.addDependency(value.getInnerValue(), ret.addInPort("inValue", sqrt.getInValue()));
		return ret;
	}

}
