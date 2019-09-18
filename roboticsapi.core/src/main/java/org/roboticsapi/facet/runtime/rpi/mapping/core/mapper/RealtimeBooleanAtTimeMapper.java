/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.core.mapper;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBooleanAtTime;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.BooleanAtTime;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeBooleanFragment;

public class RealtimeBooleanAtTimeMapper extends TypedRealtimeValueFragmentFactory<Boolean, RealtimeBooleanAtTime> {

	public RealtimeBooleanAtTimeMapper() {
		super(RealtimeBooleanAtTime.class);
	}

	@Override
	protected RealtimeValueFragment<Boolean> createFragment(RealtimeBooleanAtTime value)
			throws MappingException, RpiException {
		BooleanAtTime atTime = new BooleanAtTime(value.getMaxAge());
		RealtimeBooleanFragment ret = new RealtimeBooleanFragment(value, atTime.getOutValue());
		ret.addDependency(value.getOtherValue(), ret.addInPort("inValue", atTime.getInValue()));
		ret.addDependency(value.getAge(), ret.addInPort("inAge", atTime.getInAge()));
		return ret;
	}

}
