/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.activity;

import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.cartesianmotion.activity.FollowCartesianGoalActivityImpl;
import org.roboticsapi.cartesianmotion.activity.FollowCartesianGoalFromJavaActivity;
import org.roboticsapi.cartesianmotion.activity.FollowCartesianGoalFromJavaActivityImpl;
import org.roboticsapi.cartesianmotion.activity.MoveToCartesianGoalActivityImpl;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.multijoint.activity.JointGoalMotionInterfaceImpl;
import org.roboticsapi.robot.RobotArm;
import org.roboticsapi.world.Frame;

public class GoalMotionInterfaceImpl extends JointGoalMotionInterfaceImpl implements GoalMotionInterface {

	public GoalMotionInterfaceImpl(RobotArm device) {
		super(device);
	}

	@Override
	public FollowCartesianGoalFromJavaActivity followCartesianGoalFromJava(Frame reference,
			DeviceParameters... parameters) throws RoboticsException {
		return new FollowCartesianGoalFromJavaActivityImpl((RobotArm) getDevice(), reference,
				getDefaultParameters().withParameters(parameters));
	}

	@Override
	public RtActivity followCartesianGoal(Frame goal, DeviceParameters... parameters) throws RoboticsException {
		return new FollowCartesianGoalActivityImpl((RobotArm) getDevice(), goal,
				getDefaultParameters().withParameters(parameters));
	}

	@Override
	public RtActivity moveToCartesianGoal(Frame goal, DeviceParameters... parameters) throws RoboticsException {
		return new MoveToCartesianGoalActivityImpl((RobotArm) getDevice(), goal,
				getDefaultParameters().withParameters(parameters));
	}

}
