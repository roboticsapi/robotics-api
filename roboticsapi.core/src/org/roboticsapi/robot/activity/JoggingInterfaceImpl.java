/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.activity;

import org.roboticsapi.cartesianmotion.action.CartesianJogging;
import org.roboticsapi.cartesianmotion.activity.CartesianJoggingActivity;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.multijoint.activity.JointJoggingInterfaceImpl;
import org.roboticsapi.robot.RobotArm;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Point;

public class JoggingInterfaceImpl extends JointJoggingInterfaceImpl implements JoggingInterface {

	public JoggingInterfaceImpl(RobotArm device) {
		super(device);
	}

	@Override
	public CartesianJoggingActivity cartesianJogging(Frame joggedFrame, Frame referenceFrame, Point pivotPoint,
			Orientation orientation, DeviceParameters... parameters) throws RoboticsException {
		return new CartesianJoggingActivityImpl((RobotArm) getDevice(),
				new CartesianJogging(getDevice().getDriver().getRuntime(), joggedFrame, referenceFrame, pivotPoint,
						orientation),
				getDefaultParameters().withParameters(parameters));
	}

	@Override
	public CartesianJoggingActivity cartesianJogging(DeviceParameters... parameters) throws RoboticsException {
		RobotArm device = (RobotArm) getDevice();
		return cartesianJogging(device.getFlange(), getDevice().getBase(), device.getFlange().getPoint(),
				device.getBase().getOrientation(), parameters);
	}

}
