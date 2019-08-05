/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandRuntimeException;
import org.roboticsapi.core.State;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.eventhandler.ExceptionIgnorer;
import org.roboticsapi.core.eventhandler.ExceptionThrower;
import org.roboticsapi.core.eventhandler.JavaExceptionThrower;
import org.roboticsapi.core.eventhandler.JavaExecutor;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.state.AndState;
import org.roboticsapi.core.state.ExplicitState;
import org.roboticsapi.core.state.FalseState;
import org.roboticsapi.core.state.OrState;
import org.roboticsapi.core.state.TrueState;
import org.roboticsapi.runtime.mockclass.TestActuator;
import org.roboticsapi.runtime.mockclass.TestActuatorException;
import org.roboticsapi.runtime.mockclass.TestCommandRealtimeException;
import org.roboticsapi.runtime.mockclass.TestWaitCommand;

public abstract class AbstractStateTest extends AbstractRuntimeTest {
	private boolean threadStarted;
	private boolean stateEntered;
	private boolean stateLeft;

	@Before
	public void initSingleTest() {
		threadStarted = stateEntered = stateLeft = false;
	}

	@Test(timeout = 2500)
	public void testWaitCommandStartedIsFired() {

		Command waitCommand = getRuntime().createWaitCommand(0.5);
		try {
			waitCommand.addStateFirstEnteredHandler(waitCommand.getStartedState(), new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					threadStarted = true;

				}

			}, false));

			waitCommand.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		Assert.assertTrue(threadStarted);

	}

	@Test(timeout = 2000)
	public void testWaitErrorRaiser() throws RoboticsException {
		Command waitCommand = getRuntime().createWaitCommand(0.5);
		waitCommand.addStateFirstEnteredHandler(waitCommand.getStartedState(), new ExceptionThrower(
				new TestActuator(getRuntime()).getDriver().defineActuatorDriverException(TestActuatorException.class)));
		try {
			waitCommand.start().waitComplete();
			Assert.fail("An error should have occured");
		} catch (CommandException e) {
		}
	}

	@Test(timeout = 3000)
	public void testWaitHandledErrorRaiser() throws RoboticsException {
		Command waitCommand = new TestWaitCommand(getRuntime(), 0.5);
		waitCommand.addStateFirstEnteredHandler(waitCommand.getStartedState(),
				new ExceptionThrower(new TestCommandRealtimeException("Test")));
		waitCommand.addExceptionHandler(TestCommandRealtimeException.class, new ExceptionIgnorer(), true);

		waitCommand.start().waitComplete();
	}

	@Test(timeout = 3000)
	public void testWaitSuperclassHandledErrorRaiser() throws RoboticsException {
		Command waitCommand = new TestWaitCommand(getRuntime(), 0.5);
		waitCommand.addStateFirstEnteredHandler(waitCommand.getStartedState(),
				new ExceptionThrower(new TestCommandRealtimeException("Test")));
		waitCommand.addExceptionHandler(CommandRealtimeException.class, new ExceptionIgnorer(), true);
		waitCommand.start().waitComplete();
	}

	@Test(timeout = 3000)
	public void testWaitHandledAndSuperclassHandledErrorRaiser() throws RoboticsException {
		Command waitCommand = new TestWaitCommand(getRuntime(), 0.5);

		waitCommand.addStateFirstEnteredHandler(waitCommand.getStartedState(),
				new ExceptionThrower(new TestCommandRealtimeException("Test")));
		waitCommand.addExceptionHandler(TestCommandRealtimeException.class, new ExceptionIgnorer(), true);
		waitCommand.addExceptionHandler(CommandRealtimeException.class,
				new JavaExceptionThrower(new CommandRuntimeException("This exception should not be thrown.")));
		waitCommand.start().waitComplete();
	}

	@Test(timeout = 3000)
	public void testWaitCommandActiveIsActivated() {
		Command waitCommand = getRuntime().createWaitCommand(0.5);
		try {
			waitCommand.addStateFirstEnteredHandler(waitCommand.getActiveState(), new JavaExecutor(new Runnable() {

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

	@Test(timeout = 2000)
	public void testWaitCommandCancelIsActivated() {
		Command waitCommand = getRuntime().createWaitCommand(0.5);
		try {
			waitCommand.addStateFirstEnteredHandler(waitCommand.getCancelState(), new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					threadStarted = true;

				}

			}, false));

			CommandHandle handle = waitCommand.start();
			handle.cancel();
			handle.waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		Assert.assertTrue(threadStarted);
	}

	@Test(timeout = 3000)
	public void testWaitCommandCompletedIsActivated() throws RoboticsException {
		Command waitCommand = getRuntime().createWaitCommand(0.5);
		TransactionCommand trans = getRuntime().createTransactionCommand(waitCommand);
		trans.addStartCommand(waitCommand);

		try {
			trans.addStateFirstEnteredHandler(waitCommand.getCompletedState(), new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					threadStarted = true;

				}

			}));

			trans.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Assert.assertTrue(threadStarted);

	}

	@Test(timeout = 5000)
	public void testStartedStateOfCommandInTransactionActivated() throws RoboticsException {
		Command first = getRuntime().createWaitCommand(0.5);

		Command second = getRuntime().createWaitCommand(0.5);

		TransactionCommand trans = getRuntime().createTransactionCommand(first, second);
		trans.addStartCommand(first);

		try {
			trans.addStateFirstEnteredHandler(first.getCompletedState(), new CommandStarter(second));

			second.addStateFirstEnteredHandler(second.getStartedState(), new JavaExecutor(new Runnable() {

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

	@Test(timeout = 3000)
	public void testTrueStateInTransactionActivatedAndNotLeft() throws InterruptedException, RoboticsException {
		Random random = new Random();
		Command first = getRuntime().createWaitCommand(random.nextDouble());
		Command second = getRuntime().createWaitCommand(random.nextDouble());

		TransactionCommand trans = getRuntime().createTransactionCommand(first, second);
		trans.addStartCommand(first);

		try {
			trans.addStateFirstEnteredHandler(first.getStartedState(), new CommandStarter(second));

			State state = new TrueState();

			trans.addStateFirstEnteredHandler(state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					stateEntered = true;

				}
			}, false));

			trans.addStateLeftHandler(state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					System.out.println("Left");
					stateLeft = true;

				}
			}, false));

			trans.start().waitComplete();

		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		// try {
		// Thread.sleep(50);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		Assert.assertTrue(stateEntered);
		Assert.assertFalse(stateLeft);
	}

	@Test(timeout = 3000)
	public void testFalseStateInTransactionNotActivatedAndNotLeft() throws RoboticsException {
		Random random = new Random();
		Command first = getRuntime().createWaitCommand(random.nextDouble());
		Command second = getRuntime().createWaitCommand(random.nextDouble());

		TransactionCommand trans = getRuntime().createTransactionCommand(first, second);
		trans.addStartCommand(first);

		try {
			trans.addStateFirstEnteredHandler(first.getStartedState(), new CommandStarter(second));

			State state = new FalseState();

			trans.addStateFirstEnteredHandler(state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					stateEntered = true;

				}
			}));

			trans.addStateFirstLeftHandler(state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					stateLeft = true;

				}
			}));

			trans.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		// try {
		// Thread.sleep(50);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		Assert.assertFalse(stateEntered);
		Assert.assertFalse(stateLeft);
	}

	@Test(timeout = 3000)
	public void testAndStateInTransactionActivated() throws RoboticsException {
		Random random = new Random();
		Command first = getRuntime().createWaitCommand(random.nextDouble());
		Command second = getRuntime().createWaitCommand(random.nextDouble());

		TransactionCommand trans = getRuntime().createTransactionCommand(first, second);
		trans.addStartCommand(first);

		try {
			trans.addStateFirstEnteredHandler(first.getStartedState(), new CommandStarter(second));

			AndState andEvent = new AndState(first.getCompletedState(), second.getCompletedState());

			trans.addStateFirstEnteredHandler(andEvent, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					threadStarted = true;

				}
			}, false));

			trans.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		// try {
		// Thread.sleep(50);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		Assert.assertTrue(threadStarted);
	}

	@Test(timeout = 3000)
	public void testOrStateInTransactionActivated() throws RoboticsException {
		Random random = new Random();
		Command first = getRuntime().createWaitCommand(random.nextDouble());
		Command second = getRuntime().createWaitCommand(random.nextDouble());

		TransactionCommand trans = getRuntime().createTransactionCommand(first, second);
		trans.addStartCommand(first);

		try {
			trans.addStateFirstEnteredHandler(first.getStartedState(), new CommandStarter(second));

			OrState orEvent = new OrState(first.getCompletedState(), second.getCompletedState());

			trans.addStateFirstEnteredHandler(orEvent, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					threadStarted = true;

				}
			}, false));

			trans.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		Assert.assertTrue(threadStarted);
	}

	@Test(timeout = 5000)
	public void testExplicitStateInTransactionActivatedAndDeactivated() throws RoboticsException {
		Random random = new Random();
		Command first = getRuntime().createWaitCommand(random.nextDouble());
		Command second = getRuntime().createWaitCommand(random.nextDouble());

		TransactionCommand trans = getRuntime().createTransactionCommand(first, second);
		trans.addStartCommand(first);

		try {
			trans.addStateFirstEnteredHandler(first.getCompletedState(), new CommandStarter(second));

			ExplicitState state = new ExplicitState(first.getStartedState(), second.getStartedState());

			trans.addStateEnteredHandler(state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					stateEntered = true;

				}
			}, false));

			trans.addStateLeftHandler(state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					stateLeft = true;

				}
			}, false));

			trans.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		Assert.assertTrue(stateEntered);
		Assert.assertTrue(stateLeft);
	}

	@Test(timeout = 3000)
	public void testHasOccurredStateInTransactionActivated() throws RoboticsException {
		Random random = new Random();
		Command first = getRuntime().createWaitCommand(random.nextDouble());
		Command second = getRuntime().createWaitCommand(random.nextDouble());

		TransactionCommand trans = getRuntime().createTransactionCommand(first, second);
		trans.addStartCommand(first);

		try {
			trans.addStateFirstEnteredHandler(first.getStartedState(), new CommandStarter(second));

			State state = second.getActiveState().hasBeenActive();

			trans.addStateEnteredHandler(state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					stateEntered = true;

				}
			}, false));

			trans.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		Assert.assertTrue(stateEntered);
	}

	@Test(timeout = 5000)
	public void testHasOccurredStateInTransactionNotDeactivated() throws RoboticsException {
		Random random = new Random();
		Command first = getRuntime().createWaitCommand(random.nextDouble());
		Command second = getRuntime().createWaitCommand(random.nextDouble());

		TransactionCommand trans = getRuntime().createTransactionCommand(first, second);
		trans.addStartCommand(first);

		try {
			trans.addStateFirstEnteredHandler(first.getCompletedState(), new CommandStarter(second));

			State state = first.getActiveState().hasBeenActive();

			trans.addStateLeftHandler(state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					stateLeft = true;

				}
			}, false));

			trans.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		Assert.assertFalse(stateLeft);
	}

	@Test(timeout = 5000)
	public void testNotStateInTransactionActivatedAndDeactivated() throws RoboticsException {
		Random random = new Random();
		Command first = getRuntime().createWaitCommand(random.nextDouble());
		Command second = getRuntime().createWaitCommand(random.nextDouble());

		TransactionCommand trans = getRuntime().createTransactionCommand(first, second);
		trans.addStartCommand(first);

		try {
			trans.addStateFirstEnteredHandler(first.getCompletedState(), new CommandStarter(second));

			State state = second.getActiveState().not();

			trans.addStateEnteredHandler(state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					stateEntered = true;

				}
			}, false));

			trans.addStateLeftHandler(state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					stateLeft = true;

				}
			}, false));

			trans.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		Assert.assertTrue(stateEntered);
		Assert.assertTrue(stateLeft);
	}

	@Test(timeout = 2000)
	public void testLongStateInWaitCommandActivatedIfTimeConditionMet() {
		Random random = new Random();
		double duration = random.nextDouble();
		Command first = getRuntime().createWaitCommand(duration);

		try {
			State state = first.getActiveState().forSeconds(duration / 2d);

			first.addStateEnteredHandler(state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					stateEntered = true;

				}
			}, false));

			first.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		Assert.assertTrue(stateEntered);
	}

	@Test(timeout = 7000)
	public void testLongStateInTransactionActivatedIfTimeConditionMet() throws RoboticsException {
		Random random = new Random();

		Command first = getRuntime().createWaitCommand(random.nextDouble());
		double secondDuration = random.nextDouble() + 0.01; // will not work if
															// wait time too
															// short
		Command second = getRuntime().createWaitCommand(secondDuration);

		TransactionCommand trans = getRuntime().createTransactionCommand(first, second);
		trans.addStartCommand(first);

		try {
			trans.addStateFirstEnteredHandler(first.getCompletedState(), new CommandStarter(second));

			State state = second.getActiveState().forSeconds(secondDuration / 2d);

			trans.addStateEnteredHandler(state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					stateEntered = true;

				}
			}, false));

			trans.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		Assert.assertTrue(stateEntered);
	}

	@Test(timeout = 7000)
	public void testLongStateInTransactionNotActivatedIfTimeConditionNotMet() throws RoboticsException {
		Random random = new Random();
		Command first = getRuntime().createWaitCommand(random.nextDouble());
		double secondDuration = random.nextDouble();
		Command second = getRuntime().createWaitCommand(secondDuration);

		TransactionCommand trans = getRuntime().createTransactionCommand(first, second);
		trans.addStartCommand(first);

		try {
			trans.addStateFirstEnteredHandler(first.getCompletedState(), new CommandStarter(second));

			State state = second.getActiveState().forSeconds(secondDuration * 1.1d);

			trans.addStateEnteredHandler(state, new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					stateEntered = true;

				}
			}));

			trans.start().waitComplete();
		} catch (RoboticsException e) {
			Assert.fail("Unexpected Exception: " + e.getMessage());
		}

		Assert.assertFalse(stateEntered);
	}

	private boolean xorStateEntered = false;
	private Command first = null;
	private Command second = null;

	private void setXorStateEnteredFalse() {
		xorStateEntered = false;
	}

	private void setupWaitCommands() {
		first = getRuntime().createWaitCommand(1); // duration = 1
		second = getRuntime().createWaitCommand(2); // duration = 2
	}

	private void teardownWaitCommands() {
		first = null;
		second = null;
	}

	private boolean determineXorStateEntered(State state) throws RoboticsException {
		setXorStateEnteredFalse();

		TransactionCommand trans = getRuntime().createTransactionCommand(first, second);

		// start first when trans starts:
		trans.addStartCommand(first);

		// start second when first has started:
		trans.addStateFirstEnteredHandler(first.getStartedState(), new CommandStarter(second));

		trans.addStateFirstEnteredHandler(state, new JavaExecutor(new Runnable() {
			@Override
			public void run() {
				xorStateEntered = true;
			}
		}, false));

		// start trans and complete:
		trans.start().waitComplete();

		boolean ret = xorStateEntered;

		setXorStateEnteredFalse();

		return ret;
	}

	@Test(timeout = 5000)
	public void testXorIsNotActivatedWhenActivatedXorActivated() throws RoboticsException {
		setupWaitCommands();

		// true <= NOT (true XOR true) <= NOT (first started is true XOR
		// second started is true) is true:
		State trueXorTrue = (first.getStartedState().xor(second.getStartedState())).not();

		assertTrue(determineXorStateEntered(trueXorTrue));

		teardownWaitCommands();
	}

	@Test(timeout = 5000)
	public void testXorIsActivatedWhenActivatedXorNotActivated() throws RoboticsException {
		setupWaitCommands();

		// true <= true XOR false <= first started is true XOR second
		// completed is false:
		State trueXorFalse = first.getStartedState().xor(second.getCompletedState());

		assertTrue(determineXorStateEntered(trueXorFalse));

		teardownWaitCommands();
	}

	@Test(timeout = 5000)
	public void testXorIsActivatedWhenNotActivatedXorActivated() throws RoboticsException {
		setupWaitCommands();

		// true <= false XOR true <= first completed is false XOR second
		// started is true:
		State falseXorTrue = first.getCompletedState().xor(second.getStartedState());

		assertTrue(determineXorStateEntered(falseXorTrue));

		teardownWaitCommands();
	}

	@Test(timeout = 5000)
	public void testXorIsNotActivatedWhenNotActivatedXorNotActivated() throws RoboticsException {
		setupWaitCommands();

		// true <= NOT (false XOR false) <= NOT (first completed is false XOR
		// second completed is false):
		State falseXorFalse = (first.getCompletedState().xor(second.getCompletedState())).not();

		assertTrue(determineXorStateEntered(falseXorFalse));

		teardownWaitCommands();
	}

	private boolean determineXorStateEntered2(State state) throws RoboticsException {
		setXorStateEnteredFalse();

		Command cmd = getRuntime().createWaitCommand(1); // duration = 1

		cmd.addStateFirstEnteredHandler(state, new JavaExecutor(new Runnable() {
			@Override
			public void run() {
				xorStateEntered = true;
			}
		}, false));

		cmd.start().waitComplete();

		boolean ret = xorStateEntered;

		setXorStateEnteredFalse();

		return ret;
	}

	@Test(timeout = 3000)
	public void testXorIsNotActivatedWhenTrueStateXorTrueState() throws RoboticsException {
		State trueXorTrue = (State.True().xor(State.True())).not();

		assertTrue(determineXorStateEntered2(trueXorTrue));
	}

	@Test(timeout = 3000)
	public void testXorIsActivatedWhenTrueStateXorFalseState() throws RoboticsException {
		State trueXorFalse = State.True().xor(State.False());

		assertTrue(determineXorStateEntered2(trueXorFalse));
	}

	@Test(timeout = 3000)
	public void testXorIsActivatedWhenFalseStateXorTrueState() throws RoboticsException {
		State falseXorTrue = State.False().xor(State.True());

		assertTrue(determineXorStateEntered2(falseXorTrue));
	}

	@Test(timeout = 3000)
	public void testXorIsNotActivatedWhenFalseStateXorFalseState() throws RoboticsException {
		State falseXorFalse = (State.False().xor(State.False())).not();

		assertTrue(determineXorStateEntered2(falseXorFalse));
	}

	private boolean andStateStarted = false;

	@Test(timeout = 5000)
	public void testOverrideMethodNamedAndOfSubclassNamedAndStateIsActivatedWhenSubstatesAreActive()
			throws RoboticsException {
		AndState testAndState = new AndState(State.True());
		Command testCmd = getRuntime().createWaitCommand(1); // duration = 1

		testCmd.addStateFirstEnteredHandler(testAndState.and(testCmd.getCompletedState()),
				new JavaExecutor(new Runnable() {
					@Override
					public void run() {
						andStateStarted = true;
					}
				}, false));

		testCmd.start().waitComplete();

		assertTrue(andStateStarted);
	}

	private boolean orStateStarted = false;

	@Test(timeout = 5000)
	public void testOverrideMethodNamedOrOfSubclassNamedOrStateIsActivatedWhenOneSubstateIsActive()
			throws RoboticsException {
		OrState testOrState = new OrState(State.False());
		Command testCmd = getRuntime().createWaitCommand(1); // duration = 1

		testCmd.addStateFirstEnteredHandler(testOrState.or(testCmd.getCompletedState()),
				new JavaExecutor(new Runnable() {
					@Override
					public void run() {
						orStateStarted = true;
					}
				}, false));

		testCmd.start().waitComplete();

		assertTrue(orStateStarted);
	}
}
