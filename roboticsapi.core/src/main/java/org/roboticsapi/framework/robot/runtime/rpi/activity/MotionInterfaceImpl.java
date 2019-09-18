/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.PlannedActivity;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianSplineActivity;
import org.roboticsapi.framework.cartesianmotion.activity.LinearMotionInterface;
import org.roboticsapi.framework.cartesianmotion.activity.SplineInterface;
import org.roboticsapi.framework.multijoint.activity.JointPtpInterface;
import org.roboticsapi.framework.robot.activity.MotionInterface;
import org.roboticsapi.framework.robot.activity.RobotLinearMotionInterface;
import org.roboticsapi.framework.robot.activity.RobotPtpInterface;
import org.roboticsapi.framework.robot.activity.TeachingInterface;
import org.roboticsapi.framework.robot.runtime.rpi.driver.RobotArmGenericDriver;

public class MotionInterfaceImpl extends ActuatorInterfaceImpl implements MotionInterface {

	public MotionInterfaceImpl(RobotArmGenericDriver<?> driver) {
		super(driver);
	}

	@Override
	public Pose getDefaultMotionCenter() throws RoboticsException {
		return use(LinearMotionInterface.class).getDefaultMotionCenter();
	}

	@Override
	public PlannedActivity lin(Pose to, DeviceParameters... parameters) throws RoboticsException {
		return use(LinearMotionInterface.class).lin(to, parameters);
	}

	@Override
	public PlannedActivity lin(Pose to, double speedFactor, DeviceParameters... parameters) throws RoboticsException {
		return use(LinearMotionInterface.class).lin(to, speedFactor, parameters);
	}

	@Override
	public CartesianSplineActivity spline(double speedFactor, DeviceParameters[] parameters, Pose splinePoint,
			Pose... furtherSplinePoints) throws RoboticsException {
		return use(SplineInterface.class).spline(speedFactor, parameters, splinePoint, furtherSplinePoints);
	}

	@Override
	public CartesianSplineActivity spline(Pose splinePoint, Pose... furtherSplinePoints) throws RoboticsException {
		return use(SplineInterface.class).spline(splinePoint, furtherSplinePoints);
	}

	@Override
	public PlannedActivity ptp(Pose to, DeviceParameters... parameters) throws RoboticsException {
		return use(RobotPtpInterface.class).ptp(to, parameters);
	}

	@Override
	public PlannedActivity ptp(Pose to, double speedFactor, DeviceParameters... parameters) throws RoboticsException {
		return use(RobotPtpInterface.class).ptp(to, speedFactor, parameters);
	}

	@Override
	public PlannedActivity ptp(Pose to, double[] hintJoints, DeviceParameters... parameters) throws RoboticsException {
		return use(RobotPtpInterface.class).ptp(to, hintJoints, parameters);
	}

	@Override
	public PlannedActivity ptp(Pose to, double[] hintJoints, double speedFactor, DeviceParameters... parameters)
			throws RoboticsException {
		return use(RobotPtpInterface.class).ptp(to, hintJoints, speedFactor, parameters);
	}

	@Override
	public double[] getHintJointsFromFrame(Pose f, DeviceParameters... parameters) throws RoboticsException {
		return use(TeachingInterface.class).getHintJointsFromFrame(f, parameters);
	}

	@Override
	public Pose touchup() throws RoboticsException {
		return use(TeachingInterface.class).touchup();
	}

	@Override
	public Pose touchup(Pose motionCenter) throws RoboticsException {
		return use(TeachingInterface.class).touchup(motionCenter);
	}

	@Override
	public PlannedActivity ptp(double[] to, double speedFactor, DeviceParameters... parameters)
			throws RoboticsException {
		return use(JointPtpInterface.class).ptp(to, speedFactor, parameters);
	}

	@Override
	public PlannedActivity ptp(double[] to, DeviceParameters... parameters) throws RoboticsException {
		return use(JointPtpInterface.class).ptp(to, parameters);
	}

	@Override
	public PlannedActivity ptpHome(DeviceParameters... parameters) throws RoboticsException {
		return use(JointPtpInterface.class).ptpHome(parameters);
	}

	@Override
	public PlannedActivity lin(Pose to, double[] nullspaceJoints, DeviceParameters... parameters)
			throws RoboticsException {
		return use(RobotLinearMotionInterface.class).lin(to, nullspaceJoints, parameters);
	}

	@Override
	public PlannedActivity lin(Pose to, double[] nullspaceJoints, double speedFactor, DeviceParameters... parameters)
			throws RoboticsException {
		return use(RobotLinearMotionInterface.class).lin(to, nullspaceJoints, speedFactor, parameters);
	}

	@Override
	public CartesianSplineActivity spline(double speedFactor, Pose splinePoint, Pose... furtherSplinePoints)
			throws RoboticsException {
		return use(SplineInterface.class).spline(speedFactor, splinePoint, furtherSplinePoints);
	}

}
