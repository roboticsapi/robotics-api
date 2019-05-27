/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.activity;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.roboticsapi.activity.SingleDeviceRtActivity;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.multijoint.MultiJointDevice;
import org.roboticsapi.world.VelocityException;

public abstract class JoggingActivityImpl extends SingleDeviceRtActivity<MultiJointDevice> {

	private final Semaphore brakeSem = new Semaphore(1);

	public JoggingActivityImpl(String name, MultiJointDevice device) {
		super(name, device);
	}

	protected abstract void setVelocityZero() throws RoboticsException;

	protected abstract BooleanSensor getVelocityZeroSensor() throws VelocityException;

	public void stopJogging() throws RoboticsException {
		cancelExecuteInternal();

		endExecute();
	}

	@Override
	protected void cancelExecuteInternal() throws RoboticsException {
		try {
			setVelocityZero();
		} catch (RoboticsException e1) {
			RAPILogger.getLogger().log(RAPILogger.WARNINGLEVEL,
					"Error in waiting setting velocity to 0, will cancel jogging immediately");
			super.cancelExecuteInternal();
			return;
		}

		try {
			SensorListener<Boolean> listener = new SensorListener<Boolean>() {

				@Override
				public void onValueChanged(Boolean newValue) {
					if (newValue) {
						brakeSem.release();
					}

				}
			};

			brakeSem.acquire();

			BooleanSensor noMotion = getVelocityZeroSensor();

			noMotion.addListener(listener);

			brakeSem.tryAcquire(10, TimeUnit.SECONDS);

			noMotion.removeListener(listener);

			super.cancelExecuteInternal();

			return;

		} catch (RoboticsException e) {
			// fallback code below
		} catch (InterruptedException e) {
			// fallback code below
		}

		RAPILogger.getLogger().log(RAPILogger.WARNINGLEVEL,
				"Error in waiting for robot to brake, will cancel jogging after 2 seconds");

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			RAPILogger.getLogger().log(RAPILogger.WARNINGLEVEL,
					"Error in waiting for 2 seconds, will cancel jogging immediately");
		}

		super.cancelExecuteInternal();
	}

}
