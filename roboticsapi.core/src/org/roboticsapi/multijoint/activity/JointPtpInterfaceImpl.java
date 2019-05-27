/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import java.util.Arrays;

import org.roboticsapi.activity.ActuatorInterfaceImpl;
import org.roboticsapi.activity.PlannedRtActivity;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.multijoint.MultiJointDevice;

public class JointPtpInterfaceImpl<T extends MultiJointDevice> extends ActuatorInterfaceImpl<T>
		implements JointPtpInterface {

	public JointPtpInterfaceImpl(T multiJointDevice) {
		super(multiJointDevice);
	}

	@Override
	public PlannedRtActivity ptp(final double[] to, DeviceParameters... parameters) throws RoboticsException {
		final MultiJointDevice robot = getDevice();

		JointPtpActivityImpl activityImpl = new JointPtpActivityImpl(
				robot.getName() + ".ptp(" + Arrays.toString(to) + ")", robot,
				getDefaultParameters().withParameters(parameters)) {

			@Override
			protected double[] getTarget(double[] from) {
				return to;
			}
		};

		return activityImpl;
	}

	@Override
	public final PlannedRtActivity ptpHome(DeviceParameters... parameters) throws RoboticsException {
		return ptp(getDevice().getHomePosition(), parameters);
	}

}
