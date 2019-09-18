/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.ConstantRealtimeRotation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeRotationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.RotationFromABC;

public class ConstantRealtimeRotationMapper
		extends TypedRealtimeValueFragmentFactory<Rotation, ConstantRealtimeRotation> {

	public ConstantRealtimeRotationMapper() {
		super(ConstantRealtimeRotation.class);
	}

	@Override
	protected RealtimeValueFragment<Rotation> createFragment(ConstantRealtimeRotation value)
			throws MappingException, RpiException {
		Rotation v = value.getConstantValue();
		return new RealtimeRotationFragment(value, new RotationFromABC(v.getA(), v.getB(), v.getC()).getOutValue());
	}
}
