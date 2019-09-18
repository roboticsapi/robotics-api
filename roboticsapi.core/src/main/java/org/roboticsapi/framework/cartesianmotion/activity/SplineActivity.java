/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.activity;

import java.util.Set;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.runtime.PlannedSingleDeviceActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.DynamicScopeRealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.World;
import org.roboticsapi.framework.cartesianmotion.action.CartesianBezierSpline;
import org.roboticsapi.framework.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.cartesianmotion.property.CommandedPoseProperty;
import org.roboticsapi.framework.cartesianmotion.property.GoalPoseProperty;

public final class SplineActivity extends PlannedSingleDeviceActivity implements CartesianSplineActivity {
	private final Pose[] furtherSplinePoints;
	private final Pose splinePoint;
	private final MotionCenterParameter motionCenter;
	private final DeviceParameterBag parameters;
	private final double speedFactor;

	public SplineActivity(String name, double speedFactor, Pose[] furtherSplinePoints, ActuatorDriver driver,
			Pose splinePoint, MotionCenterParameter motionCenter, DeviceParameterBag parameters)
			throws RoboticsException {
		super(name, driver);
		this.speedFactor = speedFactor;
		this.furtherSplinePoints = furtherSplinePoints;
		this.splinePoint = splinePoint;
		this.motionCenter = motionCenter;
		this.parameters = parameters;
	}

	@Override
	public RealtimeBoolean getMotionTimeProgress(final double progress) {
		return new DynamicScopeRealtimeBoolean() {
			@Override
			public RealtimeBoolean getScopedState(Command scope) {
				return findSpline(scope).getMotionTimeProgress(scope, (float) progress);
			}
		};
	}

	@Override
	public RealtimeBoolean atSplinePoint(final int pointIndex) {
		return new DynamicScopeRealtimeBoolean() {
			@Override
			public RealtimeBoolean getScopedState(Command scope) {
				return findSpline(scope).getAtPoint(scope, pointIndex);
			}
		};
	}

	protected CartesianBezierSpline findSpline(Command command) {
		if (command instanceof RuntimeCommand) {
			Action action = ((RuntimeCommand) command).getAction();
			if (action instanceof CartesianBezierSpline) {
				return (CartesianBezierSpline) action;
			}
		}
		return null;
	}

	@Override
	protected Set<ActivityResult> getResultsForCommand(Command command) {

		CartesianBezierSpline spline = findSpline(command);
		Pose mc = motionCenter.getMotionCenter();
		ActivityResult completionResult = completionResult(((RuntimeCommand) command).getCompletionResult(),
				new GoalPoseProperty(spline.getTo().asRealtimeValue(), mc),
				new CommandedPoseProperty(spline.getTo(), mc));
		ActivityResult cancelResult = cancelResult(((RuntimeCommand) command).getCancelResult(),
				new GoalPoseProperty(spline.getTo().asRealtimeValue(), mc));

		return activityResultSet(completionResult, cancelResult);
	}

	@Override
	protected Command getCommandForResult(ActivityResult result) throws RoboticsException {

		CartesianParameters cp = parameters.get(CartesianParameters.class);
		if (cp == null) {
			throw new RoboticsException("No Cartesian paramters given");
		}

		Pose from = motionCenter.getMotionCenter().asRealtimeValue()
				.convertToRepresentation(splinePoint.getReference().asOrientation(), splinePoint.getReference(),
						World.getCommandedTopology())
				.getCurrentValue();
		Pose[] points = new Pose[furtherSplinePoints.length + 2];

		points[0] = from;
		points[1] = splinePoint;
		for (int i = 0; i < furtherSplinePoints.length; i++) {
			points[i + 2] = furtherSplinePoints[i];
		}

		CartesianBezierSpline spline = new CartesianBezierSpline(cp.getMaximumPositionVelocity() * speedFactor,
				cp.getMaximumRotationVelocity() * speedFactor * speedFactor, points);
		return createRuntimeCommand(spline, parameters);
	}
}