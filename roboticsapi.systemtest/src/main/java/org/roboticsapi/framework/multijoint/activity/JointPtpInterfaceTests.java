/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.activity;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.framework.multijoint.JointProvider;
import org.roboticsapi.framework.multijoint.MultiJointDevice;
import org.roboticsapi.framework.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.systemtest.DeviceInterfaceTest;
import org.roboticsapi.systemtest.DeviceInterfaceTestMethod;
import org.roboticsapi.systemtest.RasAlGhul;

public class JointPtpInterfaceTests implements DeviceInterfaceTest<JointPtpInterface> {

	private static double allowedJointDelta = 0.0001;

	@Override
	public Class<JointPtpInterface> getDeviceInterfaceType() {
		return JointPtpInterface.class;
	}

	@DeviceInterfaceTestMethod
	public void testJointPtpFromInitToHomeMeasured(JointPtpInterface jointPtpInterface) throws RoboticsException {
		testJointPtpFromInitToHome(jointPtpInterface, device -> device.getMeasuredJointAngles(), true);
	}

	@DeviceInterfaceTestMethod
	public void testJointPtpFromInitToHomeCommanded(JointPtpInterface jointPtpInterface) throws RoboticsException {
		testJointPtpFromInitToHome(jointPtpInterface, device -> device.getJointAngles(), false);
	}

	private static void testJointPtpFromInitToHome(JointPtpInterface jointPtpInterface, JointProvider jp,
			boolean allowPositionError) throws RoboticsException {
		MultiJointDevice device = (MultiJointDevice) jointPtpInterface.getDevice();
		double[] allowedPositionErrors = jointPtpInterface.getDefaultParameters().get(JointDeviceParameters.class)
				.getAllowedPositionErrors();
		jointPtpInterface.ptpHome().execute();
		checkJointValues(jp.readJointValues(device), device.getHomePosition(), allowedPositionErrors,
				allowPositionError);
	}

	@DeviceInterfaceTestMethod
	public void testJointPtpFromHomeToHomeMeasured(JointPtpInterface jointPtpInterface) throws RoboticsException {
		testJointPtpFromHomeToHome(jointPtpInterface, device -> device.getMeasuredJointAngles(), true);
	}

	@DeviceInterfaceTestMethod
	public void testJointPtpFromHomeToHomeCommanded(JointPtpInterface jointPtpInterface) throws RoboticsException {
		testJointPtpFromHomeToHome(jointPtpInterface, device -> device.getJointAngles(), false);
	}

	private static void testJointPtpFromHomeToHome(JointPtpInterface jointPtpInterface, JointProvider jp,
			boolean allowPositionError) throws RoboticsException {
		MultiJointDevice device = (MultiJointDevice) jointPtpInterface.getDevice();
		double[] allowedPositionErrors = jointPtpInterface.getDefaultParameters().get(JointDeviceParameters.class)
				.getAllowedPositionErrors();
		assumeJointValues(jp.readJointValues(device), device.getHomePosition(), allowedPositionErrors,
				allowPositionError);

		jointPtpInterface.ptpHome().execute();
		checkJointValues(jp.readJointValues(device), device.getHomePosition(), allowedPositionErrors,
				allowPositionError);
	}

	@DeviceInterfaceTestMethod
	public void testJointPtpFromHomeToAnywhereMeasured(JointPtpInterface jointPtpInterface) throws RoboticsException {
		testJointPtpFromHomeToAnywhere(jointPtpInterface, device -> device.getMeasuredJointAngles(), true);
	}

	@DeviceInterfaceTestMethod
	public void testJointPtpFromHomeToAnywhereCommanded(JointPtpInterface jointPtpInterface) throws RoboticsException {
		testJointPtpFromHomeToAnywhere(jointPtpInterface, device -> device.getJointAngles(), false);
	}

	private static void testJointPtpFromHomeToAnywhere(JointPtpInterface jointPtpInterface, JointProvider jp,
			boolean allowPositionError) throws RoboticsException {
		MultiJointDevice device = (MultiJointDevice) jointPtpInterface.getDevice();
		JointDeviceParameters jointDeviceParameters = jointPtpInterface.getDefaultParameters()
				.get(JointDeviceParameters.class);
		double[] allowedPositionErrors = jointDeviceParameters.getAllowedPositionErrors();
		assumeJointValues(jp.readJointValues(device), device.getHomePosition(), allowedPositionErrors,
				allowPositionError);

		double[] destination = new double[device.getJointCount()];
		for (int i = 0; i < destination.length; i++) {
			double range = jointDeviceParameters.getMaximumJointPositions()[i]
					- jointDeviceParameters.getMinimumJointPositions()[i];
			destination[i] = (Math.random() * range) + jointDeviceParameters.getMinimumJointPositions()[i];
		}
		jointPtpInterface.ptp(destination).execute();
		checkJointValues(jp.readJointValues(device), destination, allowedPositionErrors, allowPositionError);
	}

	@DeviceInterfaceTestMethod
	public void testJointPtpFromHomeToJointLimitsMeasured(JointPtpInterface jointPtpInterface)
			throws RoboticsException {
		testJointPtpFromHomeToJointLimits(jointPtpInterface, device -> device.getMeasuredJointAngles(), true);
	}

	@DeviceInterfaceTestMethod
	public void testJointPtpFromHomeToJointLimitsCommanded(JointPtpInterface jointPtpInterface)
			throws RoboticsException {
		testJointPtpFromHomeToJointLimits(jointPtpInterface, device -> device.getJointAngles(), false);
	}

	private static void testJointPtpFromHomeToJointLimits(JointPtpInterface jointPtpInterface, JointProvider jp,
			boolean allowPositionError) throws RoboticsException {
		MultiJointDevice device = (MultiJointDevice) jointPtpInterface.getDevice();
		double[] allowedPositionErrors = jointPtpInterface.getDefaultParameters().get(JointDeviceParameters.class)
				.getAllowedPositionErrors();
		assumeJointValues(jp.readJointValues(device), device.getHomePosition(), allowedPositionErrors,
				allowPositionError);

		JointDeviceParameters jointDeviceParameters = jointPtpInterface.getDefaultParameters()
				.get(JointDeviceParameters.class);
		double[] lowerLimitDestination = jointDeviceParameters.getMinimumJointPositions();
		jointPtpInterface.ptp(lowerLimitDestination).execute();
		checkJointValues(jp.readJointValues(device), lowerLimitDestination, allowedPositionErrors, allowPositionError);

		double[] upperLimitDestination = jointDeviceParameters.getMaximumJointPositions();
		jointPtpInterface.ptp(upperLimitDestination).execute();
		checkJointValues(jp.readJointValues(device), upperLimitDestination, allowedPositionErrors, allowPositionError);
	}

	private static void assumeJointValues(double[] actualJointValues, double[] expectedJointValues,
			double[] allowedDeltas, boolean allowPositionError) {
		if (allowPositionError) {
			RasAlGhul.assumeArrayEquals(actualJointValues, expectedJointValues, allowedDeltas);
		} else {
			RasAlGhul.assumeArrayEquals(actualJointValues, expectedJointValues, allowedJointDelta);
		}
	}

	private static void checkJointValues(double[] actualJointValues, double[] expectedJointValues,
			double[] allowedDeltas, boolean allowPositionError) {
		if (allowPositionError) {
			RasAlGhul.assertArrayEquals(actualJointValues, expectedJointValues, allowedDeltas);
		} else {
			RasAlGhul.assertArrayEquals(actualJointValues, expectedJointValues, allowedJointDelta);
		}
	}

}
