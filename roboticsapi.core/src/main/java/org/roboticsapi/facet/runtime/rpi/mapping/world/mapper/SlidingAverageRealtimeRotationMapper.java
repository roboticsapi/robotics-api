/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.SlidingAverageRealtimeRotation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeRotationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.RotationAverage;

public class SlidingAverageRealtimeRotationMapper
		extends TypedRealtimeValueFragmentFactory<Rotation, SlidingAverageRealtimeRotation> {

	public SlidingAverageRealtimeRotationMapper() {
		super(SlidingAverageRealtimeRotation.class);
	}

	@Override
	protected RealtimeValueFragment<Rotation> createFragment(SlidingAverageRealtimeRotation value)
			throws MappingException, RpiException {
		RotationAverage rot = new RotationAverage(value.getDuration());
		RealtimeRotationFragment ret = new RealtimeRotationFragment(value, rot.getOutValue());
		ret.addDependency(value.getOther(), ret.addInPort("inValue", rot.getInValue()));
		return ret;
	}
}
