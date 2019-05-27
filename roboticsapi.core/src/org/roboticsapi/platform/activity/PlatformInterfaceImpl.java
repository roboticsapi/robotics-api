/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.platform.activity;

import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityNotCompletedException;
import org.roboticsapi.activity.ActuatorInterfaceImpl;
import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.activity.SingleDeviceRtActivity;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.platform.Platform;
import org.roboticsapi.platform.action.DriveDirection;
import org.roboticsapi.platform.action.DriveTo;
import org.roboticsapi.platform.action.FollowFrame;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;

public class PlatformInterfaceImpl extends ActuatorInterfaceImpl<Platform> implements PlatformInterface {

	public PlatformInterfaceImpl(Platform device) {
		super(device);
	}

	@Override
	public RtActivity driveTo(Frame goal, DeviceParameters... parameters) throws RoboticsException {
		DriveTo action = new DriveTo(goal);

		RuntimeCommand cmd = getDevice().getDriver().getRuntime().createRuntimeCommand(getDevice(), action,
				getDefaultParameters().withParameters(parameters));

		return new SingleDeviceRtActivity<Platform>(getDevice(), cmd, null) {

			@Override
			protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {
				return false;
			}
		};
	}

	@Override
	public RtActivity driveDirection(Orientation orientation, DoubleSensor xVel, DoubleSensor yVel, DoubleSensor aVel,
			DeviceParameters... parameters) throws RoboticsException {
		DriveDirection action = new DriveDirection(getDevice().getOdometryOrigin(), orientation, xVel, yVel, aVel);

		RuntimeCommand cmd = getDevice().getDriver().getRuntime().createRuntimeCommand(getDevice(), action,
				getDefaultParameters().withParameters(parameters));

		return new SingleDeviceRtActivity<Platform>(getDevice(), cmd, null) {

			@Override
			protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {
				return false;
			}
		};
	}

	@Override
	public RtActivity driveDirection(Orientation orientation, double xVel, double yVel, double aVel,
			DeviceParameters... parameters) throws RoboticsException {
		return driveDirection(orientation, new DoubleFromJavaSensor(xVel), new DoubleFromJavaSensor(yVel),
				new DoubleFromJavaSensor(aVel), parameters);
	}

	@Override
	public RtActivity driveDirection(DoubleSensor xVel, DoubleSensor yVel, DoubleSensor aVel,
			DeviceParameters... parameters) throws RoboticsException {
		return driveDirection(getDevice().getOdometryFrame().getOrientation(), xVel, yVel, aVel, parameters);
	}

	@Override
	public RtActivity driveDirection(double xVel, double yVel, double aVel, DeviceParameters... parameters)
			throws RoboticsException {
		return driveDirection(getDevice().getOdometryFrame().getOrientation(), new DoubleFromJavaSensor(xVel),
				new DoubleFromJavaSensor(yVel), new DoubleFromJavaSensor(aVel), parameters);
	}

	@Override
	public RtActivity followGoal(Frame goal, DeviceParameters... parameters) throws RoboticsException {
		FollowFrame action = new FollowFrame(goal);

		RuntimeCommand cmd = getDevice().getDriver().getRuntime().createRuntimeCommand(getDevice(), action,
				getDefaultParameters().withParameters(parameters));

		cmd.addDoneStateCondition(cmd.getCancelState());

		return new SingleDeviceRtActivity<Platform>(getDevice(), cmd, null) {

			@Override
			protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {
				return false;
			}
		};
	}

}
