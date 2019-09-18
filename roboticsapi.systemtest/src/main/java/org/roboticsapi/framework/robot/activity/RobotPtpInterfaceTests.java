/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.activity;

import org.junit.Assume;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.framework.multijoint.JointProvider;
import org.roboticsapi.framework.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.framework.robot.RobotArm;
import org.roboticsapi.systemtest.DeviceInterfaceTest;
import org.roboticsapi.systemtest.DeviceInterfaceTestMethod;
import org.roboticsapi.systemtest.RasAlGhul;

public class RobotPtpInterfaceTests implements DeviceInterfaceTest<RobotPtpInterface> {

	@Override
	public Class<RobotPtpInterface> getDeviceInterfaceType() {
		return RobotPtpInterface.class;
	}

	@DeviceInterfaceTestMethod
	public void testPtpJointLimitsMeasured(RobotPtpInterface iface)
			throws TransformationException, CommunicationException, RoboticsException {
		testPtpJointLimits(iface, device -> device.getMeasuredJointAngles());
	}

	@DeviceInterfaceTestMethod
	public void testPtpJointLimitsCommanded(RobotPtpInterface iface)
			throws TransformationException, CommunicationException, RoboticsException {
		testPtpJointLimits(iface, device -> device.getJointAngles());
	}

	public void testPtpJointLimits(RobotPtpInterface iface, JointProvider jp)
			throws TransformationException, CommunicationException, RoboticsException {
		drivePtpHome(iface);

		RobotArm arm = (RobotArm) iface.getDevice();
		double[] posErrors = iface.getDefaultParameters().get(JointDeviceParameters.class).getAllowedPositionErrors();
		double[][] limits = getJointLimits(iface);
		iface.ptp(arm.getBase().asPose().plus(arm.getForwardKinematics(limits[0])), limits[0]).execute();
		RasAlGhul.assertArrayEquals(limits[0], jp.readJointValues(arm), posErrors);
		iface.ptp(arm.getBase().asPose().plus(arm.getForwardKinematics(limits[1])), limits[1]).execute();
		RasAlGhul.assertArrayEquals(limits[1], jp.readJointValues(arm), posErrors);
	}

	public double[][] getJointLimits(RobotPtpInterface ptpi) {
		JointDeviceParameters parameters = ptpi.getDefaultParameters().get(JointDeviceParameters.class);
		return new double[][] { parameters.getMaximumJointPositions(), parameters.getMaximumJointPositions() };
	}

	@DeviceInterfaceTestMethod
	public void testPtpHomeCommanded(RobotPtpInterface iface) throws RoboticsException {
		drivePtpHome(iface);
		RobotArm device = (RobotArm) iface.getDevice();
		RasAlGhul.assertArrayEquals(device.getHomePosition(), device.getJointAngles(),
				iface.getDefaultParameters().get(JointDeviceParameters.class).getAllowedPositionErrors());
	}

	@DeviceInterfaceTestMethod
	public void testPtpHomeMeasured(RobotPtpInterface iface) throws RoboticsException {
		drivePtpHome(iface);
		RobotArm device = (RobotArm) iface.getDevice();
		RasAlGhul.assertArrayEquals(device.getHomePosition(), device.getMeasuredJointAngles(),
				iface.getDefaultParameters().get(JointDeviceParameters.class).getAllowedPositionErrors());
	}

	private void drivePtpHome(RobotPtpInterface iface) {
		RobotArm device = (RobotArm) iface.getDevice();
		double[] homePosition = device.getHomePosition();
		Transformation forwardKinematics;
		try {
			forwardKinematics = device.getForwardKinematics(homePosition);
			Pose pose;
			pose = device.getBase().asPose().plus(forwardKinematics);
			iface.ptp(pose).execute();
		} catch (RoboticsException e) {
			Assume.assumeTrue("Could not drive to home via PTP motion", false);
		}
	}

}
