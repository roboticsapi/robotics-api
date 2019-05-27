/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityNotCompletedException;
import org.roboticsapi.activity.SingleDeviceRtActivity;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.ActionCancelledException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.MultiJointDevice;
import org.roboticsapi.multijoint.action.FollowJointGoal;

public class FollowJointGoalActivityImpl extends SingleDeviceRtActivity<MultiJointDevice> {

	private FollowJointGoal action;
	protected final MultiJointDevice robot;
	protected DoubleSensor[] sensors;
	protected final DeviceParameterBag parameters;

	public FollowJointGoalActivityImpl(MultiJointDevice robot, DoubleSensor[] sensors, DeviceParameterBag parameters)
			throws RoboticsException {
		super("FollowJointGoal", robot);
		this.robot = robot;
		this.sensors = sensors;
		this.parameters = parameters;

		if (sensors != null && sensors.length != robot.getJointCount()) {
			throw new RoboticsException("Expected " + robot.getJointCount()
					+ " DoubleSensors (one for each robot axis), but " + sensors.length + " were provided");
		}
	}

	@Override
	protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {

		action = new FollowJointGoal(sensors);

		RuntimeCommand command = getDevice().getDriver().getRuntime().createRuntimeCommand(getDevice(), action,
				parameters);

		setCommand(command, prevActivity);
		return false;
	}

	@Override
	protected void cancelExecuteInternal() throws RoboticsException {
		super.cancelExecuteInternal();

		try {
			endExecute();
		} catch (ActionCancelledException exc) {
			// this is okay here
		}
	}

}