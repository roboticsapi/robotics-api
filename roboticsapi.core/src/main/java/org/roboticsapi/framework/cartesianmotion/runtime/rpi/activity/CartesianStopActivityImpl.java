/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.activity;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.activity.AbstractActivity;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActivityHandle;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.ActivityResultContainer;
import org.roboticsapi.core.activity.ActivitySchedule;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Velocity;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianVelocityMotionInterface;
import org.roboticsapi.framework.cartesianmotion.device.CartesianActuatorDriver;
import org.roboticsapi.framework.cartesianmotion.property.CommandedPoseProperty;
import org.roboticsapi.framework.cartesianmotion.property.CommandedVelocityProperty;
import org.roboticsapi.framework.cartesianmotion.property.GoalPoseProperty;

public class CartesianStopActivityImpl extends AbstractActivity {

	private Activity stop;
	private CartesianActuatorDriver driver;
	private Frame reference;

	protected CartesianStopActivityImpl(CartesianActuatorDriver driver, Frame reference, DeviceParameterBag parameters)
			throws RoboticsException {
		super("Stop " + driver.getDevice(), driver.getDevice());
		this.driver = driver;
		this.reference = reference;
		stop = driver.getDevice().use(CartesianVelocityMotionInterface.class).followVelocity(
				RealtimeVelocity.createFromConstant(new Twist(), driver.getDevice().getReferenceFrame(), null, null),
				parameters.getArray());
		stop.addCancelCondition(driver.getDevice().getCompletedState(null));
	}

	@Override
	public ActivityHandle createHandle() throws RoboticsException {
		ActivityHandle stopHandle = stop.createHandle();
		return new ActivityHandle(this) {
			@Override
			public ActivitySchedule prepare(ActivityResult result, StackTraceElement[] errorStack)
					throws RoboticsException {
				ActivitySchedule schedule = stopHandle.prepare(result, errorStack);
				ActivityResultContainer results = new ActivityResultContainer();
				schedule.getResults().provide(rr -> {
					if (rr == null) {
						return;
					} else if (rr.isFailedWhenActive() != null) {
						results.addResult(rr);
					} else if (rr.isCompletedWhenActive()) {
						rr.observeStatus(status -> {
							if (status != ActivityResult.Status.ACTIVE)
								return;
							try {
								Frame movingFrame = driver.getDevice().getMovingFrame();
								Transformation pos = reference.getTransformationTo(movingFrame);
								rr.addMetadata(driver.getDevice(),
										new GoalPoseProperty(reference, pos, movingFrame.asPose()));
								rr.addMetadata(driver.getDevice(),
										new CommandedPoseProperty(reference, pos, movingFrame.asPose()));
								rr.addMetadata(driver.getDevice(), new CommandedVelocityProperty(
										new Velocity(reference, new Twist()), movingFrame.asPose()));
								results.addResult(rr);
							} catch (TransformationException e) {
								RAPILogger.logException(this, e);
							}
						});
					}
				});
				return schedule.withResults(this, results);
			}
		};
	}

}
