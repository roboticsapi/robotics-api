/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.Atan2RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleAtan2;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class Atan2RealtimeDoubleMapper extends TypedRealtimeValueFragmentFactory<Double, Atan2RealtimeDouble> {

	public Atan2RealtimeDoubleMapper() {
		super(Atan2RealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(Atan2RealtimeDouble value)
			throws MappingException, RpiException {
		DoubleAtan2 add = new DoubleAtan2();
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, add.getOutValue());
		ret.addDependency(value.getX(), ret.addInPort("inX", add.getInX()));
		ret.addDependency(value.getY(), ret.addInPort("inY", add.getInY()));
		return ret;
	}

}
