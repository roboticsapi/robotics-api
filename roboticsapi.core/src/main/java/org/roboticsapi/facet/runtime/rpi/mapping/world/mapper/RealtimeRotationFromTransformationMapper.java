/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.TransformationToRealtimeRotation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeRotationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameToPosRot;

public class RealtimeRotationFromTransformationMapper
		extends TypedRealtimeValueFragmentFactory<Rotation, TransformationToRealtimeRotation> {

	public RealtimeRotationFromTransformationMapper() {
		super(TransformationToRealtimeRotation.class);
	}

	@Override
	protected RealtimeValueFragment<Rotation> createFragment(TransformationToRealtimeRotation value)
			throws MappingException, RpiException {
		FrameToPosRot rot = new FrameToPosRot();
		RealtimeRotationFragment ret = new RealtimeRotationFragment(value, rot.getOutRotation());
		ret.addDependency(value.getTransformation(), ret.addInPort("inValue", rot.getInValue()));
		return ret;
	}

}
