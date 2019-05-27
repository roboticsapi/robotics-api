/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper.fragments;

import org.roboticsapi.runtime.core.primitives.Interval;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.primitives.FrameConditional;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;

public class CartesianConditionalFragment extends NetFragment implements CartesianMotionFragment {

	private final DataflowOutPort result;
	private final CartesianMotionFragment main;
	private final CartesianMotionFragment other;
	private final double start;
	private final double duration;

	public CartesianConditionalFragment(CartesianMotionFragment main, CartesianMotionFragment other, double start,
			double duration, DataflowOutPort time) throws MappingException, RPIException {
		super("JointJoiner");
		this.main = main;
		this.other = other;
		this.start = start;
		this.duration = duration;

		Interval progress = add(new Interval(start, start + duration));
		connect(time, addInPort(new DoubleDataflow(), true, progress.getInValue()));
		FrameConditional cond = add(new FrameConditional());
		cond.getInCondition().connectTo(progress.getOutActive());

		connect(main.getResultPort(), addInPort(main.getResultPort().getType(), false, cond.getInTrue()));
		connect(other.getResultPort(), addInPort(other.getResultPort().getType(), false, cond.getInFalse()));
		result = addOutPort(main.getResultPort().getType(), false, cond.getOutValue());
	}

	@Override
	public DataflowOutPort getResultPort() {
		return result;
	}

	@Override
	public Transformation getTransformationAt(double time) {
		if (time >= start && time < start + duration) {
			return main.getTransformationAt(time);
		} else {
			return other.getTransformationAt(time);
		}
	}

	@Override
	public Twist getTwistAt(double time) {
		if (time >= start && time < start + duration) {
			return main.getTwistAt(time);
		} else {
			return other.getTwistAt(time);
		}
	}
}
