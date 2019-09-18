/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.ConstantRealtimeTwist;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTwistFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.TwistFromVelocities;

public class ConstantRealtimeTwistMapper extends TypedRealtimeValueFragmentFactory<Twist, ConstantRealtimeTwist> {

	public ConstantRealtimeTwistMapper() {
		super(ConstantRealtimeTwist.class);
	}

	@Override
	protected RealtimeValueFragment<Twist> createFragment(ConstantRealtimeTwist value)
			throws MappingException, RpiException {
		Twist v = value.getConstantValue();
		return new RealtimeTwistFragment(value,
				new TwistFromVelocities(v.getTransVel().getX(), v.getTransVel().getY(), v.getTransVel().getZ(),
						v.getRotVel().getX(), v.getRotVel().getY(), v.getRotVel().getZ()).getOutValue());
	}
}
