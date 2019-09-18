/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.GetFromArrayRealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTransformationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameArrayGet;

public class RealtimeTransformationFromArrayMapper
		extends TypedRealtimeValueFragmentFactory<Transformation, GetFromArrayRealtimeTransformation> {

	public RealtimeTransformationFromArrayMapper() {
		super(GetFromArrayRealtimeTransformation.class);
	}

	@Override
	protected RealtimeValueFragment<Transformation> createFragment(GetFromArrayRealtimeTransformation value)
			throws MappingException, RpiException {

		FrameArrayGet get = new FrameArrayGet(value.getTransformationArray().getSize(), value.getIndex());
		RealtimeTransformationFragment ret = new RealtimeTransformationFragment(value, get.getOutValue());
		ret.addDependency(value.getTransformationArray(), ret.addInPort("inValue", get.getInArray()));
		return ret;
	}
}
