/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.ConditionalRealtimeTwist;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTwistFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.TwistConditional;

public class ConditionalRealtimeTwistMapper extends TypedRealtimeValueFragmentFactory<Twist, ConditionalRealtimeTwist> {

	public ConditionalRealtimeTwistMapper() {
		super(ConditionalRealtimeTwist.class);
	}

	@Override
	protected RealtimeValueFragment<Twist> createFragment(ConditionalRealtimeTwist value)
			throws MappingException, RpiException {
		TwistConditional cond = new TwistConditional();
		RealtimeTwistFragment ret = new RealtimeTwistFragment(value, cond.getOutValue());
		ret.addDependency(value.getCondition(), ret.addInPort("inCondition", cond.getInCondition()));
		ret.addDependency(value.getIfTrue(), ret.addInPort("inTrue", cond.getInTrue()));
		ret.addDependency(value.getIfFalse(), ret.addInPort("inFalse", cond.getInFalse()));
		return ret;
	}

}
