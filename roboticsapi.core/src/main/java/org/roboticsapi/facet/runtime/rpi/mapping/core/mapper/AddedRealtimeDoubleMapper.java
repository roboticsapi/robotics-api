/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.AddedRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleAdd;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class AddedRealtimeDoubleMapper extends TypedRealtimeValueFragmentFactory<Double, AddedRealtimeDouble> {

	public AddedRealtimeDoubleMapper() {
		super(AddedRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(AddedRealtimeDouble value)
			throws MappingException, RpiException {
		DoubleAdd add = new DoubleAdd();
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, add.getOutValue());
		ret.addDependency(value.getFirstValue(), ret.addInPort("inFirst", add.getInFirst()));
		ret.addDependency(value.getSecondValue(), ret.addInPort("inSecond", add.getInSecond()));
		return ret;
	}

}
