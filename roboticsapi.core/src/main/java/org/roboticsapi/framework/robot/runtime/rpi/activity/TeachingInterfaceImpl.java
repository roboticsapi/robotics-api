/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.activity;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.TeachingInfo;
import org.roboticsapi.core.world.World;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.robot.RobotArm;
import org.roboticsapi.framework.robot.activity.TeachingInterface;
import org.roboticsapi.framework.robot.runtime.rpi.driver.RobotArmGenericDriver;

public class TeachingInterfaceImpl extends ActuatorInterfaceImpl implements TeachingInterface {
	private final RobotArm robot;

	public TeachingInterfaceImpl(RobotArmGenericDriver<?> driver) {
		super(driver);
		this.robot = driver.getDevice();
	}

	@Override
	public double[] getHintJointsFromFrame(Pose frame, DeviceParameters... parameters) {
		DeviceParameterBag param = getDefaultParameters().withParameters(parameters);
		final MotionCenterParameter motionCenter = param.get(MotionCenterParameter.class);

		TeachingInfo teachingInfo = frame.getReference().getTeachingInfo(getDevice(), frame,
				motionCenter.getMotionCenter());

		double[] hintJoints = null;
		if (teachingInfo != null) {
			hintJoints = teachingInfo.getHintParameters();
		}

		return hintJoints;
	}

	@Override
	public Pose touchup() throws RoboticsException {
		return touchup(robot.getDefaultMotionCenter());
	}

	@Override
	public Pose touchup(Pose motionCenter) throws RoboticsException {
		Pose goal = motionCenter.asRealtimeValue()
				.convertToRepresentation(robot.getBase().asOrientation(), robot.getBase(), World.getCommandedTopology())
				.getCurrentValue();

		motionCenter.getReference().addTeachingInfo(getDevice(), goal, motionCenter, robot.getJointAngles());

		return goal;
	}

}
