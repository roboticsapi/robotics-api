/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.DataAgeRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.TimeDiff;
import org.roboticsapi.facet.runtime.rpi.core.primitives.TimeNet;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class DataAgeRealtimeDoubleMapper extends TypedRealtimeValueFragmentFactory<Double, DataAgeRealtimeDouble> {

	public DataAgeRealtimeDoubleMapper() {
		super(DataAgeRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(DataAgeRealtimeDouble value)
			throws MappingException, RpiException {
		TimeNet timeNet = new TimeNet();
		TimeDiff timeDiff = new TimeDiff();
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, timeDiff.getOutValue(), timeNet);
		ret.connect(timeNet.getOutValue(), timeDiff.getInSecond());
		ret.addDependency(value.getSensor(), ret.addInPort("inValue"), ret.addInPort("inTime", timeDiff.getInFirst()));
		return ret;
	}

}
