/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.activity;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.PlannedActivity;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianPathMotionInterface;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianSplineActivity;
import org.roboticsapi.framework.cartesianmotion.activity.LinearMotionInterface;
import org.roboticsapi.framework.cartesianmotion.activity.SplineInterface;

public class CartesianPathMotionInterfaceImpl extends ActuatorInterfaceImpl implements CartesianPathMotionInterface {

	public CartesianPathMotionInterfaceImpl(ActuatorDriver driver) {
		super(driver);
	}

	@Override
	public Pose getDefaultMotionCenter() throws RoboticsException {
		return use(LinearMotionInterface.class).getDefaultMotionCenter();
	}

	@Override
	public PlannedActivity lin(Pose to, DeviceParameters... parameters) throws RoboticsException {
		return use(LinearMotionInterface.class).lin(to, parameters);
	}

	@Override
	public PlannedActivity lin(Pose to, double speedFactor, DeviceParameters... parameters) throws RoboticsException {
		return use(LinearMotionInterface.class).lin(to, speedFactor, parameters);
	}

	@Override
	public CartesianSplineActivity spline(Pose splinePoint, Pose... furtherSplinePoints) throws RoboticsException {
		return use(SplineInterface.class).spline(splinePoint, furtherSplinePoints);
	}

	@Override
	public CartesianSplineActivity spline(double speedFactor, DeviceParameters[] parameters, Pose splinePoint,
			Pose... furtherSplinePoints) throws RoboticsException {
		return use(SplineInterface.class).spline(speedFactor, parameters, splinePoint, furtherSplinePoints);
	}

	@Override
	public CartesianSplineActivity spline(double speedFactor, Pose splinePoint, Pose... furtherSplinePoints)
			throws RoboticsException {
		return use(SplineInterface.class).spline(speedFactor, splinePoint, furtherSplinePoints);
	}

}