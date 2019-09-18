/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.activity;

import java.util.Arrays;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.PlannedActivity;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.framework.multijoint.MultiJointDevice;
import org.roboticsapi.framework.multijoint.activity.JointPtpInterface;
import org.roboticsapi.framework.multijoint.runtime.rpi.MultiJointDeviceGenericDriver;

public class JointPtpInterfaceImpl extends ActuatorInterfaceImpl implements JointPtpInterface {
	private final MultiJointDevice robot;

	public JointPtpInterfaceImpl(MultiJointDeviceGenericDriver<?> driver) {
		super(driver);
		this.robot = driver.getDevice();
	}

	@Override
	public PlannedActivity ptp(final double[] to, DeviceParameters... parameters) throws RoboticsException {
		return ptp(to, 1.0, parameters);
	}

	@Override
	public PlannedActivity ptp(final double[] to, double speedFactor, DeviceParameters... parameters)
			throws RoboticsException {
		AbstractPtpActivityImpl activityImpl = new AbstractPtpActivityImpl(
				robot.getName() + ".ptp(" + Arrays.toString(to) + ")", getDriver(), speedFactor,
				getDefaultParameters().withParameters(parameters)) {
			@Override
			protected double[] getGoal(double[] start) {
				return to;
			}
		};

		return activityImpl;
	}

	@Override
	public final PlannedActivity ptpHome(DeviceParameters... parameters) throws RoboticsException {
		return ptp(robot.getHomePosition(), parameters);
	}

}
