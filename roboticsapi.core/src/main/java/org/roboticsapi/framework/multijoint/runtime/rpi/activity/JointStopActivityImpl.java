/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi.activity;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.activity.AbstractActivity;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActivityHandle;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.ActivityResultContainer;
import org.roboticsapi.core.activity.ActivitySchedule;
import org.roboticsapi.core.exception.CommunicationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.framework.multijoint.MultiJointDeviceDriver;
import org.roboticsapi.framework.multijoint.activity.JointVelocityMotionInterface;
import org.roboticsapi.framework.multijoint.property.CommandedPositionProperty;
import org.roboticsapi.framework.multijoint.property.CommandedVelocityProperty;
import org.roboticsapi.framework.multijoint.property.GoalProperty;

public class JointStopActivityImpl extends AbstractActivity {

	private final Activity stop;
	private final MultiJointDeviceDriver driver;

	protected JointStopActivityImpl(MultiJointDeviceDriver driver, DeviceParameterBag parameters)
			throws RoboticsException {
		super("Stop " + driver.getDevice(), driver.getDevice());
		this.driver = driver;
		stop = driver.getDevice().use(JointVelocityMotionInterface.class).followVelocity(
				RealtimeDoubleArray.createFromConstants(new double[driver.getDevice().getJointCount()]),
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
							if (status != ActivityResult.Status.ACTIVE) {
								return;
							}
							try {
								double[] pos = driver.getJointAngles();
								rr.addMetadata(driver.getDevice(), new GoalProperty(pos));
								rr.addMetadata(driver.getDevice(), new CommandedPositionProperty(pos));
								rr.addMetadata(driver.getDevice(),
										new CommandedVelocityProperty(new double[driver.getJointCount()]));
								results.addResult(rr);
							} catch (CommunicationException e) {
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
