/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.activity;

import org.roboticsapi.activity.PlannedRtActivity;
import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.multijoint.activity.JointPtpInterface;
import org.roboticsapi.robot.RobotArm;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.TeachingInfo;

/**
 * The PtpInterface provides different variants of Point-To-Point (PTP) motions
 * in form of Activities.
 */
public interface PtpInterface extends JointPtpInterface {

	Frame getDefaultMotionCenter();

	/**
	 * Creates an {@link RtActivity} for a point-to-point motion from the current
	 * cartesian position to a new Cartesian position specified by the given
	 * {@link Frame}.
	 * 
	 * @param to         the Cartesian target position
	 * @param hintJoints a 'hint' to the preferred joint configuration to take at
	 *                   the target position; if multiple joint configurations
	 *                   exist, the one with the smallest distance to these hint
	 *                   joints will be chosen
	 * @param parameters optional DeviceParameters
	 * @return point-to-point motion {@link RtActivity}
	 * @throws RoboticsException if the requested point-to-point motion is not valid
	 */
	public abstract PlannedRtActivity ptp(final Frame to, final double[] hintJoints,
			final DeviceParameters... parameters) throws RoboticsException;

	/**
	 * Creates an {@link RtActivity} for a point-to-point motion from the current
	 * cartesian position to a new Cartesian position specified by the given
	 * {@link Frame}.
	 * 
	 * @param to         the Cartesian target position
	 * @param parameters optional DeviceParameters
	 * @return point-to-point motion {@link RtActivity}
	 * @throws RoboticsException if the requested point-to-point motion is not valid
	 */
	public abstract PlannedRtActivity ptp(final Frame to, final DeviceParameters... parameters)
			throws RoboticsException;

	/**
	 * Gets hint joint information (if any) stored in the given {@link Frame} for
	 * this LinInterface's {@link Actuator}.
	 * 
	 * @param f          the Frame to extract hint joints from
	 * @param parameters {@link DeviceParameters} to consider when looking for hint
	 *                   joints (e.g. {@link MotionCenterParameter})
	 * @return the hint joints stored in the given Frame
	 */
	public abstract double[] getHintJointsFromFrame(Frame f, DeviceParameters... parameters);

	/**
	 * Creates a new temporary {@link Frame} that stores the position of the
	 * {@link RobotArm}'s motion center relative to its base Frame. The created
	 * Frame is augmented by {@link TeachingInfo}, storing the current Robot pose.
	 * 
	 * @return the taught Frame
	 * @throws RoboticsException thrown if the Frame could not be created
	 */
	public abstract Frame touchup() throws RoboticsException;

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
	public abstract Frame touchup(Frame motionCenter) throws RoboticsException;
}