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
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianSplineActivity;
import org.roboticsapi.framework.cartesianmotion.activity.SplineActivity;
import org.roboticsapi.framework.cartesianmotion.activity.SplineInterface;
import org.roboticsapi.framework.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.framework.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;

public class SplineInterfaceImpl extends ActuatorInterfaceImpl implements SplineInterface {
	private final CartesianMotionDevice robot;

	public SplineInterfaceImpl(ActuatorDriver driver) {
		super(driver);
		this.robot = (CartesianMotionDevice) driver.getDevice();
	}

	@Override
	public Pose getDefaultMotionCenter() {
		return robot.getDefaultMotionCenter();
	}

	@Override
	public final CartesianSplineActivity spline(Pose splinePoint, Pose... furtherSplinePoints)
			throws RoboticsException {
		return spline(1.0, new DeviceParameters[0], splinePoint, furtherSplinePoints);
	}

	@Override
	public final CartesianSplineActivity spline(double speedFactor, Pose splinePoint, Pose... furtherSplinePoints)
			throws RoboticsException {
		return spline(speedFactor, new DeviceParameters[0], splinePoint, furtherSplinePoints);
	}

	@Override
	public CartesianSplineActivity spline(double speedFactor, DeviceParameters[] parameters, final Pose splinePoint,
			final Pose... furtherSplinePoints) throws RoboticsException {

		DeviceParameterBag param = getDefaultParameters().withParameters(parameters);
		final MotionCenterParameter motionCenter = param.get(MotionCenterParameter.class);

		if (motionCenter == null) {
			throw new RoboticsException("No motion center parameters given");
		}

		CartesianParameters cp = param.get(CartesianParameters.class);
		if (cp == null) {
			throw new RoboticsException("No Cartesian paramters given");
		}

		try {
			if (motionCenter.getMotionCenter().getReference().getTransformationTo(splinePoint.getReference(),
					false) != null) {
				throw new RoboticsException("Found static Transformation between motion target and motion center");
			}
		} catch (TransformationException e) {
			// that's okay
			// TODO: Maybe switch back to returning null instead of exception
			// throwing
		}

		for (Pose f : furtherSplinePoints) {
			try {
				if (motionCenter.getMotionCenter().getReference().getTransformationTo(f.getReference(),
						false) != null) {
					throw new RoboticsException("Found static Transformation between motion target and motion center");
				}
			} catch (TransformationException e) {
				// that's okay
				// TODO: Maybe switch back to returning null instead of
				// exception throwing
			}
		}

		return new SplineActivity(robot.getName() + ".spline()", speedFactor, furtherSplinePoints, getDriver(),
				splinePoint, motionCenter, param);
	}

}
