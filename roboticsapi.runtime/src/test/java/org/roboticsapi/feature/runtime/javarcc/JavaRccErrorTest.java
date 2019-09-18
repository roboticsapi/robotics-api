/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.javarcc;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.UnhandledErrorsException;
import org.roboticsapi.core.WaitCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.facet.runtime.rcc.RccRuntime;
import org.roboticsapi.feature.startup.launcher.DefaultRapi;
import org.roboticsapi.feature.startup.launcher.Rapi;

public class JavaRccErrorTest {

	@Test
	public void testJavaRccCommandFailsWithReason() throws RoboticsException {
		Rapi rapi = DefaultRapi.createNewEmpty();
		JavaRcc rcc = new JavaRcc();
		RccRuntime runtime = new RccRuntime();
		runtime.setRcc(rcc);
		rapi.add(runtime);

		for (int i = 0; i < 10; i++) {
			try {
				WaitCommand command = runtime.createWaitCommand(2);
				command.addExceptionCondition(command.getCommandExecutionTime().greater(0.189),
						new CommandRealtimeException("Expected exception"));
				command.execute();

				Assert.fail("An exception should have been thrown");
			} catch (UnhandledErrorsException e) {
				Assert.assertFalse("An InnerException should be set", e.getInnerExceptions().isEmpty());
			}
		}
		rapi.destroy();
	}

	@Test
	public void testJavaRccCommandWithListenerFailsWithReason() throws RoboticsException {
		Rapi rapi = DefaultRapi.createNewEmpty();

		JavaRcc rcc = new JavaRcc();
		RccRuntime runtime = new RccRuntime();
		runtime.setRcc(rcc);
		rapi.add(runtime);

		for (int i = 0; i < 10; i++) {
			try {
				WaitCommand command = runtime.createWaitCommand(2);
				command.addObserver(command.getCommandExecutionTime(), t -> {
				});
				command.addExceptionCondition(command.getCommandExecutionTime().greater(0.189),
						new CommandRealtimeException("Expected exception"));
				command.execute();

				Assert.fail("An exception should have been thrown");
			} catch (UnhandledErrorsException e) {
				Assert.assertFalse("An InnerException should be set", e.getInnerExceptions().isEmpty());
			}
		}
		rapi.destroy();
	}

}
