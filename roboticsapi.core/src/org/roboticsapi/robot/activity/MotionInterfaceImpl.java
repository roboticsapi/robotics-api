/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.activity;

import java.util.Arrays;

import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityNotCompletedException;
import org.roboticsapi.activity.PlannedRtActivity;
import org.roboticsapi.activity.SingleDeviceRtActivity;
import org.roboticsapi.cartesianmotion.action.CartesianBezierSpline;
import org.roboticsapi.cartesianmotion.activity.CartesianSplineActivity;
import org.roboticsapi.cartesianmotion.activity.FrameGoalProperty;
import org.roboticsapi.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.State;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.multijoint.activity.JointPtpInterfaceImpl;
import org.roboticsapi.robot.KinematicsException;
import org.roboticsapi.robot.RobotArm;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.TeachingInfo;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;

public class MotionInterfaceImpl<T extends RobotArm> extends JointPtpInterfaceImpl<T> implements MotionInterface {

	private final class SplineActivity extends SingleDeviceRtActivity<RobotArm> implements CartesianSplineActivity {
		private final Frame[] furtherSplinePoints;
		private final CartesianBezierSpline spline;
		private final RobotArm robot;
		private final Frame splinePoint;
		private final MotionCenterParameter motionCenter;
		private final Command cmd;

