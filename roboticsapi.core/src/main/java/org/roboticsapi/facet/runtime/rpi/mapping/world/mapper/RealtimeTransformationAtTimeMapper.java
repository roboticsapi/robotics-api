/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformationAtTime;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTransformationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameAtTime;

public class RealtimeTransformationAtTimeMapper
		extends TypedRealtimeValueFragmentFactory<Transformation, RealtimeTransformationAtTime> {

	public RealtimeTransformationAtTimeMapper() {
		super(RealtimeTransformationAtTime.class);
	}

	@Override
	protected RealtimeValueFragment<Transformation> createFragment(RealtimeTransformationAtTime value)
			throws MappingException, RpiException {
		FrameAtTime frame = new FrameAtTime(value.getMaxAge());
		RealtimeTransformationFragment ret = new RealtimeTransformationFragment(value, frame.getOutValue());
		ret.addDependency(value.getOther(), ret.addInPort("inValue", frame.getInValue()));
		ret.addDependency(value.getAge(), ret.addInPort("inAge", frame.getInAge()));
		return ret;
	}
}
