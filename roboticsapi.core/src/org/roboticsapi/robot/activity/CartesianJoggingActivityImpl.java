/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.activity;

import java.util.concurrent.Semaphore;

import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityNotCompletedException;
import org.roboticsapi.activity.ActivityStatusListenerAdapter;
import org.roboticsapi.cartesianmotion.action.CartesianJogging;
import org.roboticsapi.cartesianmotion.action.CartesianJoggingException;
import org.roboticsapi.cartesianmotion.action.CartesianJoggingHandle;
import org.roboticsapi.cartesianmotion.activity.CartesianJoggingActivity;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.multijoint.action.AllJointVelocitiesScaledMotion;
import org.roboticsapi.multijoint.activity.JoggingActivityImpl;
import org.roboticsapi.robot.RobotArm;
import org.roboticsapi.world.VelocityException;
import org.roboticsapi.world.sensor.VelocitySensor;

public class CartesianJoggingActivityImpl extends JoggingActivityImpl
		implements CartesianJoggingActivity, CartesianJoggingHandle {

	private final CartesianJogging action;
	private final DeviceParameterBag parameters;
	private boolean executing = false;
	private boolean valid = false;
	private final Semaphore runningSemaphore = new Semaphore(1);
	private RoboticsException exception;

	public CartesianJoggingActivityImpl(RobotArm device, CartesianJogging action, DeviceParameterBag parameters)
			throws RoboticsException {
		super("CartesianJogging", device);
		try {
			runningSemaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.action = action;
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
			public void onActivityFailed(RoboticsException exc) {
				exception = exc;
				valid = false;
				executing = false;

			}
		});
	}

	@Override
	public CartesianJoggingHandle startJogging() throws RoboticsException {
		beginExecute();

		return getHandle();
	}

	@Override
	public CartesianJoggingHandle getHandle() {
		return this;
	}

	@Override
	public void endExecute() throws RoboticsException {
		valid = false;

		super.endExecute();
	}

	@Override
	protected boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException {

		RuntimeCommand joggingCommand = getDevice().getDriver().getRuntime().createRuntimeCommand(getDevice(),
				new AllJointVelocitiesScaledMotion(action, getDevice()), parameters);

		// joggingCommand.addErrorHandler(
		// ((Robot) getDevice()).getIllegalJointValueError(),
		// new ErrorIgnorer());

		setCommand(joggingCommand, prevActivity);

		return false;
	}

	@Override
	public void setCartesianVelocity(double xspeed, double yspeed, double zspeed, double aspeed, double bspeed,
			double cspeed) throws CartesianJoggingException {

		if (!executing && !valid) {
			throw new CartesianJoggingException("Jogging not yet started or interrupted, please (re)start jogging.",
					exception);
		}

		if (executing && !valid) {
			try {
				runningSemaphore.acquire();
				runningSemaphore.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (exception != null) {
			throw new CartesianJoggingException(exception);
		}

		action.getxSpeed().setValue(xspeed);
		action.getySpeed().setValue(yspeed);
		action.getzSpeed().setValue(zspeed);
		action.getaSpeed().setValue(aspeed);
		action.getbSpeed().setValue(bspeed);
		action.getcSpeed().setValue(cspeed);
	}

	@Override
	protected void setVelocityZero() throws CartesianJoggingException {
		setCartesianVelocity(0, 0, 0, 0, 0, 0);
	}

	@Override
	protected BooleanSensor getVelocityZeroSensor() throws VelocityException {
		VelocitySensor velSensor = action.getReferenceFrame().getMeasuredVelocitySensorOf(action.getJoggedFrame(),
				action.getPivotPoint(), action.getOrientation());

		BooleanSensor noMotion = velSensor.getRotationVelocitySensor().getVectorSensor().getLengthSensor()
				.less(Math.toRadians(0.5))
				.and(velSensor.getTranslationVelocitySensor().getVectorSensor().getLengthSensor().less(0.001));

		return noMotion;
	}

}
