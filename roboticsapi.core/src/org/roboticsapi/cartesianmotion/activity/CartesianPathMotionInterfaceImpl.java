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
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;

public class CartesianPathMotionInterfaceImpl<A extends CartesianMotionDevice> extends ActuatorInterfaceImpl<A>
		implements CartesianPathMotionInterface {

	private final LinearMotionInterface lin;
	private final SplineInterface spline;

	public CartesianPathMotionInterfaceImpl(A device, LinearMotionInterface lin, SplineInterface spline) {
		super(device);
		this.lin = lin;
		this.spline = spline;
	}

	@Override
	public Frame getDefaultMotionCenter() {
		MotionCenterParameter mc = getDefaultParameters().get(MotionCenterParameter.class);
		return mc == null ? null : mc.getMotionCenter();
	}

	@Override
	public PlannedRtActivity lin(Frame to, DeviceParameters... parameters) throws RoboticsException {
		return lin.lin(to, getDefaultParameters().withParameters(parameters).getArray());
	}

	@Override
	public PlannedRtActivity lin(Frame to, double speedFactor, DeviceParameters... parameters)
			throws RoboticsException {
		return lin.lin(to, speedFactor, getDefaultParameters().withParameters(parameters).getArray());
	}

	@Override
	public CartesianSplineActivity spline(DeviceParameters[] parameters, Frame splinePoint,
			Frame... furtherSplinePoints) throws RoboticsException {
		return spline.spline(getDefaultParameters().withParameters(parameters).getArray(), splinePoint,
				furtherSplinePoints);
	}

	@Override
	public CartesianSplineActivity spline(Frame splinePoint, Frame... furtherSplinePoints) throws RoboticsException {
		return spline.spline(getDefaultParameters().getArray(), splinePoint, furtherSplinePoints);
	}

}