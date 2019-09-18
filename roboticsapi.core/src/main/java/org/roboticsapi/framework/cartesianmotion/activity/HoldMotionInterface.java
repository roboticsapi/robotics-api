/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.activity;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;

/**
 * The Interface HoldMotionInterface creates RtActivities that let Actuators
 * hold certain poses.
 */
public interface HoldMotionInterface extends ActuatorInterface {

	/**
	 * Creates an {@link RtActivity} that lets the {@link Actuator} hold the given
	 * position, specified as a Frame.
	 *
	 * The Actuator is expected to have its motion center already located at the
	 * specified position. The Actuator will try to follow all motions of the given
	 * Frame without any interpolation.
	 *
	 * @param position   the position to hold
	 * @param parameters DeviceParameters to use for RtActivity execution
	 * @return the RtActivity that lets the Actuator hold the given position
	 * @throws RoboticsException thrown if RtActivity creation failed
	 */
	Activity holdCartesianPosition(Pose position, DeviceParameters... parameters) throws RoboticsException;

	Activity holdCartesianPosition(RealtimePose position, DeviceParameters... parameters) throws RoboticsException;
}
