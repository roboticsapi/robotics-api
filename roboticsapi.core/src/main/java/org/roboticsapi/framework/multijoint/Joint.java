/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.relation.DynamicConnection;

/**
 * This interface represent a joint (i.e. controllable axis, e.g. revolute or
 * prismatic).
 */
public interface Joint extends Actuator {

	/**
	 * Retrieves the fixed frame of the joint.
	 *
	 * @return the fixed frame
	 */
	Frame getFixedFrame();

	/**
	 * Retrieves the moving frame of the joint.
	 *
	 * @return the moving frame
	 */
	Frame getMovingFrame();

	/**
	 * Retrieves the logical connection between the two {@link Frame}s
	 *
	 * @return the logical connection between fixed and moving frame
	 */
	DynamicConnection getLogicalRelation();

	/**
	 * Retrieves the maximum acceleration.
	 *
	 * @return maximum acceleration of the joint
	 */
	double getMaximumAcceleration();

	/**
	 * Retrieves the maximum velocity.
	 *
	 * @return maximum velocity of the joint
	 */
	double getMaximumVelocity();

	/**
	 * Retrieves the minimum position.
	 *
	 * @return minimum position of the joint
	 */
	double getMinimumPosition();

	/**
	 * Retrieves the maximum position.
	 *
	 * @return maximum position of the joint
	 */
	double getMaximumPosition();

	/**
	 * Retrieves the maximum jerk.
	 *
	 * @return maximum jerk of the joint
	 */
	double getMaximumJerk();

	/**
	 * Retrieves the maximum allowed deviation between measured and commanded
	 * position of the joint (rad)
	 *
	 * @return the maximum allowed deviation between measured and commanded position
	 *         of the joint (rad)
	 */
	double getAllowedPositionError();

	/**
	 * Retrieves the home position of the joint.
	 *
	 * @return home position of the joint
	 */
	double getHomePosition();

	/**
	 * Retrieves the commanded position of the joint.
	 *
	 * @return the currently commanded position of the joint.
	 * @throws RoboticsException if the value can not be determined
	 */
	double getCommandedPosition() throws RoboticsException;

	/**
	 * Retrieves the measured position of the joint.
	 *
	 * @return the currently measured position of the joint.
	 * @throws RoboticsException if the value can not be determined
	 */
	double getMeasuredPosition() throws RoboticsException;

	/**
	 * Retrieves a RealtimeDouble for the (commanded) value of the joint.
	 *
	 * @return RealtimeDouble delivering the joint's commanded position.
	 */
	RealtimeDouble getCommandedRealtimePosition();

	/**
	 * Retrieves a RealtimeDouble for the (measured) value of the joint.
	 *
	 * @return RealtimeDouble delivering the joint's measured position.
	 */
	RealtimeDouble getMeasuredRealtimePosition();

	/**
	 * Retrieves a RealtimeDouble for the (commanded) velocity value of the joint.
	 *
	 * @return RealtimeDouble delivering the joint's commanded velocity.
	 */
	RealtimeDouble getCommandedRealtimeVelocity();

	/**
	 * Retrieves a RealtimeDouble for the (measured) velocity value of the joint.
	 *
	 * @return RealtimeDouble delivering the joint's measured velocity.
	 */
	RealtimeDouble getMeasuredRealtimeVelocity();

	/**
	 * Retrieves a forward kinematics RealtimeValue to calculate the transformation
	 * of the joint based on the joint position given by a RealtimeDouble.
	 *
	 * @param position joint position
	 * @return transformation for the given position
	 */
	RealtimeTransformation getRealtimeForwardKinematics(RealtimeDouble position);

	@Override
	public JointDriver getDriver();

}