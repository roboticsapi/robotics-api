/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.VectorToRealtimeTwist;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTwistFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.TwistFromVelocities;

public class RealtimeTwistFromComponentsMapper extends TypedRealtimeValueFragmentFactory<Twist, VectorToRealtimeTwist> {

	public RealtimeTwistFromComponentsMapper() {
		super(VectorToRealtimeTwist.class);
	}

	@Override
	protected RealtimeValueFragment<Twist> createFragment(VectorToRealtimeTwist value)
			throws MappingException, RpiException {
		TwistFromVelocities twist = new TwistFromVelocities();
		RealtimeTwistFragment ret = new RealtimeTwistFragment(value, twist.getOutValue());
		ret.addDependency(value.getTranslationVelocity(), ret.addInPort("inTrans", twist.getInTransVel()));
		ret.addDependency(value.getRotationVelocity(), ret.addInPort("inRot", twist.getInRotVel()));
		return ret;
	}

}
