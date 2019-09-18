/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.AcosRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleAcos;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class AcosRealtimeDoubleMapper extends TypedRealtimeValueFragmentFactory<Double, AcosRealtimeDouble> {

	public AcosRealtimeDoubleMapper() {
		super(AcosRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(AcosRealtimeDouble value)
			throws MappingException, RpiException {
		DoubleAcos acos = new DoubleAcos();
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, acos.getOutValue());
		ret.addDependency(value.getInnerValue(), ret.addInPort("inValue", acos.getInValue()));
		return ret;
	}

}
