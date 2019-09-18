/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.ConditionalRealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTransformationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameConditional;

public class ConditionalRealtimeTransformationMapper
		extends TypedRealtimeValueFragmentFactory<Transformation, ConditionalRealtimeTransformation> {

	public ConditionalRealtimeTransformationMapper() {
		super(ConditionalRealtimeTransformation.class);
	}

	@Override
	protected RealtimeValueFragment<Transformation> createFragment(ConditionalRealtimeTransformation value)
			throws MappingException, RpiException {
		FrameConditional cond = new FrameConditional();
		RealtimeTransformationFragment ret = new RealtimeTransformationFragment(value, cond.getOutValue());
		ret.addDependency(value.getCondition(), ret.addInPort("inCondition", cond.getInCondition()));
		ret.addDependency(value.getIfTrue(), ret.addInPort("inTrue", cond.getInTrue()));
		ret.addDependency(value.getIfFalse(), ret.addInPort("inFalse", cond.getInFalse()));
		return ret;
	}

}
