/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import java.util.concurrent.Semaphore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.State;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.eventhandler.JavaExecutor;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;

public abstract class AbstractEventTest extends AbstractRuntimeTest {

	private int eventFireCount;
	private final Semaphore sem = new Semaphore(1);

	@Before
	public void init() {
		eventFireCount = 0;
	}

	@Test(timeout = 5000)
	public void testStateEnteredIsFiredEachTimeStateBecomesActiveInWaitCommand() throws InterruptedException {
		Command wait = getRuntime().createWaitCommand(1);
		BooleanFromJavaSensor sensor = new BooleanFromJavaSensor(false);

		sem.acquire();

		try {
			wait.addStateEnteredHandler(sensor.isTrue(), new JavaExecutor(new Runnable() {
				@Override
				public void run() {
					eventFireCount++;
					sem.release();
				}
			}));

			wait.addStateLeftHandler(sensor.isTrue(), new JavaExecutor(new Runnable() {
				@Override
				public void run() {
					sem.release();
				}
			}));

			CommandHandle handle = wait.start();

			sensor.setValue(true);
			sem.acquire();
			sensor.setValue(false);
			sem.acquire();
			sensor.setValue(true);
			sem.acquire();
			sensor.setValue(false);

			handle.waitComplete();

			Assert.assertEquals(2, eventFireCount);

		} catch (RoboticsException e) {
			e.printStackTrace();
			Assert.fail("Unexpected Exception!");
		}
	}

	@Test(timeout = 5000)
	public void testStateEnteredIsFiredEachTimeStateBecomesActiveInTransaction() throws RoboticsException {
		Command wait1 = getRuntime().createWaitCommand(0.3);
		Command wait2 = getRuntime().createWaitCommand(0.5);
		Command wait3 = getRuntime().createWaitCommand(0.5);

		TransactionCommand trans = getRuntime().createTransactionCommand(wait1, wait2, wait3);
		trans.addStartCommand(wait1);

		trans.addStateFirstEnteredHandler(wait1.getStartedState(), new CommandStarter(wait2));
		trans.addStateFirstEnteredHandler(wait2.getCompletedState(), new CommandStarter(wait3));

		try {
			State or = wait1.getActiveState().or(wait3.getActiveState());
			trans.addStateEnteredHandler(or, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					eventFireCount++;
				}
			}));

			trans.execute();

			Assert.assertEquals(2, eventFireCount);

		} catch (RoboticsException e) {
			e.printStackTrace();
			Assert.fail("Unexpected Exception!");

		}
	}

	@Test(timeout = 5000)
	public void testAndStateEnteredOnlyOnceBothSubstatesEntered() throws InterruptedException {
		Command wait1 = getRuntime().createWaitCommand(2);

		BooleanFromJavaSensor sensor1 = new BooleanFromJavaSensor(false);
		BooleanFromJavaSensor sensor2 = new BooleanFromJavaSensor(false);

		try {
			State and = sensor1.isTrue().and(sensor2.isTrue());
			wait1.addStateEnteredHandler(and, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					eventFireCount++;
				}
			}));

			CommandHandle handle = wait1.start();

			sensor1.setValue(true);
			Thread.sleep(200);
			sensor1.setValue(false);
			Thread.sleep(200);
			sensor2.setValue(true);
			Thread.sleep(200);
			sensor2.setValue(false);

			Thread.sleep(200);
			sensor1.setValue(true);
			Thread.sleep(200);
			sensor2.setValue(true);

			Thread.sleep(200);

			handle.cancel();
			handle.waitComplete();

			Assert.assertEquals(1, eventFireCount);

		} catch (RoboticsException e) {
			e.printStackTrace();
			Assert.fail("Unexpected Exception!");

		}
	}
}
