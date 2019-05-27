/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.activity;

import java.util.List;

import org.roboticsapi.activity.ActivityProperty;
import org.roboticsapi.cartesianmotion.activity.FrameGoalProperty;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.multijoint.activity.SimulatedJointMotionInterfaceImpl;
import org.roboticsapi.robot.RobotArm;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;

public class SimulatedRobotArmMotionInterfaceImpl<T extends RobotArm> extends SimulatedJointMotionInterfaceImpl<T>
		implements SimulatedRobotArmMotionInterface {

	public SimulatedRobotArmMotionInterfaceImpl(T robotArm) {
		super(robotArm);
	}

	@Override
	protected List<ActivityProperty> createProperties(double[] newPositions) throws RoboticsException {
		List<ActivityProperty> list = super.createProperties(newPositions);
		Transformation transformation = getDevice().getForwardKinematics(newPositions);
		Frame goal = getDevice().getBase().plus(transformation);
		list.add(new FrameGoalProperty(goal, getDevice().getFlange()));
		return list;
	}

}
