/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimeinteger.MultipliedRealtimeInteger;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.IntMultiply;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeIntegerFragment;

public class MultipliedRealtimeIntegerMapper
		extends TypedRealtimeValueFragmentFactory<Integer, MultipliedRealtimeInteger> {

	public MultipliedRealtimeIntegerMapper() {
		super(MultipliedRealtimeInteger.class);
	}

	@Override
	protected RealtimeValueFragment<Integer> createFragment(MultipliedRealtimeInteger value)
			throws MappingException, RpiException {
		IntMultiply add = new IntMultiply();
		RealtimeIntegerFragment ret = new RealtimeIntegerFragment(value, add.getOutValue());
		ret.addDependency(value.getMultiplicand(), ret.addInPort("inFirst", add.getInFirst()));
		ret.addDependency(value.getMultiplier(), ret.addInPort("inSecond", add.getInSecond()));
		return ret;
	}

}
