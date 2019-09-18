/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.LogicalRelation;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.StateVariable;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;

/**
 * A Robotics API world model dynamic connection (e.g. representing a device)
 */
public abstract class DynamicConnection extends LogicalRelation {
	private final List<StateVariable> positionStateVariables = new ArrayList<>();
	private final List<StateVariable> velocityStateVariables = new ArrayList<>();

	/**
	 * Creates a new dynamic connection
	 */
	protected DynamicConnection(Frame from, Frame to) {
		super(from, to);
	}

	protected StateVariable addPositionStateVariable(StateVariable variable) {
		positionStateVariables.add(variable);
		return variable;
	}

	protected StateVariable addPositionStateVariable(String name) {
		return addPositionStateVariable(new StateVariable(name));
	}

	protected StateVariable addVelocityStateVariable(StateVariable variable) {
		velocityStateVariables.add(variable);
		return variable;
	}

	protected StateVariable addVelocityStateVariable(String name) {
		return addVelocityStateVariable(new StateVariable(name));
	}

	@Override
	public boolean isVariable() {
		return true;
	}

	@Override
	public boolean isPersistent() {
		return true;
	}

	/**
	 * Provides the {@link StateVariable}s required to define the position.
	 *
	 * @return List of {@link StateVariable}s
	 */
	public List<StateVariable> getPositionStateVariables() {
		return positionStateVariables;
	}

	/**
	 * Provides the {@link StateVariable}s required to define the velocity.
	 *
	 * @return List of {@link StateVariable}
	 */
	public List<StateVariable> getVelocityStateVariables() {
		return velocityStateVariables;
	}

	/**
	 * Provides the {@link Transformation} for given values of
	 * {@link StateVariable}s.
	 *
	 * @param state the system state; should provide values for all
	 *              {@link StateVariable}s returned in
	 *              {@link #getPositionStateVariables()}
	 * @return {@link RealtimeTransformation} for the given state
	 */
	public abstract RealtimeTransformation getTransformationForState(Map<StateVariable, RealtimeDouble> state);

	/**
	 * Provides the {@link RealtimeTwist} for given values of
	 * {@link StateVariable}s.
	 *
	 * @param state the system state, should provide values for all
	 *              {@link StateVariable}s returned in
	 *              {@link #getPositionStateVariables()} and
	 *              {@link #getVelocityStateVariables()}
	 * @return {@link RealtimeTwist} for the given state
	 */
	public abstract RealtimeTwist getTwistForState(Map<StateVariable, RealtimeDouble> state);

	/**
	 * Computes the values of the {@link StateVariable}s for a given
	 * {@link Transformation}.
	 *
	 * @param transformation {@link RealtimeTransformation} of the {@link Relation}
	 * @param topology       {@link FrameTopology} to use if the pose has to be
	 *                       converted
	 * @return {@link StateVariable}s resulting in this pose
	 * @throws TransformationException if the pose cannot be converted into the
	 *                                 required form
	 */
	public abstract Map<StateVariable, RealtimeDouble> getStateForTransformation(RealtimeTransformation transformation,
			FrameTopology topology) throws TransformationException;

	/**
	 * Computes the values of the {@link StateVariable}s for a given
	 * {@link Transformation} and {@link Twist}.
	 *
	 * @param transformation {@link RealtimeTransformation} of the {@link Relation}
	 * @param twist          {@link RealtimeTwist} of the {@link Relation}
	 * @param topology       {@link FrameTopology} to use if the pose or velocity
	 *                       has to be converted
	 * @return {@link StateVariable}s resulting in this velocity
	 * @throws TransformationException if the velocity cannot be converted into the
	 *                                 required form
	 */
	public abstract Map<StateVariable, RealtimeDouble> getStateForTwist(RealtimeTransformation transformation,
			RealtimeTwist twist, FrameTopology topology) throws TransformationException;

}
