/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.AsinRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleAsin;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class AsinRealtimeDoubleMapper extends TypedRealtimeValueFragmentFactory<Double, AsinRealtimeDouble> {

	public AsinRealtimeDoubleMapper() {
		super(AsinRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(AsinRealtimeDouble value)
			throws MappingException, RpiException {
		DoubleAsin asin = new DoubleAsin();
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, asin.getOutValue());
		ret.addDependency(value.getInnerValue(), ret.addInPort("inValue", asin.getInValue()));
		return ret;
	}

}
