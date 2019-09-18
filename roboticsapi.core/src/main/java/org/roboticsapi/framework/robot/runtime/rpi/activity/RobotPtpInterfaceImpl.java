/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.activity;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.PlannedActivity;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.framework.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.framework.multijoint.runtime.rpi.activity.AbstractPtpActivityImpl;
import org.roboticsapi.framework.robot.RobotArm;
import org.roboticsapi.framework.robot.activity.JointMotionWithCartesianMetadata;
import org.roboticsapi.framework.robot.activity.RobotPtpInterface;
import org.roboticsapi.framework.robot.runtime.rpi.driver.RobotArmGenericDriver;

public class RobotPtpInterfaceImpl extends ActuatorInterfaceImpl implements RobotPtpInterface {

	private final RobotArm robot;

	public RobotPtpInterfaceImpl(RobotArmGenericDriver<?> driver) {
		super(driver);
		this.robot = driver.getDevice();
	}

	private void addPropertyProviders(PlannedActivity activity) {
		JointMotionWithCartesianMetadata.addProviders(activity);
	}

	@Override
	public PlannedActivity ptp(final Pose to, final DeviceParameters... parameters) throws RoboticsException {
		return ptp(to, 1.0, parameters);
	}

	@Override
	public PlannedActivity ptp(Pose to, double speedFactor, DeviceParameters... parameters) throws RoboticsException {
		DeviceParameterBag finalParams = getDefaultParameters().withParameters(parameters);
		PlannedActivity result = new AbstractPtpActivityImpl(getDriver(), speedFactor, finalParams) {
			@Override
			protected double[] getGoal(double[] start) throws RoboticsException {
				double[] goal = robot.getInverseKinematics(to, start, finalParams.getArray());
				goal = fixGoal(goal, finalParams.get(JointDeviceParameters.class));
				return goal;
			}
		};
		addPropertyProviders(result);
		return result;
	}

	@Override
	public PlannedActivity ptp(final Pose to, final double[] hintJoints, final DeviceParameters... parameters)
			throws RoboticsException {
		return ptp(to, hintJoints, 1.0, parameters);
	}

	@Override
	public PlannedActivity ptp(Pose to, double[] hintJoints, double speedFactor, DeviceParameters... parameters)
			throws RoboticsException {
		DeviceParameterBag finalarams = getDefaultParameters().withParameters(parameters);
		PlannedActivity result = new AbstractPtpActivityImpl(getDriver(), speedFactor, finalarams) {
			@Override
			protected double[] getGoal(double[] start) throws RoboticsException {
				double[] goal = robot.getInverseKinematics(to, hintJoints, finalarams.getArray());
				goal = fixGoal(goal, finalarams.get(JointDeviceParameters.class));
				return goal;
			}
		};
		addPropertyProviders(result);
		return result;
	}

	/**
	 * Hacky fix: If the goal is outside the joint limits but using the joint limits
	 * for the corresponding axes instead yields a position error < 1e-3 m / rad,
	 * use the joint limits instead
	 *
	 * @param goal            joint position to correct
	 * @param jointParameters joint limits to check against
	 * @return joint position that uses joint limits instead of invalid values if
	 *         the cartesian error is sufficiently small
	 * @throws RoboticsException if the computation (forward kinematics) fails
	 */
	protected double[] fixGoal(double[] goal, JointDeviceParameters jointParameters) throws RoboticsException {
		double[] min = jointParameters.getMinimumJointPositions();
		double[] max = jointParameters.getMaximumJointPositions();
		double[] ret = new double[goal.length];
		boolean changed = false;
		for (int i = 0; i < goal.length; i++) {
			if (goal[i] < min[i]) {
				ret[i] = min[i];
				changed = true;
			} else if (goal[i] > max[i]) {
				ret[i] = max[i];
				changed = true;
			} else {
				ret[i] = goal[i];
			}
		}
		if (changed) {
			Transformation retPos = robot.getForwardKinematics(ret);
			Transformation goalPos = robot.getForwardKinematics(goal);
			Transformation diff = retPos.invert().multiply(goalPos);
			if (diff.getTranslation().getLength() > 1e-3 || diff.getRotation().getAngle() > 1e-3) {
				return goal;
			}
		}
		return ret;
	}

}
