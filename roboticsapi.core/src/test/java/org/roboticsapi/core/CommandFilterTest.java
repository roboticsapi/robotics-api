/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.commandfilter.RuntimeCommandFilter;
import org.roboticsapi.core.commandfilter.SimpleWaitCommandFilter;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;

public class CommandFilterTest {

	private RoboticsContext context;
	private TestRuntime runtime;
	private boolean processed;
	private List<Command> processedCmds;

	private int transEventHandlerCount;
	private int outerTransEventHandlerCount;

	@Before
	public void init() throws InitializationException {
		context = new RoboticsContextImpl("test");
		runtime = new TestRuntime();
		runtime.setActuatorPresency(true);
		runtime.doGoOperational();
		context.initialize(runtime);

		processed = false;
		processedCmds = new ArrayList<Command>();

		transEventHandlerCount = 0;
		outerTransEventHandlerCount = 0;
	}

	@After
	public void teardown() {
		context.destroy();
	}

	@Test
	public void testRuntimeCommandFilterMatchesCorrectRuntimeCommands() {
		runtime.addCommandFilter(new RuntimeCommandFilter() {

			@Override
			protected boolean filterRuntimeCommand(RuntimeCommand command) {
				return command instanceof TestRuntimeCommand;
			}

			@Override
			protected void processRuntimeCommand(RuntimeCommand command) {
				processed = true;
				processedCmds.add(command);
			}
		});

		TestRuntimeCommand cmd = null;
		try {
			TestActuator actuator = new TestActuator();
			TestActuatorDriver driver = new TestActuatorDriver(actuator, runtime);
			TestAction action = new TestAction();
			cmd = new TestRuntimeCommand(action, driver);
			cmd.load();
		} catch (RoboticsException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception!");
		}

		Assert.assertTrue(processed);
		Assert.assertEquals(1, processedCmds.size());
		Assert.assertTrue(processedCmds.contains(cmd));
	}

	@Test
	public void testWaitCommandFilterMatchesWaitCommands() {
		runtime.addCommandFilter(new SimpleWaitCommandFilter() {

			@Override
			protected void processWaitCommand(WaitCommand command) {
				processed = true;
				processedCmds.add(command);

			}
		});

		Command cmd = null;
		try {
			cmd = runtime.createWaitCommand();
			cmd.load();
		} catch (RoboticsException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception!");
		}

		Assert.assertTrue(processed);
		Assert.assertEquals(1, processedCmds.size());
		Assert.assertTrue(processedCmds.contains(cmd));
	}

}
