/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper.fragments;

import org.roboticsapi.runtime.core.primitives.BooleanConditional;
import org.roboticsapi.runtime.core.primitives.BooleanIsNull;
import org.roboticsapi.runtime.core.primitives.BooleanValue;
import org.roboticsapi.runtime.core.primitives.DoubleConditional;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.parts.AndFragment;
import org.roboticsapi.runtime.mapping.parts.DoubleInterpolateFragment;
import org.roboticsapi.runtime.mapping.parts.OrFragment;
import org.roboticsapi.runtime.rpi.RPIException;

public abstract class CancellableMotionFragment extends NetFragment {

	private final DataflowOutPort interpolatedOverridePort;
	// protected DataflowOutPort completedPort;
	private DataflowOutPort motionCompletedPort;
	private final DataflowOutPort cancelCompleted;

	public CancellableMotionFragment(String name, ActionMappingContext ports, double cancelVelocity)
			throws MappingException {
		super(name);

		// as cancel may be null, we interpret this as false
		BooleanIsNull nullChecker = add(new BooleanIsNull());
		connect(ports.cancelPort, addInPort(new StateDataflow(), true, nullChecker.getInValue()));
		BooleanConditional cancelChecker = add(new BooleanConditional());
		BooleanValue constantFalse = add(new BooleanValue(false));
		connect(ports.cancelPort, addInPort(new StateDataflow(), true, cancelChecker.getInFalse()));

		// we use a default override multiplier of 1 in case cancel is null
		DoubleConditional overrideGoalCombiner = add(new DoubleConditional(0d, 1d));
		try {
			cancelChecker.getInCondition().connectTo(nullChecker.getOutValue());
			cancelChecker.getInTrue().connectTo(constantFalse.getOutValue());
			overrideGoalCombiner.getInCondition().connectTo(cancelChecker.getOutValue());
		} catch (RPIException e) {
			throw new MappingException(e);
		}

		DataflowOutPort cancelPort = addOutPort(new StateDataflow(), true, cancelChecker.getOutValue());

		// if cancelled, we activate an interpolation fragment
		DoubleInterpolateFragment overrideInterpolator = add(new DoubleInterpolateFragment(
				"Override DoubleInterpolateFragment", cancelPort, 1, 0, 0.01, cancelVelocity));

		// if cancelled, we use the interpolated value, otherwise the default
		// value, see above
		connect(overrideInterpolator.getInterpolatedValue(),
				addInPort(new DoubleDataflow(), true, overrideGoalCombiner.getInTrue()));

		overrideGoalCombiner.getInTrue().setDebug(10);

		DataflowOutPort overrideMultiplier = addOutPort(new DoubleDataflow(), true, overrideGoalCombiner.getOutValue());
		DoubleMultiply mult = add(new DoubleMultiply());

		connect(overrideMultiplier, addInPort(new DoubleDataflow(), true, mult.getInFirst()));
		connect(ports.overridePort, addInPort(new DoubleDataflow(), true, mult.getInSecond()));

		interpolatedOverridePort = addOutPort(new DoubleDataflow(), true, mult.getOutValue());

		// completion logic
		AndFragment cancelAnd = add(
				new AndFragment(new StateDataflow(), cancelPort, overrideInterpolator.getDonePort()));
		cancelCompleted = cancelAnd.getAndOut();
	}

	protected DataflowOutPort getInterpolatedOverridePort() {
		return interpolatedOverridePort;
	}

	protected void setMotionCompletedPort(DataflowOutPort motionCompletedPort) {
		this.motionCompletedPort = motionCompletedPort;
	}

	public DataflowOutPort getCompletedPort() throws MappingException {
		if (motionCompletedPort == null) {
			throw new IllegalStateException("motionCompletedPort is null, call setMotionCompletedPort() first");
		}

		OrFragment completedOr = add(new OrFragment(new StateDataflow(), motionCompletedPort, getCancelCompleted()));

		return completedOr.getOrOut();
	}

	public DataflowOutPort getCancelCompleted() {
		return cancelCompleted;
	}

	public DataflowOutPort getMotionCompleted() {
		return motionCompletedPort;
	}

}
