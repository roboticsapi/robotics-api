/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.activity;

import java.util.Set;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.activity.runtime.SingleDeviceActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Velocity;
import org.roboticsapi.core.world.realtimevalue.RealtimePose;
import org.roboticsapi.framework.cartesianmotion.action.HoldCartesianPosition;
import org.roboticsapi.framework.cartesianmotion.activity.HoldMotionInterface;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.cartesianmotion.property.CommandedPoseProperty;
import org.roboticsapi.framework.cartesianmotion.property.CommandedVelocityProperty;
import org.roboticsapi.framework.cartesianmotion.property.GoalPoseProperty;

public class HoldMotionInterfaceImpl extends ActuatorInterfaceImpl implements HoldMotionInterface {

	private ActuatorDriver driver;

	public HoldMotionInterfaceImpl(ActuatorDriver driver) {
		super(driver);
		this.driver = driver;
	}

	@Override
	public Activity holdCartesianPosition(final Pose position, final DeviceParameters... parameters)
			throws RoboticsException {
		return holdCartesianPosition(position.asRealtimeValue(), parameters);
	}

	@Override
	public Activity holdCartesianPosition(final RealtimePose position, final DeviceParameters... parameters)
			throws RoboticsException {
		final DeviceParameterBag params = getDefaultParameters().withParameters(parameters);

		return new SingleDeviceActivity("HoldCartesianPosition", driver) {

			@Override
			protected Set<ActivityResult> getResultsForCommand(Command command) throws RoboticsException {
				Pose motionCenter = params.get(MotionCenterParameter.class).getMotionCenter();

				ActivityResult takeoverResult = takeoverResult(
						command.addTakeoverResult("Takeover", RealtimeBoolean.TRUE, false),
						new GoalPoseProperty(position, motionCenter),
						position.isConstant() ? new CommandedPoseProperty(position.getCurrentValue(), motionCenter)
								: null,
						position.isConstant()
								? new CommandedVelocityProperty(
										new Velocity(position.getReference(), null, null, new Twist(0, 0, 0, 0, 0, 0)),
										motionCenter)
								: null);

				ActivityResult completionResult = cancelResult(
						command.addCompletionResult("Cancel", command.getCancelState(), true),
						new GoalPoseProperty(position, motionCenter));

				return activityResultSet(takeoverResult, completionResult);
			}

			@Override
			protected Command getCommandForResult(ActivityResult result) throws RoboticsException {

				return createRuntimeCommand(new HoldCartesianPosition(position), params);
			}
		};
	}
}
