/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.activity;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.PlannedActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.framework.multijoint.MultiJointDevice;
import org.roboticsapi.framework.multijoint.activity.JointPtpInterface;

public class JointPtpInterfaceWithCartesian extends JointMotionWithCartesianMetadata<JointPtpInterface>
		implements JointPtpInterface {

	public JointPtpInterfaceWithCartesian(JointPtpInterface instance, MultiJointDevice device) {
		super(instance);
	}

	@Override
	public PlannedActivity ptp(double[] to, double speedFactor, DeviceParameters... parameters)
			throws RoboticsException {
		PlannedActivity ret = instance.ptp(to, speedFactor, parameters);
		addProviders(ret);
		return ret;
	}

	@Override
	public PlannedActivity ptp(double[] to, DeviceParameters... parameters) throws RoboticsException {
		PlannedActivity ret = instance.ptp(to, parameters);
		addProviders(ret);
		return ret;
	}

	@Override
	public PlannedActivity ptpHome(DeviceParameters... parameters) throws RoboticsException {
		PlannedActivity ret = instance.ptpHome();
		addProviders(ret);
		return ret;
	}

	@Override
	public MultiJointDevice getDevice() {
		return (MultiJointDevice) super.getDevice();
	}

}
