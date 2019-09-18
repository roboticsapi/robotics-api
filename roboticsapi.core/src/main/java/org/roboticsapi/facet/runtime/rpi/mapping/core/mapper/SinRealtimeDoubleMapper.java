/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.SinRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleSin;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class SinRealtimeDoubleMapper extends TypedRealtimeValueFragmentFactory<Double, SinRealtimeDouble> {

	public SinRealtimeDoubleMapper() {
		super(SinRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(SinRealtimeDouble value)
			throws MappingException, RpiException {
		DoubleSin sin = new DoubleSin();
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, sin.getOutValue());
		ret.addDependency(value.getInnerValue(), ret.addInPort("inValue", sin.getInValue()));
		return ret;
	}

}
