/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.activity;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.TeachingInfo;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.robot.RobotArm;

public interface TeachingInterface extends ActuatorInterface {

	/**
	 * Gets hint joint information (if any) stored in the given {@link Frame} for
	 * this LinInterface's {@link Actuator}.
	 *
	 * @param f          the Frame to extract hint joints from
	 * @param parameters {@link DeviceParameters} to consider when looking for hint
	 *                   joints (e.g. {@link MotionCenterParameter})
	 * @return the hint joints stored in the given Frame
	 * @throws RoboticsException
	 */
	public abstract double[] getHintJointsFromFrame(Pose f, DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates a new temporary {@link Frame} that stores the position of the
	 * {@link RobotArm}'s motion center relative to its base Frame. The created
	 * Frame is augmented by {@link TeachingInfo}, storing the current Robot pose.
	 *
	 * @return the taught Frame
	 * @throws RoboticsException thrown if the Frame could not be created
	 */
	public abstract Pose touchup() throws RoboticsException;

	/**
	 * Creates a new temporary {@link Frame} that stores the position of the given
	 * motion center Frame relative to the {@link RobotArm}'s base Frame. The
	 * created Frame is augmented by {@link TeachingInfo}, storing the current Robot
	 * pose.
	 *
	 * @param motionCenter the motion center to teach
	 * @return the taught frame
	 * @throws RoboticsException thrown if the Frame could not be created
	 */
	public abstract Pose touchup(Pose motionCenter) throws RoboticsException;
}
