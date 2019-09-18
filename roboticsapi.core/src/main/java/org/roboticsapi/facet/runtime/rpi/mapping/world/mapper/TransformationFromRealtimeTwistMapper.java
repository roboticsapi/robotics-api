/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwistToRealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTransformationFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameAddTwist;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameConditional;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameIsNull;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FramePre;
import org.roboticsapi.facet.runtime.rpi.world.primitives.FrameToPosRot;
import org.roboticsapi.facet.runtime.rpi.world.primitives.TwistRotate;

public class TransformationFromRealtimeTwistMapper
		extends TypedRealtimeValueFragmentFactory<Transformation, RealtimeTwistToRealtimeTransformation> {

	public TransformationFromRealtimeTwistMapper() {
		super(RealtimeTwistToRealtimeTransformation.class);
	}

	@Override
	protected RealtimeValueFragment<Transformation> createFragment(RealtimeTwistToRealtimeTransformation value)
			throws MappingException, RpiException {
		RealtimeTransformationFragment ret = new RealtimeTransformationFragment(value);

		FramePre pre = ret.add(new FramePre());
		FrameIsNull isNull = ret.add(new FrameIsNull());
		ret.connect(pre.getOutValue(), isNull.getInValue());

		FrameConditional cond = ret.add(new FrameConditional());
		ret.connect(pre.getOutValue(), cond.getInFalse());
		ret.connect(isNull.getOutValue(), cond.getInCondition());
		ret.addDependency(value.getInitialTransformation(), ret.addInPort("inInitial", cond.getInTrue()));

		FrameToPosRot invrot = ret.add(new FrameToPosRot());
		ret.connect(cond.getOutValue(), invrot.getInValue());

		TwistRotate rot = ret.add(new TwistRotate());
		ret.connect(invrot.getOutRotation(), rot.getInRot());
		ret.addDependency(value.getTwist(), ret.addInPort("inTwist", rot.getInValue()));

		FrameAddTwist add = ret.add(new FrameAddTwist());
		ret.connect(rot.getOutValue(), add.getInTwist());
		ret.connect(cond.getOutValue(), add.getInFrame());
		ret.connect(add.getOutValue(), pre.getInValue());

		ret.defineResult(add.getOutValue());
		return ret;
	}

}
