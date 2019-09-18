/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimedouble.ConstantRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleValue;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;

public class ConstantRealtimeDoubleMapper extends TypedRealtimeValueFragmentFactory<Double, ConstantRealtimeDouble> {

	public ConstantRealtimeDoubleMapper() {
		super(ConstantRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(ConstantRealtimeDouble value) throws MappingException {
		return new RealtimeDoubleFragment(value, new DoubleValue(value.getValue()).getOutValue());
	}

}
