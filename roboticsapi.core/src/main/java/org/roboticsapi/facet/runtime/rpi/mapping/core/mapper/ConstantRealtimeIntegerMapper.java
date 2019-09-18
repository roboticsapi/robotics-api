/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimeinteger.ConstantRealtimeInteger;
import org.roboticsapi.facet.runtime.rpi.core.primitives.IntValue;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeIntegerFragment;

public class ConstantRealtimeIntegerMapper extends TypedRealtimeValueFragmentFactory<Integer, ConstantRealtimeInteger> {

	public ConstantRealtimeIntegerMapper() {
		super(ConstantRealtimeInteger.class);
	}

	@Override
	protected RealtimeValueFragment<Integer> createFragment(ConstantRealtimeInteger value) throws MappingException {
		return new RealtimeIntegerFragment(value, new IntValue(value.getValue()).getOutValue());
	}

}
