/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.DataAgeForTimeRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.TimeAdd;
import org.roboticsapi.facet.runtime.rpi.core.primitives.TimeHistory;
import org.roboticsapi.facet.runtime.rpi.core.primitives.TimeNet;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class DataAgeForTimeRealtimeDoubleMapper
		extends TypedRealtimeValueFragmentFactory<Double, DataAgeForTimeRealtimeDouble> {

	public DataAgeForTimeRealtimeDoubleMapper() {
		super(DataAgeForTimeRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(DataAgeForTimeRealtimeDouble value)
			throws MappingException, RpiException {

		TimeAdd add = new TimeAdd();
		TimeNet net = new TimeNet();
		TimeHistory history = new TimeHistory(value.getMaxAge());
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, history.getOutAge(), add, net);
		ret.addDependency(value.getInnerValue(), ret.addInPort("inValue"),
				ret.addInPort("inTime", history.getInValue()));
		ret.addDependency(value.getTime().negate(), "inDestTime", add.getInDelta());
		ret.connect(net.getOutValue(), add.getInTime());
		ret.connect(add.getOutValue(), history.getInTime());
		return ret;
	}

}
