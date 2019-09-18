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
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

/**
 * Relation belonging to a prismatic joint
 */
public class PrismaticConnection extends SingleDofConnection {

	private final Vector movementAxis;

	public PrismaticConnection(Frame from, Frame to) {
		this(from, to, new Vector(0, 0, 1));
	}

	public PrismaticConnection(Frame from, Frame to, Vector movementAxis) {
		super(from, to);
		this.movementAxis = movementAxis;
	}

	@Override
	public RealtimeTransformation getTransformationForState(Map<StateVariable, RealtimeDouble> state) {
		RealtimeTransformation trans = RealtimeTransformation.createFromVectorRotation(
				movementAxis.asRealtimeValue().scale(state.get(position)), RealtimeRotation.IDENTITY);
		return trans;
	}

	@Override
	public RealtimeTwist getTwistForState(Map<StateVariable, RealtimeDouble> state) {
		RealtimeTwist twist = RealtimeTwist.createFromLinearAngular(
				movementAxis.asRealtimeValue().scale(state.get(velocity)), RealtimeVector.ZERO);
		return twist;
	}

	@Override
	public Map<StateVariable, RealtimeDouble> getStateForTransformation(RealtimeTransformation pose,
			FrameTopology topology) throws TransformationException {
		Map<StateVariable, RealtimeDouble> ret = new HashMap<>();
		ret.put(position, pose.getTranslation().getLength().divide(movementAxis.getLength()));
		return ret;
	}

	@Override
	public Map<StateVariable, RealtimeDouble> getStateForTwist(RealtimeTransformation pose, RealtimeTwist vel,
			FrameTopology topology) throws TransformationException {
		Map<StateVariable, RealtimeDouble> ret = new HashMap<>();
		ret.put(velocity, vel.getTranslationVelocity().getLength().divide(movementAxis.getLength()));
		return ret;
	}

}
