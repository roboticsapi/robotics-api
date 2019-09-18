/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.DividedRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleDivide;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class DividedRealtimeDoubleMapper extends TypedRealtimeValueFragmentFactory<Double, DividedRealtimeDouble> {

	public DividedRealtimeDoubleMapper() {
		super(DividedRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(DividedRealtimeDouble value)
			throws MappingException, RpiException {
		DoubleDivide add = new DoubleDivide();
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, add.getOutValue());
		ret.addDependency(value.getDividend(), ret.addInPort("inFirst", add.getInFirst()));
		ret.addDependency(value.getDivisor(), ret.addInPort("inSecond", add.getInSecond()));
		return ret;
	}

}
