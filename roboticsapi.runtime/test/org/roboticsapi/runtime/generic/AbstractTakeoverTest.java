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
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.eventhandler.JavaExecutor;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.state.TrueState;

public abstract class AbstractTakeoverTest extends AbstractRuntimeTest {

	private boolean takeoverReceived;

	@Test(timeout = 3000)
	public void testTakeoverAllowedStateEntered() throws RoboticsException {
		Command cmd1 = getRuntime().createWaitCommand(1);

		takeoverReceived = false;
		cmd1.addTakeoverAllowedCondition(new TrueState());
		cmd1.addStateEnteredHandler(cmd1.getTakeoverAllowedState(), new JavaExecutor(new Runnable() {

			@Override
			public void run() {
				takeoverReceived = true;
			}
		}));

		CommandHandle handle1 = cmd1.start();

		Command cmd2 = getRuntime().createWaitCommand(0.1);
		CommandHandle handle2 = cmd2.scheduleAfter(handle1);

		handle1.waitComplete();
		handle2.waitComplete();

		Assert.assertTrue(takeoverReceived);
	}

	@Test(timeout = 3000)
	public void testAbortOnTakeoverSucceeds() throws RoboticsException {
		Command cmd1 = getRuntime().createWaitCommand();

		cmd1.addTakeoverAllowedCondition(new TrueState());
		CommandHandle handle1 = cmd1.start();

		Command cmd2 = getRuntime().createWaitCommand(0.1);
		CommandHandle handle2 = cmd2.scheduleAfter(handle1);

		handle1.waitComplete();
		handle2.waitComplete();

	}

}
