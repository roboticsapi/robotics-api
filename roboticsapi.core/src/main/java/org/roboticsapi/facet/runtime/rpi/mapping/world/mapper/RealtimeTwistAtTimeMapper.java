/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwistAtTime;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTwistFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.TwistAtTime;

public class RealtimeTwistAtTimeMapper extends TypedRealtimeValueFragmentFactory<Twist, RealtimeTwistAtTime> {

	public RealtimeTwistAtTimeMapper() {
		super(RealtimeTwistAtTime.class);
	}

	@Override
	protected RealtimeValueFragment<Twist> createFragment(RealtimeTwistAtTime value)
			throws MappingException, RpiException {
		TwistAtTime frame = new TwistAtTime(value.getMaxAge());
		RealtimeTwistFragment ret = new RealtimeTwistFragment(value, frame.getOutValue());
		ret.addDependency(value.getOther(), ret.addInPort("inValue", frame.getInValue()));
		ret.addDependency(value.getAge(), ret.addInPort("inAge", frame.getInAge()));
		return ret;
	}
}
