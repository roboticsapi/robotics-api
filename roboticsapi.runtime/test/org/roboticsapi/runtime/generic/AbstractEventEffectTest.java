/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.eventhandler.JavaExecutor;
import org.roboticsapi.core.exception.RoboticsException;

public abstract class AbstractEventEffectTest extends AbstractRuntimeTest {

	private boolean threadStarted;

	@Before
	public void init() {
		threadStarted = false;
	}

	@Test(timeout = 5000)
	public void testJavaThreadStarterStartsThread() {
		Command waitCommand = getRuntime().createWaitCommand(1);

		try {
			waitCommand.addStateFirstEnteredHandler(waitCommand.getStartedState(), new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					threadStarted = true;

				}

			}));

			waitCommand.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		Assert.assertTrue(threadStarted);

	}

	@Test(timeout = 10000)
	public void testCommandStarterStartsCommand() throws RoboticsException {
		Command first = getRuntime().createWaitCommand(1);

		Command second = getRuntime().createWaitCommand(1);

		TransactionCommand trans = getRuntime().createTransactionCommand(first, second);
		trans.addStartCommand(first);

		try {
			trans.addStateFirstEnteredHandler(first.getCompletedState(), new CommandStarter(second));

			trans.addStateFirstEnteredHandler(second.getStartedState(), new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					threadStarted = true;

				}
			}));

			trans.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		Assert.assertTrue(threadStarted);
	}

}
