/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import org.roboticsapi.activity.ActuatorInterfaceImpl;
import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.MultiJointDevice;

public class JointGoalMotionInterfaceImpl extends ActuatorInterfaceImpl<MultiJointDevice>
		implements JointGoalMotionInterface {

	public JointGoalMotionInterfaceImpl(MultiJointDevice device) {
		super(device);
	}

	@Override
	public FollowJointGoalFromJavaActivity followJointGoalFromJava(DeviceParameters... parameters)
			throws RoboticsException {
		return new FollowJointGoalFromJavaActivityImpl(getDevice(), getDefaultParameters().withParameters(parameters));
	}

	@Override
	public RtActivity followJointGoal(DoubleSensor[] sensors, DeviceParameters... parameters) throws RoboticsException {
		return new FollowJointGoalActivityImpl(getDevice(), sensors, getDefaultParameters().withParameters(parameters));
	}

	@Override
	public RtActivity followJointVelocity(DoubleSensor[] sensors, DeviceParameters... parameters)
			throws RoboticsException {
		return new FollowJointVelocityActivityImpl(getDevice(), sensors,
				getDefaultParameters().withParameters(parameters));
	}

	@Override
	public RtActivity followJointVelocity(DoubleSensor[] sensors, double[] limits, DeviceParameters... parameters)
			throws RoboticsException {
		return new FollowJointVelocityActivityImpl(getDevice(), sensors, limits,
				getDefaultParameters().withParameters(parameters));
	}

}
