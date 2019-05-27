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
import org.roboticsapi.activity.ActivityStatusListenerAdapter;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.multijoint.Joint;
import org.roboticsapi.multijoint.MultiJointDevice;
import org.roboticsapi.multijoint.action.AllJointVelocitiesScaledMotion;
import org.roboticsapi.multijoint.action.FollowJointVelocity;
import org.roboticsapi.multijoint.action.JointJoggingException;
import org.roboticsapi.multijoint.action.JointJoggingHandle;
import org.roboticsapi.world.VelocityException;

public class JointJoggingActivityImpl extends JoggingActivityImpl implements JointJoggingActivity, JointJoggingHandle {

	private final FollowJointVelocity action;
	private final DoubleFromJavaSensor[] sensors;
	private final DeviceParameterBag parameters;

	private final Semaphore runningSemaphore = new Semaphore(1);
	private boolean executing = false;
	private boolean valid = false;
	private RoboticsException exception;

	public JointJoggingActivityImpl(MultiJointDevice device, DeviceParameterBag parameters) throws RoboticsException {
		super("JointJogging", device);
		try {
			runningSemaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int jointCount = device.getJointCount();
		sensors = new DoubleFromJavaSensor[jointCount];
		double limits[] = new double[jointCount];

		for (int i = 0; i < jointCount; i++) {
			sensors[i] = new DoubleFromJavaSensor(0);
			limits[i] = device.getJoint(i).getMaximumVelocity();
		}

		this.action = new FollowJointVelocity(sensors, limits);
		this.parameters = parameters;

		addStatusListener(new ActivityStatusListenerAdapter() {

			@Override
			public void onActivityScheduled() {
				executing = true;
			}

			@Override
			public void onActivityStarted() {
				valid = true;
				runningSemaphore.release();
			}

			@Override
			public void onActivityCompleted() {
				valid = false;
				executing = false;
			}

			@Override
			public void onActivityFailed(RoboticsException roboticsException) {
				exception = roboticsException;
				valid = false;
				executing = false;
			}
		});
	}

	@Override
	public JointJoggingHandle startJogging() throws RoboticsException {
		beginExecute();

		return getHandle();
	}

	@Override
	public JointJoggingHandle getHandle() {
		return this;
	}

	@Override
	public void endExecute() throws RoboticsException {
		valid = false;

		super.endExecute();
	}

	@Override
	protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {

		RuntimeCommand jogging = getDevice().getDriver().getRuntime().createRuntimeCommand(getDevice(),
				new AllJointVelocitiesScaledMotion(action, getDevice()), parameters);

		setCommand(jogging, prevActivity);
		return false;
	}

	@Override
	public void setJointVelocity(int jointNumber, double velocity) throws RoboticsException {
		if (jointNumber >= action.getJointCount() || jointNumber < 0) {
			throw new IllegalArgumentException(
					"jointNumber must be greater zero and smaller than the number of joints");
		}

		if (!executing && !valid) {
			throw new JointJoggingException("Jogging not yet started or interrupted, please (re)start jogging.",
					exception);
		}

		if (executing && !valid) {
			try {
				runningSemaphore.acquire();
				runningSemaphore.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (exception != null) {
			throw exception;
		}

		sensors[jointNumber].setValue(velocity);
	}

	@Override
	protected void setVelocityZero() throws RoboticsException {
		for (int i = 0; i < getDevice().getJointCount(); i++) {
			setJointVelocity(i, 0);
		}

	}

	@Override
	protected BooleanSensor getVelocityZeroSensor() throws VelocityException {
		BooleanSensor sensor = null;
		for (Joint j : getDevice().getJoints()) {
			BooleanSensor less = j.getMeasuredVelocitySensor().less(Math.toRadians(0.5));

			if (sensor == null) {
				sensor = less;
			} else {
				sensor = sensor.and(less);
			}
		}

		return sensor != null ? sensor : BooleanSensor.fromValue(true);
	}

}
