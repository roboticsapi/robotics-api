/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimeinteger.AddedRealtimeInteger;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.IntAdd;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeIntegerFragment;

public class AddedRealtimeIntegerMapper extends TypedRealtimeValueFragmentFactory<Integer, AddedRealtimeInteger> {

	public AddedRealtimeIntegerMapper() {
		super(AddedRealtimeInteger.class);
	}

	@Override
	protected RealtimeValueFragment<Integer> createFragment(AddedRealtimeInteger value)
			throws MappingException, RpiException {
		IntAdd add = new IntAdd();
		RealtimeIntegerFragment ret = new RealtimeIntegerFragment(value, add.getOutValue());
		ret.addDependency(value.getAddend1(), ret.addInPort("inFirst", add.getInFirst()));
		ret.addDependency(value.getAddend2(), ret.addInPort("inSecond", add.getInSecond()));
		return ret;
	}

}
