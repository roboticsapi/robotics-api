/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import java.util.List;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimedouble.ConsistentTimeRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleValue;
import org.roboticsapi.facet.runtime.rpi.core.primitives.TimeConsistentRange;
import org.roboticsapi.facet.runtime.rpi.core.primitives.TimeDiff;
import org.roboticsapi.facet.runtime.rpi.core.primitives.TimeNet;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class ConsistentTimeRealtimeDoubleMapper
		extends TypedRealtimeValueFragmentFactory<Double, ConsistentTimeRealtimeDouble> {

	public ConsistentTimeRealtimeDoubleMapper() {
		super(ConsistentTimeRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(ConsistentTimeRealtimeDouble value)
			throws MappingException, RpiException {

		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value);
		List<RealtimeValue<?>> values = value.getValues();
		if (values.size() == 0) {
			// no values -- then we have current data
			ret.defineResult(ret.add(new DoubleValue(0.0)).getOutValue());
		} else if (values.size() == 1) {
			// one value -- then its age is perfect
			FragmentInPort timePort = ret.addInPort("inTime");
			ret.addDependency(values.get(0), ret.addInPort("inValue"), timePort);
			TimeNet net = ret.add(new TimeNet());
			TimeDiff diff = ret.add(new TimeDiff());
			ret.connect(timePort.getInternalOutPort(), diff.getInFirst());
			ret.connect(net.getOutValue(), diff.getInSecond());
			ret.defineResult(diff.getOutValue());

		} else {
			OutPort startPort = null, endPort = null;

			for (int i = 0; i < values.size(); i++) {
				FragmentInPort timePort = ret.addInPort("inTime" + i);
				ret.addDependency(values.get(i), ret.addInPort("inValue" + i), timePort);
				if (startPort == null) {
					startPort = timePort.getInternalOutPort();
					endPort = timePort.getInternalOutPort();
				} else {
					TimeConsistentRange range = ret.add(new TimeConsistentRange(value.getEpsilon(), value.getMaxAge()));
					ret.connect(startPort, range.getInFirstStart());
					ret.connect(endPort, range.getInFirstEnd());
					ret.connect(timePort.getInternalOutPort(), range.getInSecondStart());
					ret.connect(timePort.getInternalOutPort(), range.getInSecondEnd());
					startPort = range.getOutStart();
					endPort = range.getOutEnd();
				}
			}
			TimeNet net = ret.add(new TimeNet());
			TimeDiff diff = ret.add(new TimeDiff());
			ret.connect(endPort, diff.getInFirst());
			ret.connect(net.getOutValue(), diff.getInSecond());
			ret.defineResult(diff.getOutValue());
		}
		return ret;
	}

}
