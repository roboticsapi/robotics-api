/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.composed.ParallelActivity;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.activity.runtime.FromCommandActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.framework.multijoint.Joint;
import org.roboticsapi.framework.multijoint.JointDriver;
import org.roboticsapi.framework.multijoint.action.JointReset;
import org.roboticsapi.framework.multijoint.activity.SimulatedJointMotionInterface;
import org.roboticsapi.framework.multijoint.runtime.rpi.MultiJointDeviceGenericDriver;

public class SimulatedJointMotionInterfaceImpl extends ActuatorInterfaceImpl implements SimulatedJointMotionInterface {

	private final MultiJointDeviceGenericDriver<?> driver;

	public SimulatedJointMotionInterfaceImpl(MultiJointDeviceGenericDriver<?> driver) {
		super(driver);
		this.driver = driver;
	}

	@Override
	public final Activity resetJoints(final double[] newPositions, DeviceParameters... parameters)
			throws RoboticsException {
		Activity ret = null;
		for (int i = 0; i < newPositions.length; i++) {
			Activity newActivity = createResetJointActivity(i, newPositions[i], parameters);
			if (ret == null) {
				ret = newActivity;
			} else {
				ret = new ParallelActivity(ret, newActivity);
//				ret = new StrictlySequentialActivity(ret, newActivity); // FIXME: sequential
			}
		}
		return ret;
	}

	private final Activity createResetJointActivity(final int jointIndex, final double newPosition,
			DeviceParameters... parameters) throws RoboticsException {
		JointDriver jointDriver = driver.getJointDriver(jointIndex);
		Joint joint = jointDriver.getDevice();

		return new FromCommandActivity(
				() -> jointDriver.getRuntime().createRuntimeCommand(jointDriver, new JointReset(newPosition)), joint);
	}

}
