/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.activity;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.activity.Activity;
import org.roboticsapi.activity.ActivityStatus;
import org.roboticsapi.activity.ActivityStatusListener;
import org.roboticsapi.activity.FromCommandActivity;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.runtime.generic.AbstractRuntimeTest;
import org.roboticsapi.runtime.mockclass.TestActuator;
import org.roboticsapi.runtime.mockclass.TestDevice;
import org.roboticsapi.runtime.mockclass.TestDeviceDriver;

public abstract class AbstractActivityTest extends AbstractRuntimeTest {

	private boolean firstMaintaining;
	private boolean secondStartedTooEarly;

	@Test(timeout = 3000)
	public void testSecondActivityScheduledAfterFirstActivityInMaintaining() throws RoboticsException {

		firstMaintaining = false;
		secondStartedTooEarly = false;

		TestActuator actuator = new TestActuator("TestActuator", getRuntime());

		Command wait = getRuntime().createWaitCommand(1);
		FromCommandActivity a = new FromCommandActivity("Wait1", wait, wait.getActiveState().forSeconds(.5), actuator);

		a.addStatusListener(new ActivityStatusListener() {

			@Override
			public void activityStatusChanged(Activity activity, ActivityStatus newStatus) {
				firstMaintaining = newStatus == ActivityStatus.MAINTAINING;

			}
		});

		a.beginExecute();

		Command wait2 = getRuntime().createWaitCommand(1);
		FromCommandActivity a2 = new FromCommandActivity("Wait2", wait2, wait2.getActiveState().forSeconds(.5),
				actuator);

		a2.addStatusListener(new ActivityStatusListener() {

			@Override
			public void activityStatusChanged(Activity activity, ActivityStatus newStatus) {
				secondStartedTooEarly = newStatus == ActivityStatus.RUNNING && !firstMaintaining;

			}
		});

		a2.beginExecute();

		a.endExecute();
		a2.endExecute();

		Assert.assertFalse(secondStartedTooEarly);
	}

	boolean runningParallel;

	@Test(timeout = 3000)
	public void testIndependentActivitiesRunningParallel() throws RoboticsException {

		runningParallel = false;

		TestActuator actuator1 = new TestActuator("TestActuator1", getRuntime());
		TestActuator actuator2 = new TestActuator("TestActuator2", getRuntime());
		TestActuator actuator3 = new TestActuator("TestActuator3", getRuntime());

		Command wait = getRuntime().createWaitCommand(2);
		final FromCommandActivity a = new FromCommandActivity("Wait1", wait, wait.getActiveState().forSeconds(1),
				actuator1);

		Command wait2 = getRuntime().createWaitCommand(2);
		final FromCommandActivity a2 = new FromCommandActivity("Wait2", wait2, wait2.getActiveState().forSeconds(1),
				actuator2);

		Command wait3 = getRuntime().createWaitCommand(2);
		final FromCommandActivity a3 = new FromCommandActivity("Wait3", wait3, wait3.getActiveState().forSeconds(1),
				actuator3);

		a.addStatusListener(new ActivityStatusListener() {

			@Override
			public void activityStatusChanged(Activity activity, ActivityStatus newStatus) {
				if (newStatus == ActivityStatus.RUNNING && a2.getStatus() == ActivityStatus.RUNNING
						&& a3.getStatus() == ActivityStatus.RUNNING) {
					runningParallel = true;
				}

			}
		});

		a2.addStatusListener(new ActivityStatusListener() {

			@Override
			public void activityStatusChanged(Activity activity, ActivityStatus newStatus) {
				if (newStatus == ActivityStatus.RUNNING && a3.getStatus() == ActivityStatus.RUNNING
						&& a.getStatus() == ActivityStatus.RUNNING) {
					runningParallel = true;
				}

			}
		});

		a3.addStatusListener(new ActivityStatusListener() {

			@Override
			public void activityStatusChanged(Activity activity, ActivityStatus newStatus) {
				if (newStatus == ActivityStatus.RUNNING && a.getStatus() == ActivityStatus.RUNNING
						&& a2.getStatus() == ActivityStatus.RUNNING) {
					runningParallel = true;
				}

			}
		});

		a.beginExecute();
		a2.beginExecute();
		a3.beginExecute();

		a.endExecute();
		a2.endExecute();
		a3.endExecute();

		Assert.assertTrue(runningParallel);
	}

	@Test
	public void testExceptionInAsynchronousActivityIsThrownBySubsequentActivity() throws RoboticsException {
		TestDevice device = new TestDevice();
		device.setDriver(new TestDeviceDriver(getRuntime()));

		Command cmd = getRuntime().createWaitCommand(0.1);
		FromCommandActivity sleep = new FromCommandActivity(cmd, device);

		sleep.declareException(new CommandRealtimeException("testException"), cmd.getStartedState());

		sleep.beginExecute();

		FromCommandActivity sleep2 = new FromCommandActivity(getRuntime().createWaitCommand(0.1), device);

		try {
			sleep2.execute();
		} catch (RoboticsException e) {
			Throwable cause = e.getCause();
			if (!(cause instanceof CommandRealtimeException) || !cause.getMessage().equals("testException")) {
				Assert.fail("Exception thrown by preceding Activity should have been thrown");
			}
		}

	}

	@Test
	public void testExceptionInAsynchronousActivityIsNotThrownBySubsequentActivityWhenEndExecuteCalledBefore()
			throws RoboticsException {
		TestDevice device = new TestDevice();
		device.setDriver(new TestDeviceDriver(getRuntime()));

		Command cmd = getRuntime().createWaitCommand(0.1);
		FromCommandActivity sleep = new FromCommandActivity(cmd, device);

		sleep.declareException(new CommandRealtimeException("testException"), cmd.getStartedState());

		sleep.beginExecute();

		try {
			sleep.endExecute();
		} catch (CommandRealtimeException e) {
			// we expect this
		}

		FromCommandActivity sleep2 = new FromCommandActivity(getRuntime().createWaitCommand(0.1), device);

		sleep2.execute();

	}

	@Test
	public void testDeclaredExceptionIsThrownInActivity() throws RoboticsException {
		TestDevice device = new TestDevice();
		device.setDriver(new TestDeviceDriver(getRuntime()));

		Command cmd = getRuntime().createWaitCommand(0.1);
		FromCommandActivity sleep = new FromCommandActivity(cmd, device);

		sleep.declareException(new CommandRealtimeException("testException"), cmd.getStartedState());

		try {
			sleep.execute();
			Assert.fail("Exception should have been thrown");
		} catch (CommandRealtimeException e) {
			Assert.assertEquals("testException", e.getMessage());
		}

	}

	@Test
	public void testIgnoredExceptionTypeIsNotThrownInRtActivity() throws RoboticsException {
		TestDevice device = new TestDevice();
		device.setDriver(new TestDeviceDriver(getRuntime()));

		Command cmd = getRuntime().createWaitCommand(0.1);
		FromCommandActivity sleep = new FromCommandActivity(cmd, device);

		sleep.declareException(new CommandRealtimeException("testException"), cmd.getStartedState());

		sleep.ignoreException(CommandRealtimeException.class);

		try {
			sleep.execute();
		} catch (CommandRealtimeException e) {
			Assert.fail("Exception should have been thrown: " + e);
		}

	}

}