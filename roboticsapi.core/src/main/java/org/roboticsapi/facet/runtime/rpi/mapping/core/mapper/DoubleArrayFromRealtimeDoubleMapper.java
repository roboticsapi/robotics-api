/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoublesToRealtimeDoubleArray;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleArray;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleArraySet;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleArrayFragment;

public class DoubleArrayFromRealtimeDoubleMapper
		extends TypedRealtimeValueFragmentFactory<Double[], RealtimeDoublesToRealtimeDoubleArray> {

	public DoubleArrayFromRealtimeDoubleMapper() {
		super(RealtimeDoublesToRealtimeDoubleArray.class);
	}

	@Override
	protected RealtimeValueFragment<Double[]> createFragment(RealtimeDoublesToRealtimeDoubleArray value)
			throws MappingException, RpiException {
		RealtimeValueFragment<Double[]> ret = new RealtimeDoubleArrayFragment(value);
		DoubleArray doubleArray = ret.add(new DoubleArray(value.getSize()));
		OutPort out = doubleArray.getOutArray();
		for (int i = 0; i < value.getSize(); i++) {
			DoubleArraySet set = ret.add(new DoubleArraySet(value.getSize(), i));
			ret.addDependency(value.get(i), ret.addInPort("in" + i, set.getInValue()));
			ret.connect(out, set.getInArray());
			out = set.getOutArray();
		}
		ret.defineResult(out);
		return ret;
	}
}
