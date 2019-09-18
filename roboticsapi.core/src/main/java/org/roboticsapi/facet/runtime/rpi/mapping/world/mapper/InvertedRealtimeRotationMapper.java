/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.InvertedRealtimeRotation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeRotationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.RotationInvert;

public class InvertedRealtimeRotationMapper
		extends TypedRealtimeValueFragmentFactory<Rotation, InvertedRealtimeRotation> {

	public InvertedRealtimeRotationMapper() {
		super(InvertedRealtimeRotation.class);
	}

	@Override
	protected RealtimeValueFragment<Rotation> createFragment(InvertedRealtimeRotation value)
			throws MappingException, RpiException {

		RotationInvert inv = new RotationInvert();
		RealtimeRotationFragment ret = new RealtimeRotationFragment(value, inv.getOutValue());
		ret.addDependency(value.getOther(), ret.addInPort("inValue", inv.getInValue()));
		return ret;
	}

}
