/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.EventHandler;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.WaitCommand;
import org.roboticsapi.core.commandfilter.RuntimeCommandFilter;
import org.roboticsapi.core.commandfilter.SimpleTransactionCommandFilter;
import org.roboticsapi.core.commandfilter.SimpleWaitCommandFilter;
import org.roboticsapi.core.commandfilter.TransactionCommandFilter;
import org.roboticsapi.core.commandfilter.WaitCommandFilter;
import org.roboticsapi.core.eventhandler.CommandStopper;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.mockclass.TestAction;
import org.roboticsapi.mockclass.TestActuator;
import org.roboticsapi.mockclass.TestRuntime;
import org.roboticsapi.mockclass.TestRuntimeCommand;

public class CommandFilterTest {

	private TestRuntime runtime;
	private boolean processed;
	private List<Command> processedCmds;

	private int transEventHandlerCount;
	private int outerTransEventHandlerCount;

	@Before
	public void init() {
		runtime = new TestRuntime();
		processed = false;
		processedCmds = new ArrayList<Command>();

		transEventHandlerCount = 0;
		outerTransEventHandlerCount = 0;
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
			cmd = new TestRuntimeCommand(new TestAction(), new TestActuator(runtime));

			runtime.load(cmd);
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception!");
			e.printStackTrace();
		}

