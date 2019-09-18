/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.QuaternionToRealtimeRotation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeRotationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.RotationFromQuaternion;

public class RealtimeRotationFromQuaternionMapper
		extends TypedRealtimeValueFragmentFactory<Rotation, QuaternionToRealtimeRotation> {

	public RealtimeRotationFromQuaternionMapper() {
		super(QuaternionToRealtimeRotation.class);
	}

	@Override
	protected RealtimeValueFragment<Rotation> createFragment(QuaternionToRealtimeRotation value)
			throws MappingException, RpiException {

		RotationFromQuaternion rot = new RotationFromQuaternion();
		RealtimeRotationFragment ret = new RealtimeRotationFragment(value, rot.getOutValue());
		ret.addDependency(value.getQuaternionX(), ret.addInPort("inX", rot.getInX()));
		ret.addDependency(value.getQuaternionY(), ret.addInPort("inY", rot.getInY()));
		ret.addDependency(value.getQuaternionZ(), ret.addInPort("inZ", rot.getInZ()));
		ret.addDependency(value.getQuaternionW(), ret.addInPort("inW", rot.getInW()));
		return ret;
	}

}
