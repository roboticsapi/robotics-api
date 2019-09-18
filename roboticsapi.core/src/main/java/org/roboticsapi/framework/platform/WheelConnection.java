/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.StateVariable;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.relation.DynamicConnection;

public class WheelConnection extends DynamicConnection {
	private final StateVariable pos = addPositionStateVariable("pos"), vel = addVelocityStateVariable("vel");

	protected WheelConnection(Frame from, Frame to) {
		super(from, to);
		pos.addFlowExpression(vel.asExpression());
	}

	@Override
	public RealtimeTransformation getTransformationForState(Map<StateVariable, RealtimeDouble> state) {
		return RealtimeTransformation.createFromXYZABC(RealtimeDouble.ZERO, RealtimeDouble.ZERO, RealtimeDouble.ZERO,
				state.get(pos), RealtimeDouble.ZERO, RealtimeDouble.ZERO);
	}

	@Override
	public RealtimeTwist getTwistForState(Map<StateVariable, RealtimeDouble> state) {
		return RealtimeTwist.createFromLinearAngular(RealtimeDouble.ZERO, RealtimeDouble.ZERO, RealtimeDouble.ZERO,
				RealtimeDouble.ZERO, RealtimeDouble.ZERO, state.get(vel));
	}

	@Override
	public Map<StateVariable, RealtimeDouble> getStateForTransformation(RealtimeTransformation pose,
			FrameTopology topology) throws TransformationException {
		Map<StateVariable, RealtimeDouble> ret = new HashMap<>();
		ret.put(pos, pose.getA());
		return ret;
	}

	@Override
	public Map<StateVariable, RealtimeDouble> getStateForTwist(RealtimeTransformation pose, RealtimeTwist velocity,
			FrameTopology topology) throws TransformationException {
		Map<StateVariable, RealtimeDouble> ret = new HashMap<>();
		ret.put(vel, velocity.getRotationVelocity().getZ());
		return ret;
	}

}
