/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandRuntimeException;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.eventhandler.CommandStopper;
import org.roboticsapi.core.eventhandler.JavaExceptionThrower;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;

public abstract class AbstractSensorFromJavaTest extends AbstractRuntimeTest {

	@Test(timeout = 15000)
	public void testBooleanFromJava() throws RoboticsException, InterruptedException {
		long start = System.currentTimeMillis();

		Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		wait.addStateFirstEnteredHandler(bool.isTrue(), new CommandStopper());
		CommandHandle handle = wait.start();
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		Thread.sleep(100);
		Assert.assertEquals(CommandStatus.RUNNING, handle.getStatus());
		bool.setValue(true);
		handle.waitComplete();
		Assert.assertTrue("Boolean sensor did not cancel command.", System.currentTimeMillis() - start < 10000);
	}

	@Test(timeout = 15000)
	public void testBooleanFromJavaInitialTrue() throws RoboticsException, InterruptedException {
		long start = System.currentTimeMillis();
		final Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(true);
		wait.addObserver(bool, new SensorListener<Boolean>() {
			@Override
			public void onValueChanged(Boolean newValue) {
				try {
					Assert.assertTrue(newValue);
					wait.getCommandHandle().cancel();
				} catch (CommandException e) {
				}
			}
		});
		wait.execute();
		Assert.assertTrue("Boolean sensor did not fire with initial value.",
				System.currentTimeMillis() - start < 10000);
	}

	@Test(timeout = 15000)
	public void testBooleanFromJavaInitialFalse() throws RoboticsException, InterruptedException {
		long start = System.currentTimeMillis();
		final Command wait = getRuntime().createWaitCommand(10);
		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);
		wait.addObserver(bool, new SensorListener<Boolean>() {
			@Override
			public void onValueChanged(Boolean newValue) {
				try {
					Assert.assertFalse(newValue);
					wait.getCommandHandle().cancel();
				} catch (CommandException e) {
				}
			}
		});
		wait.execute();
		Assert.assertTrue("Boolean sensor did not fire with initial value.",
				System.currentTimeMillis() - start < 10000);
	}

	// @Test(timeout = 15000)
	// enable this test as soon as BooleanSensors support it
	public void testTwoCommandsReactSynchronouslyToSameJavaSensor() throws RoboticsException {
		Command wait1 = getRuntime().createWaitCommand(10);
		Command wait2 = getRuntime().createWaitCommand(10);
		Command wait3 = getRuntime().createWaitCommand(1);

		TransactionCommand trans = getRuntime().createTransactionCommand();

		trans.addStartCommand(wait1);
		trans.addStartCommand(wait2);

		trans.addCommand(wait3);

		trans.addStateFirstEnteredHandler(wait1.getCompletedState(), new CommandStarter(wait3));

		BooleanFromJavaSensor bool = new BooleanFromJavaSensor(false);

		wait1.addStateFirstEnteredHandler(bool.isTrue(), new CommandCanceller());
		wait2.addStateFirstEnteredHandler(bool.isTrue(), new CommandCanceller());

		trans.addStateEnteredHandler(wait1.getCancelState().xor(wait2.getCancelState()),
				new JavaExceptionThrower(new CommandRuntimeException("CancelSync")));

		try {
			CommandHandle t = trans.start();

			bool.setValue(true);

			t.waitComplete();

		} catch (CommandException e) {
			if (e.getMessage().equals("CancelSync")) {
				Assert.fail();
			}
		}

	}
}