		private SplineActivity(String name, RobotArm device, Frame[] furtherSplinePoints, CartesianBezierSpline spline,
				RobotArm robot, Frame splinePoint, MotionCenterParameter motionCenter, Command cmd) {
			super(name, device);
			this.furtherSplinePoints = furtherSplinePoints;
			this.spline = spline;
			this.robot = robot;
			this.splinePoint = splinePoint;
			this.motionCenter = motionCenter;
			this.cmd = cmd;
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

			addProperty(robot, new FrameGoalProperty(spline.getTo(), motionCenter.getMotionCenter()));

			setCommand(cmd, prevActivity);
			return false; // never true

			// }

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

	public MotionInterfaceImpl(T robot) {
		super(robot);

		// try {
		// addDefaultParameters(new BlendingParameter(1));
		// } catch (InvalidParametersException e) {
		// RAPILogger.getLogger().log(RAPILogger.WARNINGLEVEL,
		// "BlendingParameter rejected");
		// }
	}

	@Override
	public double[] getHintJointsFromFrame(Frame frame, DeviceParameters... parameters) {
		DeviceParameterBag param = getDefaultParameters().withParameters(parameters);
		final MotionCenterParameter motionCenter = param.get(MotionCenterParameter.class);

		TeachingInfo teachingInfo = frame.getTeachingInfo(getDevice(), motionCenter.getMotionCenter());

		double[] hintJoints = null;
		if (teachingInfo != null) {
			hintJoints = teachingInfo.getHintParameters();
		}

		return hintJoints;
	}

	@Override
	public final PlannedRtActivity lin(final Frame to, final DeviceParameters... parameters) throws RoboticsException {

		return lin(to, getHintJointsFromFrame(to, parameters), parameters);
	}

	@Override
	public final PlannedRtActivity lin(final Frame to, double[] nullspaceJoints, final DeviceParameters... parameters)
			throws RoboticsException {
		return lin(to, nullspaceJoints, 1, parameters);
	}

	@Override
	public final PlannedRtActivity lin(Frame to, double speedFactor, DeviceParameters... parameters)
			throws RoboticsException {
		return lin(to, getHintJointsFromFrame(to, parameters), speedFactor, parameters);
	}

	@Override
	public PlannedRtActivity lin(Frame to, double[] nullspaceJoints, double speedFactor, DeviceParameters... parameters)
			throws RoboticsException {

		if (speedFactor < 0 || speedFactor > 1) {
			throw new IllegalArgumentException("Only values between 0 and 1 allowed for speedFactor");
		}

		final RobotArm robot = getDevice();

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

		// return new RobotArmLinActivityImpl(robot.getName() + ".lin("
		// + to.getName() + ")", robot, to, nullspaceJoints, speedFactor,
		// motionCenter, param);
		return new LinActivityImpl(robot.getName() + ".lin(" + to.getName() + ")", robot, to, nullspaceJoints,
				speedFactor, motionCenter, param);
	}

	@Override
	public final CartesianSplineActivity spline(Frame splinePoint, Frame... furtherSplinePoints)
			throws RoboticsException {
		return spline(new DeviceParameters[] {}, splinePoint, furtherSplinePoints);
	}

	@Override
	public CartesianSplineActivity spline(DeviceParameters[] parameters, final Frame splinePoint,
			final Frame... furtherSplinePoints) throws RoboticsException {

		final RobotArm robot = getDevice();
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

		final Command cmd = robot.getDriver().getRuntime().createRuntimeCommand(robot, spline, param);

		return new SplineActivity(robot.getName() + ".spline()", robot, furtherSplinePoints, spline, robot, splinePoint,
				motionCenter, cmd);

	}

	@Override
	public PlannedRtActivity ptp(final double[] to, DeviceParameters... parameters) throws RoboticsException {
		final RobotArm robot = getDevice();
		PtpActivityImpl activityImpl = new PtpActivityImpl(robot.getName() + ".ptp(" + Arrays.toString(to) + ")", robot,
				getDefaultParameters().withParameters(parameters)) {

			@Override
			protected double[] getTarget(double[] from) {
				return to;
			}
		};

		return activityImpl;
	}

	@Override
	public final PlannedRtActivity ptp(Frame to, double[] hintJoints, DeviceParameters... parameters)
			throws RoboticsException {

		final RobotArm robot = getDevice();
		DeviceParameterBag param = getDefaultParameters().withParameters(parameters);
		final MotionCenterParameter motionCenter = param.get(MotionCenterParameter.class);

		if (motionCenter == null) {
			throw new RoboticsException("No motion center parameters given");
		}

		try {
			if (motionCenter.getMotionCenter().getTransformationTo(to, false) != null) {
				throw new RoboticsException("Found static Transformation between motion target (" + to
						+ ") and motion center (" + motionCenter.getMotionCenter() + ")");
			}
		} catch (TransformationException e) {
			// that's okay
			// TODO: Maybe switch back to returning null instead of exception
			// throwing
		}

		final Transformation point = robot.getBase().getTransformationTo(to)
				.multiply(robot.getFlange().getTransformationTo(motionCenter.getMotionCenter()).invert());

		double[] inverseKinematics = calculateInverseKinematics(point, hintJoints, param);
		return ptp(inverseKinematics, parameters);
	}

	protected double[] calculateInverseKinematics(Transformation point, double[] hintJoints, DeviceParameterBag params)
			throws KinematicsException, CommunicationException, RoboticsException {
		final RobotArm robot = getDevice();
		if (hintJoints == null) {
			hintJoints = robot.getJointAngles();
		}
		double[] ds = robot.getInverseKinematics(point, hintJoints, params);

		return checkInverseKinematicsSolution(ds);
	}

	protected double[] checkInverseKinematicsSolution(double[] solution) throws KinematicsException {
		for (int i = 0; i < solution.length; i++) {
			if (Double.isNaN(solution[i])) {
				throw new KinematicsException(
						"Could not calculate appropriate kinematics solution (one or more values are NaN)");
			}
		}

		return solution;
	}

	@Override
	public final PlannedRtActivity ptp(Frame to, DeviceParameters... parameters) throws RoboticsException {

		return ptp(to, getHintJointsFromFrame(to, parameters), parameters);

		// DeviceParameterBag param = getDefaultParameters().withParameters(
		// parameters);
		//
		// final MotionCenterParameters motionCenter = param
		// .get(MotionCenterParameters.class);
		//
		// if (motionCenter == null) {
		// throw new RoboticsException("No motion center parameters given");
		// }
		//
		// final Robot robot = getDevice();
		//
		// final Transformation point = robot
		// .getBase()
		// .getTransformationTo(to)
		// .multiply(
		// robot.getFlange()
		// .getTransformationTo(
		// motionCenter.getMotionCenter())
		// .invert());
		//
		// return new PtpActivityImpl(robot.getName() + ".ptp(" + to.getName()
		// + ")", robot, this.blendingPercent, param) {
		//
		// @Override
		// protected double[] getTarget(double[] from)
		// throws RoboticsException {
		// return robot.getInverseKinematics(point, from);
		// }
		//
		// };
	}

	@Override
	public Frame getDefaultMotionCenter() {
		MotionCenterParameter mcp = getDefaultParameters().get(MotionCenterParameter.class);

		if (mcp == null) {
			return null;
		}

		return mcp.getMotionCenter();
	}

	@Override
	public Frame touchup() throws RoboticsException {
		return touchup(getDefaultMotionCenter());
	}

	@Override
	public Frame touchup(Frame motionCenter) throws RoboticsException {
		Frame snapshot = motionCenter.snapshot(getDevice().getBase());

		snapshot.addTeachingInfo(getDevice(), motionCenter, getDevice().getJointAngles());

		return snapshot;
	}

}
