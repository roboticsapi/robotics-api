/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.activity.runtime.FromCommandActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.framework.multijoint.action.FollowJointGoal;
import org.roboticsapi.framework.multijoint.activity.JointGoalMotionInterface;
import org.roboticsapi.framework.multijoint.runtime.rpi.MultiJointDeviceGenericDriver;

public class JointGoalMotionInterfaceImpl extends ActuatorInterfaceImpl implements JointGoalMotionInterface {

	public JointGoalMotionInterfaceImpl(MultiJointDeviceGenericDriver<?> driver) {
		super(driver);
	}

	@Override
	public Activity followJointGoal(RealtimeDouble[] goal, DeviceParameters... parameters) throws RoboticsException {
		// TODO: make better (takeover, properties)
		return new FromCommandActivity(() -> createRuntimeCommand(new FollowJointGoal(goal),
				getDefaultParameters().withParameters(parameters)), getDevice());
	}

	@Override
	public Activity moveToJointGoal(RealtimeDouble[] goal, DeviceParameters... parameters) throws RoboticsException {
		return new MoveToJointGoalActivity(getDriver(), goal, getDefaultParameters().withParameters(parameters));
	}

	@Override
	public Activity moveToJointGoal(double[] goal, DeviceParameters... parameters) throws RoboticsException {
		RealtimeDouble[] rgoal = new RealtimeDouble[goal.length];
		for (int i = 0; i < goal.length; i++) {
			rgoal[i] = RealtimeDouble.createFromConstant(goal[i]);
		}
		return new MoveToJointGoalActivity(getDriver(), rgoal, getDefaultParameters().withParameters(parameters));
	}
}
