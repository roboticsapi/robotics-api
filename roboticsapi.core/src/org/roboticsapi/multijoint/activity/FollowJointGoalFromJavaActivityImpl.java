/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import java.util.concurrent.Semaphore;

import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityNotCompletedException;
import org.roboticsapi.activity.ActivityStatus;
import org.roboticsapi.activity.ActivityStatusListenerAdapter;
import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.multijoint.MultiJointDevice;

public class FollowJointGoalFromJavaActivityImpl extends FollowJointGoalActivityImpl
		implements FollowJointGoalFromJavaActivity, FollowJointGoalHandle {

	private RoboticsException exception;
	private boolean valid = false;
	Semaphore startSem = new Semaphore(1);

	public FollowJointGoalFromJavaActivityImpl(MultiJointDevice multiJointDevice, DeviceParameterBag parameters)
			throws RoboticsException {
		super(multiJointDevice, null, parameters);

		addStatusListener(new ActivityStatusListenerAdapter() {
			@Override
			public void onActivityFailed(RoboticsException exc) {
				exception = exc;
				valid = false;
				startSem.release();

			}

			@Override
			public void onActivityStarted() {
				valid = true;
				startSem.release();
			}

			@Override
			public void onActivityCompleted() {
				valid = false;

				startSem.release();
			}
		});
	}

	@Override
	public void updateGoal(Double... goalValues) throws RoboticsException {
		double[] goalValues2 = new double[goalValues.length];

		for (int i = 0; i < goalValues2.length; i++) {
			goalValues2[i] = goalValues[i];
		}

		updateGoal(goalValues2);

	}

	@Override
	public void updateGoal(double[] goalValues) throws RoboticsException {
		if (!valid) {
			if (exception != null) {
				throw new RoboticsException("FollowJointGoal was interrupted, please restart.", exception);
			}
			throw new RoboticsException("FollowJointGoal was interrupted for unknown reason, please restart.");
		}

		if (goalValues.length != sensors.length) {
			throw new RoboticsException("FollowJointGoal for robot " + getDevice().getName() + " requires "
					+ sensors.length + " values, but " + goalValues.length + " were given");
		}

		for (int i = 0; i < sensors.length; i++) {
			((DoubleFromJavaSensor) sensors[i]).setValue(goalValues[i]);
		}
	}

	@Override
	public RtActivity beginExecute() throws RoboticsException {
		RtActivity beginExecute = super.beginExecute();

		try {
			startSem.acquire();
			startSem.acquire();
		} catch (InterruptedException e) {
			throw new RoboticsException("Error starting FollowJointGoalFromJavaActivity", e);
		}

		return beginExecute;
	}

	@Override
	protected void cancelExecuteInternal() throws RoboticsException {
		super.cancelExecuteInternal();

		startSem.release();
	}

	@Override
	public FollowJointGoalHandle startFollowing() throws RoboticsException {
		beginExecute();

		return getHandle();
	}

	@Override
	public FollowJointGoalHandle getHandle() {
		return this;
	}

	@Override
	protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {
		double[] startJointValues = null;

		if (prevActivity != null) {

			JointGoalProperty prop = prevActivity.getProperty(robot, JointGoalProperty.class);

			if (prop != null) {

				startJointValues = prop.getGoal();
			} else {
				if (prevActivity.getStatus() == ActivityStatus.RUNNING) {
					throw new ActivityNotCompletedException(prevActivity);
				}
			}

		}

		if (startJointValues == null) {
			startJointValues = robot.getJointAngles();
		}

		if (startJointValues.length < robot.getJointCount()) {
			throw new RoboticsException("Could not determine initial joint goal for all axes");
		}

		sensors = new DoubleFromJavaSensor[robot.getJointCount()];

		for (int i = 0; i < robot.getJointCount(); i++) {
			sensors[i] = new DoubleFromJavaSensor(startJointValues[i]);
		}

		return super.prepare(prevActivity);
	}

	@Override
	public boolean isValid() {
		return valid;
	}

}
