/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.MultipliedRealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTransformationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameTransform;

public class MultipliedRealtimeTransformationMapper
		extends TypedRealtimeValueFragmentFactory<Transformation, MultipliedRealtimeTransformation> {

	public MultipliedRealtimeTransformationMapper() {
		super(MultipliedRealtimeTransformation.class);
	}

	@Override
	protected RealtimeValueFragment<Transformation> createFragment(MultipliedRealtimeTransformation value)
			throws MappingException, RpiException {

		FrameTransform trans = new FrameTransform();
		RealtimeTransformationFragment ret = new RealtimeTransformationFragment(value, trans.getOutValue());
		ret.addDependency(value.getFirstTransformation(), ret.addInPort("inFirst", trans.getInFirst()));
		ret.addDependency(value.getSecondTransformation(), ret.addInPort("inSecond", trans.getInSecond()));
		return ret;
	}
}
