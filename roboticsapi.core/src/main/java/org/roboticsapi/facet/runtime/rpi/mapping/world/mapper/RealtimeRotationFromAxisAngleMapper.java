/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.AxisAngleToRealtimeRotation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeRotationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.RotationFromAxisAngle;

public class RealtimeRotationFromAxisAngleMapper
		extends TypedRealtimeValueFragmentFactory<Rotation, AxisAngleToRealtimeRotation> {

	public RealtimeRotationFromAxisAngleMapper() {
		super(AxisAngleToRealtimeRotation.class);
	}

	@Override
	protected RealtimeValueFragment<Rotation> createFragment(AxisAngleToRealtimeRotation value)
			throws MappingException, RpiException {
		RotationFromAxisAngle rot = new RotationFromAxisAngle();
		RealtimeRotationFragment ret = new RealtimeRotationFragment(value, rot.getOutValue());
		ret.addDependency(value.getAxis(), ret.addInPort("inAxis", rot.getInAxis()));
		ret.addDependency(value.getAngle(), ret.addInPort("inAngle", rot.getInAngle()));
		return ret;
	}

}
