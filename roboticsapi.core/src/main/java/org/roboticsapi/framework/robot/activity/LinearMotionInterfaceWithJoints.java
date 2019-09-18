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
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.framework.cartesianmotion.activity.LinearMotionInterface;
import org.roboticsapi.framework.cartesianmotion.device.CartesianMotionDevice;

public class LinearMotionInterfaceWithJoints extends CartesianMotionWithJointMetadata<LinearMotionInterface>
		implements LinearMotionInterface {

	public LinearMotionInterfaceWithJoints(LinearMotionInterface instance, CartesianMotionDevice device) {
		super(instance);
	}

	@Override
	public Pose getDefaultMotionCenter() throws RoboticsException {
		return instance.getDefaultMotionCenter();
	}

	@Override
	public PlannedActivity lin(Pose to, DeviceParameters... parameters) throws RoboticsException {
		PlannedActivity ret = instance.lin(to, parameters);
		addProviders(ret);
		return ret;
	}

	@Override
	public PlannedActivity lin(Pose to, double speedFactor, DeviceParameters... parameters) throws RoboticsException {
		PlannedActivity ret = instance.lin(to, speedFactor, parameters);
		addProviders(ret);
		return ret;
	}

}