		Assert.assertTrue(processed);
		Assert.assertEquals(1, processedCmds.size());
		Assert.assertTrue(processedCmds.contains(cmd));
	}

	@Test
	public void testRuntimeCommandFilterMatchesRuntimeCommandsInTransactionCommands() throws RoboticsException {
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

		TestRuntimeCommand cmd = new TestRuntimeCommand(new TestAction(), new TestActuator("Test", runtime));
		TransactionCommand trans = runtime.createTransactionCommand(cmd);
		trans.addStartCommand(cmd);

		try {
			runtime.load(trans);
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception!");
			e.printStackTrace();
		}

		Assert.assertTrue(processed);
		Assert.assertEquals(1, processedCmds.size());
		Assert.assertTrue(processedCmds.contains(cmd));
	}

	@Test
	public void testRuntimeCommandFilterDoesNotMatchIncorrectRuntimeCommands() throws RoboticsException {
		runtime.addCommandFilter(new RuntimeCommandFilter() {

			@Override
			protected boolean filterRuntimeCommand(RuntimeCommand command) {
				return command instanceof TestRuntimeCommand
						&& !(((TestRuntimeCommand) command).getDevice() instanceof TestActuator);
			}

			@Override
			protected void processRuntimeCommand(RuntimeCommand command) {
				processed = true;
				processedCmds.add(command);
			}
		});

		TestRuntimeCommand cmd = new TestRuntimeCommand(new TestAction(), new TestActuator("Test", runtime));
		TransactionCommand trans = runtime.createTransactionCommand(cmd);
		trans.addStartCommand(cmd);

		try {
			runtime.load(trans);
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception!");
			e.printStackTrace();
		}

		Assert.assertFalse(processed);
		Assert.assertEquals(0, processedCmds.size());
		Assert.assertFalse(processedCmds.contains(cmd));
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

		Command cmd = runtime.createWaitCommand();

		try {
			runtime.load(cmd);
		} catch (RoboticsException e) {
			e.printStackTrace();
			Assert.fail("Unexpected exception!");
		}

		Assert.assertTrue(processed);
		Assert.assertEquals(1, processedCmds.size());
		Assert.assertTrue(processedCmds.contains(cmd));
	}

	@Test
	public void testWaitCommandFilterMatchesWaitCommandsInTransactionCommands() throws RoboticsException {
		runtime.addCommandFilter(new SimpleWaitCommandFilter() {

			@Override
			protected void processWaitCommand(WaitCommand command) {
				processed = true;
				processedCmds.add(command);

			}
		});

		Command cmd = runtime.createWaitCommand();
		TransactionCommand trans = runtime.createTransactionCommand(cmd);
		trans.addStartCommand(cmd);

		try {
			runtime.load(trans);
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception!");
			e.printStackTrace();
		}

		Assert.assertTrue(processed);
		Assert.assertEquals(1, processedCmds.size());
		Assert.assertTrue(processedCmds.contains(cmd));

	}

	@Test
	public void testWaitCommandFilterDoesNotMatchIncorrectWaitCommands() throws RoboticsException {
		runtime.addCommandFilter(new WaitCommandFilter() {

			@Override
			protected boolean filterWaitCommand(WaitCommand command) {
				return command.getDuration() > 1.0d;
			}

			@Override
			protected void processWaitCommand(WaitCommand command) {
				processed = true;
				processedCmds.add(command);

			}

		});

		Command cmd = runtime.createWaitCommand(0.5d);
		Command cmd2 = runtime.createWaitCommand(1.5d);
		TransactionCommand trans = runtime.createTransactionCommand(cmd, cmd2);
		trans.addStartCommand(cmd);

		try {
			runtime.load(trans);
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception!");
			e.printStackTrace();
		}

		Assert.assertTrue(processed);
		Assert.assertEquals(1, processedCmds.size());
		Assert.assertTrue(processedCmds.contains(cmd2));
	}

	@Test
	public void testTransactionCommandFilterMatchesCorrectTransactionCommands() throws RoboticsException {
		final Command cmd1 = runtime.createWaitCommand();
		final Command cmd2 = runtime.createWaitCommand();
		TransactionCommand trans = runtime.createTransactionCommand(cmd1, cmd2);
		trans.addStartCommand(cmd1);

		runtime.addCommandFilter(new TransactionCommandFilter() {

			@Override
			protected boolean filterTransactionCommand(TransactionCommand command) {
				return command.getCommandsInTransaction().contains(cmd1)
						&& command.getCommandsInTransaction().contains(cmd2);
			}

			@Override
			protected void processTransactionCommand(TransactionCommand command) {
				processed = true;
				processedCmds.add(command);
			}
		});

		try {
			runtime.load(trans);
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception!");
			e.printStackTrace();
		}

		Assert.assertTrue(processed);
		Assert.assertEquals(1, processedCmds.size());
		Assert.assertTrue(processedCmds.contains(trans));
	}

	@Test
	public void testTransactionCommandFilterMatchesTransactionCommandsInTransactionCommands() throws RoboticsException {
		final Command cmd1 = runtime.createWaitCommand();
		final Command cmd2 = runtime.createWaitCommand();
		TransactionCommand trans = runtime.createTransactionCommand(cmd1, cmd2);
		trans.addStartCommand(cmd1);
		TransactionCommand outerTrans = runtime.createTransactionCommand(trans);
		outerTrans.addStartCommand(trans);

		runtime.addCommandFilter(new SimpleTransactionCommandFilter() {

			@Override
			protected void processTransactionCommand(TransactionCommand command) {
				processed = true;
				processedCmds.add(command);
			}
		});

		try {
			runtime.load(outerTrans);
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception!");
			e.printStackTrace();
		}

		Assert.assertTrue(processed);
		Assert.assertEquals(2, processedCmds.size());
		Assert.assertTrue(processedCmds.contains(trans));
		Assert.assertTrue(processedCmds.contains(outerTrans));
	}

	@Test
	public void testTransactionCommandFilterDoesNotMatchIncorrectTransactionCommands() throws RoboticsException {
		final Command cmd1 = runtime.createWaitCommand();
		final Command cmd2 = runtime.createWaitCommand();
		TransactionCommand trans = runtime.createTransactionCommand(cmd1);
		trans.addStartCommand(cmd1);
		TransactionCommand outerTrans = runtime.createTransactionCommand(trans);
		outerTrans.addStartCommand(trans);

		runtime.addCommandFilter(new TransactionCommandFilter() {

			@Override
			protected boolean filterTransactionCommand(TransactionCommand command) {
				return command.getCommandsInTransaction().contains(cmd1)
						&& command.getCommandsInTransaction().contains(cmd2);
			}

			@Override
			protected void processTransactionCommand(TransactionCommand command) {
				processed = true;
				processedCmds.add(command);
			}
		});

		try {
			runtime.load(outerTrans);
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception!");
			e.printStackTrace();
		}

		Assert.assertFalse(processed);
		Assert.assertEquals(0, processedCmds.size());
	}

	@Test
	public void testFilterChildrenInTransactionCommandFilterFiltersCorrectChildren() throws RoboticsException {
		final Command cmd1 = runtime.createWaitCommand();
		final Command cmd2 = runtime.createWaitCommand();
		final Command cmd3 = runtime.createWaitCommand();
		TransactionCommand trans = runtime.createTransactionCommand(cmd1, cmd2);
		trans.addStartCommand(cmd1);
		TransactionCommand outerTrans = runtime.createTransactionCommand(trans, cmd3);
		outerTrans.addStartCommand(trans);

		runtime.addCommandFilter(new SimpleTransactionCommandFilter() {

			@Override
			protected void processTransactionCommand(TransactionCommand command) {
				filterChildren(new SimpleWaitCommandFilter() {

					@Override
					protected void processWaitCommand(WaitCommand command) {
						processed = true;
						processedCmds.add(command);
					}
				});
			}
		});

		try {
			runtime.load(outerTrans);
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception!");
			e.printStackTrace();
		}

		Assert.assertTrue(processed);
		Assert.assertEquals(3, processedCmds.size());
		Assert.assertTrue(processedCmds.contains(cmd1));
		Assert.assertTrue(processedCmds.contains(cmd2));
		Assert.assertTrue(processedCmds.contains(cmd3));
	}

	@Test
	public void testFilterDescendantsInTransactionCommandFilterFiltersCorrectDescendants() throws RoboticsException {
		final Command cmd1 = runtime.createWaitCommand();
		final Command cmd2 = runtime.createWaitCommand();
		final Command cmd3 = runtime.createWaitCommand();
		TransactionCommand trans = runtime.createTransactionCommand(cmd1, cmd2);
		trans.addStartCommand(cmd1);
		TransactionCommand outerTrans = runtime.createTransactionCommand(trans, cmd3);
		outerTrans.addStartCommand(trans);

		runtime.addCommandFilter(new SimpleTransactionCommandFilter() {

			@Override
			protected void processTransactionCommand(TransactionCommand command) {
				filterDescendants(new SimpleWaitCommandFilter() {

					@Override
					protected void processWaitCommand(WaitCommand command) {
						processed = true;
						processedCmds.add(command);
					}
				});
			}
		});

		try {
			runtime.load(outerTrans);
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception!");
			e.printStackTrace();
		}

		Assert.assertTrue(processed);
		// cmd1 and cmd2 are processed twice, as they are descendants of
		// outerTrans as well as trans
		Assert.assertEquals(5, processedCmds.size());
		Assert.assertTrue(processedCmds.contains(cmd1));
		Assert.assertTrue(processedCmds.contains(cmd2));
		Assert.assertTrue(processedCmds.contains(cmd3));
	}

	@Test
	public void testFilterDescendantsInTransactionCommandCreatesCorrectContext() throws RoboticsException {
		final Command cmd1 = runtime.createWaitCommand();
		final Command cmd2 = runtime.createWaitCommand();
		final Command cmd3 = runtime.createWaitCommand();
		final TransactionCommand trans = runtime.createTransactionCommand(cmd1, cmd2);
		trans.addStartCommand(cmd1);
		final TransactionCommand outerTrans = runtime.createTransactionCommand(trans, cmd3);
		outerTrans.addStartCommand(trans);

		// just to find out how many event handlers the TransactionCommands have
		// before the
		// test filter below is applied
		runtime.addCommandFilter(new SimpleTransactionCommandFilter() {
			@Override
			protected void processTransactionCommand(TransactionCommand command) {
				if (command.equals(trans)) {
					transEventHandlerCount = command.getEventHandlers().size();
				} else if (command.equals(outerTrans)) {
					outerTransEventHandlerCount = command.getEventHandlers().size();
				}
			}
		});

		// the test filter
		runtime.addCommandFilter(new SimpleTransactionCommandFilter() {
			@Override
			protected void processTransactionCommand(final TransactionCommand trans) {
				filterDescendants(new SimpleWaitCommandFilter() {

					@Override
					protected void processWaitCommand(WaitCommand command) {
						try {
							trans.addStateEnteredHandler(command.getActiveState(), new CommandStopper(trans));
						} catch (RoboticsException e) {
							Assert.fail("Unexpected exception!");
							e.printStackTrace();
						}
					}
				});
			}
		});

		try {
			runtime.load(outerTrans);
		} catch (RoboticsException e) {
			Assert.fail("Unexpected exception!");
			e.printStackTrace();
		}

		boolean cmd1TransHandlerCreated = false;

		for (EventHandler h : trans.getEventHandlers()) {
			if (h.getEffect() instanceof CommandStopper && ((CommandStopper) h.getEffect()).getTarget().equals(trans)) {
				if (h.getState() instanceof Command.ActiveState
						&& ((Command.ActiveState) h.getState()).getCommand().equals(cmd1)) {
					cmd1TransHandlerCreated = true;
				}
			}
		}

		boolean cmd2TransHandlerCreated = false;

		for (EventHandler h : trans.getEventHandlers()) {
			if (h.getEffect() instanceof CommandStopper && ((CommandStopper) h.getEffect()).getTarget().equals(trans)) {
				if (h.getState() instanceof Command.ActiveState
						&& ((Command.ActiveState) h.getState()).getCommand().equals(cmd2)) {
					cmd2TransHandlerCreated = true;
				}
			}
		}

		boolean cmd1OuterTransHandlerCreated = false;

		for (EventHandler h : outerTrans.getEventHandlers()) {
			if (h.getEffect() instanceof CommandStopper
					&& ((CommandStopper) h.getEffect()).getTarget().equals(outerTrans)) {
				if (h.getState() instanceof Command.ActiveState
						&& ((Command.ActiveState) h.getState()).getCommand().equals(cmd1)) {
					cmd1OuterTransHandlerCreated = true;
				}
			}
		}

		boolean cmd2OuterTransHandlerCreated = false;

		for (EventHandler h : outerTrans.getEventHandlers()) {
			if (h.getEffect() instanceof CommandStopper
					&& ((CommandStopper) h.getEffect()).getTarget().equals(outerTrans)) {
				if (h.getState() instanceof Command.ActiveState
						&& ((Command.ActiveState) h.getState()).getCommand().equals(cmd2)) {
					cmd2OuterTransHandlerCreated = true;
				}
			}
		}

		boolean cmd3OuterTransHandlerCreated = false;

		for (EventHandler h : outerTrans.getEventHandlers()) {
			if (h.getEffect() instanceof CommandStopper
					&& ((CommandStopper) h.getEffect()).getTarget().equals(outerTrans)) {
				if (h.getState() instanceof Command.ActiveState
						&& ((Command.ActiveState) h.getState()).getCommand().equals(cmd3)) {
					cmd3OuterTransHandlerCreated = true;
				}
			}
		}

		// all expected handlers were created
		Assert.assertTrue(cmd1TransHandlerCreated);
		Assert.assertTrue(cmd2TransHandlerCreated);
		Assert.assertTrue(cmd1OuterTransHandlerCreated);
		Assert.assertTrue(cmd2OuterTransHandlerCreated);
		Assert.assertTrue(cmd3OuterTransHandlerCreated);

		// no additional handlers were created
		Assert.assertEquals(2, trans.getEventHandlers().size() - transEventHandlerCount);
		Assert.assertEquals(3, outerTrans.getEventHandlers().size() - outerTransEventHandlerCount);
	}

}
