/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimeinteger.EqualRealtimeInteger;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.IntEquals;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;

public class EqualRealtimeIntegerMapper extends TypedRealtimeValueFragmentFactory<Boolean, EqualRealtimeInteger> {

	public EqualRealtimeIntegerMapper() {
		super(EqualRealtimeInteger.class);
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(EqualRealtimeInteger value)
			throws MappingException, RpiException {
		IntEquals equals = new IntEquals();
		RealtimeBooleanFragment ret = new RealtimeBooleanFragment(value, equals.getOutValue());
		ret.addDependency(value.getFirst(), ret.addInPort("inFirst", equals.getInFirst()));
		ret.addDependency(value.getSecond(), ret.addInPort("inSecond", equals.getInSecond()));
		return ret;
	}

}
