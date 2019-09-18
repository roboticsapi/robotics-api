/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.activity;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.activity.PlannedActivity;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.World;
import org.roboticsapi.framework.cartesianmotion.activity.LinearMotionInterface;
import org.roboticsapi.framework.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.framework.cartesianmotion.parameter.CartesianBlendingParameter;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;

public class LinearMotionInterfaceImpl extends ActuatorInterfaceImpl implements LinearMotionInterface {
	private CartesianMotionDevice robot;

	public LinearMotionInterfaceImpl(ActuatorDriver driver) {
		super(driver);
		this.robot = (CartesianMotionDevice) driver.getDevice();
		try {

			CartesianBlendingParameter cbp = getDevice().getDefaultParameters().get(CartesianBlendingParameter.class);
			if (cbp != null) {
				addDefaultParameters(new CartesianBlendingParameter(1d));
			}
		} catch (InvalidParametersException e) {
			RAPILogger.getLogger(this).warning("Could not set CartesianBlendingParameter(1) as default parameter");
		}
	}

	@Override
	public Pose getDefaultMotionCenter() {
		return robot.getDefaultMotionCenter();
	}

	@Override
	public PlannedActivity lin(Pose to, DeviceParameters... parameters) throws RoboticsException {
		return lin(to, 1, parameters);
	}

	@Override
	public PlannedActivity lin(Pose to, double speedFactor, DeviceParameters... parameters) throws RoboticsException {

		if (speedFactor < 0 || speedFactor > 1) {
			throw new IllegalArgumentException("Only values between 0 and 1 allowed for speedFactor");
		}
		DeviceParameterBag param = getDefaultParameters().withParameters(parameters);
		final MotionCenterParameter motionCenter = param.get(MotionCenterParameter.class);

		if (motionCenter == null) {
			throw new RoboticsException("No motion center parameters given");
		}

		if (motionCenter.getMotionCenter().getReference().getRelationsTo(to.getReference(),
				World.getCommandedTopology().withoutDynamic()) != null) {
			throw new RoboticsException("Found static Transformation between motion target and motion center");
		}

		return new LinearMotionActivityImpl(robot.getName() + ".lin(" + to + ")", getDriver(), to, speedFactor,
				motionCenter, param);
	}

}
