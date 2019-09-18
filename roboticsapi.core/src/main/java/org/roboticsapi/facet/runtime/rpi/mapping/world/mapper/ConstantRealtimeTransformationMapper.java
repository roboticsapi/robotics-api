/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.ConstantRealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTransformationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameFromPosRot;

public class ConstantRealtimeTransformationMapper
		extends TypedRealtimeValueFragmentFactory<Transformation, ConstantRealtimeTransformation> {

	public ConstantRealtimeTransformationMapper() {
		super(ConstantRealtimeTransformation.class);
	}

	@Override
	protected RealtimeValueFragment<Transformation> createFragment(ConstantRealtimeTransformation value)
			throws MappingException, RpiException {

		Transformation v = value.getTransformation();
		return new RealtimeTransformationFragment(value,
				new FrameFromPosRot(v.getTranslation().getX(), v.getTranslation().getY(), v.getTranslation().getZ(),
						v.getRotation().getA(), v.getRotation().getB(), v.getRotation().getC()).getOutValue());
	}
}
