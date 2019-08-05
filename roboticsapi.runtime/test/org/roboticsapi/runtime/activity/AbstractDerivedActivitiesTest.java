/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.activity;

import java.util.concurrent.Semaphore;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.activity.ActivityStartedListener;
import org.roboticsapi.activity.FromCommandActivity;
import org.roboticsapi.activity.ParallelRtActivity;
import org.roboticsapi.activity.RtActivities;
import org.roboticsapi.activity.SequentialRtActivity;
import org.roboticsapi.activity.SleepRtActivity;
import org.roboticsapi.activity.StateEnteredListener;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.runtime.generic.AbstractRuntimeTest;

public abstract class AbstractDerivedActivitiesTest extends AbstractRuntimeTest {

	private boolean wait1Cancelled = false;
	private boolean wait2Cancelled = false;
	private boolean wait3Cancelled = false;
	private int counter;

	@Test(timeout = 2000)
	public void testSequentialRtActivityCancelsSequenceWhenCancelled() throws RoboticsException {
		counter = 0;
		SleepRtActivity sleep1 = RtActivities.sleep(getRuntime(), 0.1);
		SleepRtActivity sleep2 = RtActivities.sleep(getRuntime(), 0.1);
		SleepRtActivity sleep3 = RtActivities.sleep(getRuntime(), 0.1);

		ActivityStartedListener incrementer = new ActivityStartedListener() {

			@Override
			public void onActivityStarted() {
				counter++;

			}
		};

		sleep1.addStatusListener(incrementer);
		sleep2.addStatusListener(incrementer);
		sleep3.addStatusListener(incrementer);

		SequentialRtActivity sequential = RtActivities.strictlySequential(sleep1, sleep2, sleep3);

		sequential.addCancelConditions(sleep2.getStartedState());

		sequential.execute();

		Assert.assertTrue("Third activity in sequence should not have been executed", counter < 3);
	}

	@Test(timeout = 2000)
	public void testParallelRtActivityCancelCancelsAllInnerCommands() throws RoboticsException, InterruptedException {
		Command wait1 = getRuntime().createWaitCommand();
		Command wait2 = getRuntime().createWaitCommand();
		Command wait3 = getRuntime().createWaitCommand();

		FromCommandActivity wait1A = new FromCommandActivity(wait1);
		FromCommandActivity wait2A = new FromCommandActivity(wait2);
		FromCommandActivity wait3A = new FromCommandActivity(wait3);

		final Semaphore sem = new Semaphore(3);

		sem.acquire(3);

		wait1A.addStateListener(wait1.getCancelState(), new StateEnteredListener() {

			@Override
			public void stateEntered() {
				wait1Cancelled = true;
				sem.release();
			}
		});

		wait2A.addStateListener(wait2.getCancelState(), new StateEnteredListener() {

			@Override
			public void stateEntered() {
				wait2Cancelled = true;
				sem.release();
			}
		});

		wait3A.addStateListener(wait3.getCancelState(), new StateEnteredListener() {

			@Override
			public void stateEntered() {
				wait3Cancelled = true;
				sem.release();
			}
		});

		ParallelRtActivity parallel = RtActivities.parallel(wait1A, wait2A, wait3A);

		parallel.beginExecute();

		parallel.cancelExecute();

		parallel.endExecute();

		// as StateListeners are executed asynchonously, we have to wait here
		sem.acquire(3);

		Assert.assertTrue(wait1Cancelled);
		Assert.assertTrue(wait2Cancelled);
		Assert.assertTrue(wait3Cancelled);

	}
}
