/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformationToRealtimeTwist;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTwistFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameIsNull;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FramePre;
import org.roboticsapi.facet.runtime.rpi.world.primitives.TwistFromFrames;
import org.roboticsapi.facet.runtime.rpi.world.primitives.TwistSetNull;

public class TwistFromRealtimeTransformationMapper
		extends TypedRealtimeValueFragmentFactory<Twist, RealtimeTransformationToRealtimeTwist> {

	public TwistFromRealtimeTransformationMapper() {
		super(RealtimeTransformationToRealtimeTwist.class);
	}

	@Override
	protected RealtimeValueFragment<Twist> createFragment(RealtimeTransformationToRealtimeTwist value)
			throws MappingException, RpiException {
		RealtimeTwistFragment ret = new RealtimeTwistFragment(value);
		FramePre pre = ret.add(new FramePre());
		ret.addDependency(value.getTransformation(), ret.addInPort("inFrame1", pre.getInValue()));
		FrameIsNull isNull = ret.add(new FrameIsNull());
		ret.connect(pre.getOutValue(), isNull.getInValue());

		TwistFromFrames twist = ret.add(new TwistFromFrames());
		ret.connect(pre.getOutValue(), twist.getInPrevFrame());
		ret.addDependency(value.getTransformation(), ret.addInPort("inFrame2", twist.getInFrame()));

		TwistSetNull twistOrNull = ret.add(new TwistSetNull());
		ret.connect(twist.getOutValue(), twistOrNull.getInValue());
		ret.connect(isNull.getOutValue(), twistOrNull.getInNull());

		ret.defineResult(twistOrNull.getOutValue());
		return ret;
	}
}
