/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.activity;

import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityNotCompletedException;
import org.roboticsapi.activity.ActuatorInterfaceImpl;
import org.roboticsapi.activity.SingleDeviceRtActivity;
import org.roboticsapi.cartesianmotion.action.CartesianBezierSpline;
import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.State;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.TransformationException;

public class SplineInterfaceImpl<T extends CartesianMotionDevice> extends ActuatorInterfaceImpl<T>
		implements SplineInterface {

	public SplineInterfaceImpl(T device) {
		super(device);
	}

	@Override
	public Frame getDefaultMotionCenter() {
		return getDevice().getDefaultMotionCenter();
	}

	@Override
	public final CartesianSplineActivity spline(Frame splinePoint, Frame... furtherSplinePoints)
			throws RoboticsException {
		return spline(new DeviceParameters[0], splinePoint, furtherSplinePoints);
	}

	@Override
	public CartesianSplineActivity spline(DeviceParameters[] parameters, final Frame splinePoint,
			final Frame... furtherSplinePoints) throws RoboticsException {

		final CartesianMotionDevice device = getDevice();
		DeviceParameterBag param = getDefaultParameters().withParameters(parameters);
		final MotionCenterParameter motionCenter = param.get(MotionCenterParameter.class);

		if (motionCenter == null) {
			throw new RoboticsException("No motion center parameters given");
		}

		CartesianParameters cp = param.get(CartesianParameters.class);
		if (cp == null) {
			throw new RoboticsException("No Cartesian paramters given");
		}

		final CartesianBezierSpline spline = new CartesianBezierSpline(cp.getMaximumPositionVelocity(),
				cp.getMaximumRotationVelocity());
		try {
			if (motionCenter.getMotionCenter().getTransformationTo(splinePoint, false) != null) {
				throw new RoboticsException("Found static Transformation between motion target and motion center");
			}
		} catch (TransformationException e) {
			// that's okay
			// TODO: Maybe switch back to returning null instead of exception
			// throwing
		}

		for (Frame f : furtherSplinePoints) {
			try {
				if (motionCenter.getMotionCenter().getTransformationTo(f, false) != null) {
					throw new RoboticsException("Found static Transformation between motion target and motion center");
				}
			} catch (TransformationException e) {
				// that's okay
				// TODO: Maybe switch back to returning null instead of
				// exception throwing
			}
		}

		return new SplineActivity(device.getName() + ".spline()", furtherSplinePoints, spline, device, splinePoint,
				motionCenter, param);
	}

	private final class SplineActivity extends SingleDeviceRtActivity<CartesianMotionDevice>
			implements CartesianSplineActivity {
		private final Frame[] furtherSplinePoints;
		private final CartesianBezierSpline spline;
		private final CartesianMotionDevice device;
		private final Frame splinePoint;
		private final MotionCenterParameter motionCenter;
		private final DeviceParameterBag parameters;

		private SplineActivity(String name, Frame[] furtherSplinePoints, CartesianBezierSpline spline,
				CartesianMotionDevice device, Frame splinePoint, MotionCenterParameter motionCenter,
				DeviceParameterBag parameters) {
			super(name, device);
			this.furtherSplinePoints = furtherSplinePoints;
			this.spline = spline;
			this.device = device;
			this.splinePoint = splinePoint;
			this.motionCenter = motionCenter;
			this.parameters = parameters;
		}

		@Override
		protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {

			Frame from = new Frame("[MotionInterfaceImpl.spline start frame]", splinePoint,
					splinePoint.getTransformationTo(motionCenter.getMotionCenter(), true));

			spline.addPoint(from);
			spline.addPoint(splinePoint);
			for (final Frame f : furtherSplinePoints) {
				spline.addPoint(f);
			}

			addProperty(device, new FrameGoalProperty(spline.getTo(), motionCenter.getMotionCenter()));

			Command cmd = device.getDriver().getRuntime().createRuntimeCommand(device, spline, parameters);
			setCommand(cmd, prevActivity);
			return false; // never true
		}

		@Override
		public State getMotionTimeProgress(double progress) {
			return spline.getMotionTimeProgress((float) progress);
		}

		@Override
		public State atSplinePoint(int pointIndex) {
			return spline.getAtPoint(pointIndex);
		}
	}

}
