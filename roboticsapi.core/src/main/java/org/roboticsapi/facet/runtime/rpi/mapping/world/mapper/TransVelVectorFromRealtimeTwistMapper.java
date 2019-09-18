/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.GetLinearVelocityFromTwistRealtimeVector;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeVectorFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.TwistToVelocities;

public class TransVelVectorFromRealtimeTwistMapper
		extends TypedRealtimeValueFragmentFactory<Vector, GetLinearVelocityFromTwistRealtimeVector> {

	public TransVelVectorFromRealtimeTwistMapper() {
		super(GetLinearVelocityFromTwistRealtimeVector.class);
	}

	@Override
	protected RealtimeValueFragment<Vector> createFragment(GetLinearVelocityFromTwistRealtimeVector value)
			throws MappingException, RpiException {
		TwistToVelocities vel = new TwistToVelocities();
		RealtimeVectorFragment ret = new RealtimeVectorFragment(value, vel.getOutTransVel());
		ret.addDependency(value.getTwist(), ret.addInPort("inValue", vel.getInValue()));
		return ret;
	}
}
