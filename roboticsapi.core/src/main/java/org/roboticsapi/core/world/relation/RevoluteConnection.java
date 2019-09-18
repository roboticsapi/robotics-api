/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.relation;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.StateVariable;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;

/**
 * Relation belonging to a prismatic joint
 */
public class RevoluteConnection extends SingleDofConnection {

	public RevoluteConnection(Frame from, Frame to) {
		super(from, to);
	}

	@Override
	public RealtimeTransformation getTransformationForState(Map<StateVariable, RealtimeDouble> state) {
		RealtimeTransformation trans = RealtimeTransformation.createFromXYZABC(RealtimeDouble.ZERO, RealtimeDouble.ZERO,
				RealtimeDouble.ZERO, state.get(position), RealtimeDouble.ZERO, RealtimeDouble.ZERO);
		return trans;
	}

	@Override
	public RealtimeTwist getTwistForState(Map<StateVariable, RealtimeDouble> state) {
		RealtimeTwist twist = RealtimeTwist.createFromLinearAngular(RealtimeDouble.ZERO, RealtimeDouble.ZERO,
				RealtimeDouble.ZERO, RealtimeDouble.ZERO, RealtimeDouble.ZERO, state.get(velocity));
		return twist;
	}

	@Override
	public Map<StateVariable, RealtimeDouble> getStateForTransformation(RealtimeTransformation pose,
			FrameTopology topology) throws TransformationException {
		Map<StateVariable, RealtimeDouble> ret = new HashMap<>();
		ret.put(position, pose.getA());
		return ret;
	}

	@Override
	public Map<StateVariable, RealtimeDouble> getStateForTwist(RealtimeTransformation pose, RealtimeTwist vel,
			FrameTopology topology) throws TransformationException {
		Map<StateVariable, RealtimeDouble> ret = new HashMap<>();
		ret.put(position, vel.getRotationVelocity().getZ());
		return ret;
	}

}
