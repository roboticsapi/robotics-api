/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Actuator.ActuatorBusyListener;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.CommandStatusListener;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RoboticsRuntime.CommandHook;
import org.roboticsapi.core.actuator.AbstractActuator;
import org.roboticsapi.runtime.mockclass.MockActuatorDriverImpl;

public abstract class AbstractAbstractActuatorTest extends AbstractRuntimeTest {
	private class MockAbstractActuator<AD extends ActuatorDriver> extends AbstractActuator<AD> {
		@Override
		public void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
			// empty implementation
		}

		@Override
		protected void defineMaximumParameters() throws InvalidParametersException {
			// empty implementation
		}

		@Override
		protected void setupDriver(AD driver) {
			// empty implementation
		}
	}

	private class MockActuatorBusyListener implements ActuatorBusyListener {
		@Override
		public void busyStateChanged(boolean busy) {
			// Auto-generated method stub
		}
	}

	private ActuatorDriver mockActuatorDriver = null;
	private AbstractActuator<ActuatorDriver> mockActuator = null;

	@Before
	public void setup() {
		mockActuatorDriver = new MockActuatorDriverImpl(getRuntime());
		mockActuator = new MockAbstractActuator<ActuatorDriver>();
		mockActuator.setDriver(mockActuatorDriver);
	}

	@After
	public void teardown() {
		mockActuatorDriver = null;
		mockActuator = null;
	}

	@Test(timeout = 15000)
	public void testCommandHandleHookOfInnerClassBusyCheckHook() {
		CommandHandle testCmdHandle = new CommandHandle() {
			@Override
			public boolean start() throws CommandException {
				return false;
			}

			@Override
			public boolean scheduleAfter(CommandHandle executeAfter) throws CommandException {
				return false;
			}

			@Override
			public boolean abort() throws CommandException {
				return false;
			}

			@Override
			public boolean cancel() throws CommandException {
				return false;
			}

			@Override
			public void waitComplete() throws CommandException {
			}

			@Override
			public CommandStatus getStatus() throws CommandException {
				return null;
			}

			@Override
			public void addStatusListener(CommandStatusListener listener) throws CommandException {
				// to test the catch part of method commandHandleHook(...):
				throw new CommandException("Test CommandException");
			}

			@Override
			public void throwException(CommandException ex) {
			}

			@Override
			public CommandException getOccurredException() {
				return null;
			}

			@Override
			public void startThread(Runnable runnable) {
			}
		};

		ActuatorBusyListener testListener = new MockActuatorBusyListener();

		mockActuator.addBusyListener(testListener);

		List<CommandHook> cmdHooks = mockActuator.getDriver().getRuntime().getCommandHooks();

		CommandHook testCmdHook = null;

		final int CMD_HOOKS_SIZE = cmdHooks.size();
		if (CMD_HOOKS_SIZE == 0) {
			fail("There's no CommandHook that could be tested. Test will be aborted.");
		} else {
			testCmdHook = cmdHooks.get(CMD_HOOKS_SIZE - 1);
		}

		assertNotNull(testCmdHook);

		testCmdHook.commandHandleHook(testCmdHandle);
	}

	@Test(timeout = 15000)
	public void testRemoveBusyListenerSimpleTest() {
		ActuatorBusyListener testListener = new MockActuatorBusyListener();

		mockActuator.addBusyListener(testListener);

		mockActuator.removeBusyListener(testListener);
	}

	@Test(timeout = 15000)
	public void testCheckParameterBoundsSimpleTest() throws InvalidParametersException {
		DeviceParameters testParameters = new DeviceParameters() {
			@Override
			public boolean respectsBounds(DeviceParameters boundingObject) {
				return true;
			}
		};

		mockActuator.checkParameterBounds(testParameters, testParameters);
	}
}
