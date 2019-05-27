/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.ActuatorInterfaceImpl;
import org.roboticsapi.activity.PlannedRtActivity;
import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.cartesianmotion.parameter.CartesianBlendingParameter;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.TransformationException;

public class LinearMotionInterfaceImpl<A extends CartesianMotionDevice> extends ActuatorInterfaceImpl<A>
		implements LinearMotionInterface {

	public LinearMotionInterfaceImpl(A device) {
		super(device);

		try {
			addDefaultParameters(new CartesianBlendingParameter(1d));
		} catch (InvalidParametersException e) {
			RAPILogger.getLogger().log(RAPILogger.WARNINGLEVEL,
					"Could not set CartesianBlendingParameter(1) as default parameter");
		}
	}

	@Override
	public Frame getDefaultMotionCenter() {
		return getDevice().getDefaultMotionCenter();
	}

	@Override
	public PlannedRtActivity lin(Frame to, DeviceParameters... parameters) throws RoboticsException {
		return lin(to, 1, parameters);
	}

	@Override
	public PlannedRtActivity lin(Frame to, double speedFactor, DeviceParameters... parameters)
			throws RoboticsException {

		if (speedFactor < 0 || speedFactor > 1) {
			throw new IllegalArgumentException("Only values between 0 and 1 allowed for speedFactor");
		}
		A robot = getDevice();
		DeviceParameterBag param = getDefaultParameters().withParameters(parameters);
		final MotionCenterParameter motionCenter = param.get(MotionCenterParameter.class);

		if (motionCenter == null) {
			throw new RoboticsException("No motion center parameters given");
		}

		try {
			if (motionCenter.getMotionCenter().getTransformationTo(to, false) != null) {
				throw new RoboticsException("Found static Transformation between motion target and motion center");
			}
		} catch (TransformationException e) {
			// that's okay
			// TODO: Maybe switch back to returning null instead of exception
			// throwing
		}

		return new LinearMotionActivityImpl(robot.getName() + ".lin(" + to.getName() + ")", robot, to, speedFactor,
				motionCenter, param);
	}

}
