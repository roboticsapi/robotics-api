/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.GetTranslationFromTransformationRealtimeVector;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeVectorFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameToPosRot;

public class VectorFromRealtimeTransformationMapper
		extends TypedRealtimeValueFragmentFactory<Vector, GetTranslationFromTransformationRealtimeVector> {

	public VectorFromRealtimeTransformationMapper() {
		super(GetTranslationFromTransformationRealtimeVector.class);
	}

	@Override
	protected RealtimeValueFragment<Vector> createFragment(GetTranslationFromTransformationRealtimeVector value)
			throws MappingException, RpiException {

		FrameToPosRot vec = new FrameToPosRot();
		RealtimeVectorFragment ret = new RealtimeVectorFragment(value, vec.getOutPosition());
		ret.addDependency(value.getTransformation(), ret.addInPort("inValue", vec.getInValue()));
		return ret;
	}
}
