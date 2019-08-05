/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.state.ActuatorState;
import org.roboticsapi.platform.action.FollowFrame;
import org.roboticsapi.runtime.mockclass.MockAction;
import org.roboticsapi.runtime.mockclass.MockActuatorImpl;
import org.roboticsapi.runtime.mockclass.MockRuntimeCommand;
import org.roboticsapi.runtime.mockclass.TestActuator;
import org.roboticsapi.world.Frame;

public abstract class AbstractRuntimeCommandTest extends AbstractRuntimeTest {
	private RuntimeCommand mockRuntCmd = null;

	@Before
	public void setup() {
		RoboticsRuntime runtime = getRuntime();

		TestActuator mockActuator = new TestActuator("MockActuator", runtime);

		try {
			getContext().initialize(mockActuator);
		} catch (InitializationException e) {
			fail("Exception while initializing MockActuator. Test will be aborted.");
		}

		Frame refFrm = new Frame("ReferenceFrame");
		Frame frm = new Frame("FrameUp", refFrm, 0.1, 0.1, 0.1, 0, 0, 0);
		FollowFrame ff = new FollowFrame(frm);

		try {
			mockRuntCmd = runtime.createRuntimeCommand(mockActuator, ff);
		} catch (RoboticsException e) {
			fail("Exception while creating RuntimeCommand. Test will be aborted.");
		}

		assertNotNull(mockRuntCmd);
	}

	@After
	public void teardown() {
		mockRuntCmd = null;
	}

	@Test(timeout = 15000)
	public void testGetWatchdogTimeoutWithVariousTimeoutValues() {
		// both timeouts = 0
		try {
			mockRuntCmd.setWatchdogTimeout(0);
		} catch (RoboticsException e) {
			fail("Exception while setting watchdogTimeout. Test will be aborted.");
		}

		mockRuntCmd.getAction().setWatchdogTimeout(0);

		assertEquals(0, mockRuntCmd.getWatchdogTimeout(), 0.001);

		// command timeout = 0, action timeout > 0
		try {
			mockRuntCmd.setWatchdogTimeout(0);
		} catch (RoboticsException e) {
			fail("Exception while setting watchdogTimeout. Test will be aborted.");
		}

		mockRuntCmd.getAction().setWatchdogTimeout(10);

		assertEquals(10, mockRuntCmd.getWatchdogTimeout(), 0.001);

		// command timeout > 0, action timeout = 0
		try {
			mockRuntCmd.setWatchdogTimeout(10);
		} catch (RoboticsException e) {
			fail("Exception while setting watchdogTimeout. Test will be aborted.");
		}

		mockRuntCmd.getAction().setWatchdogTimeout(0);

		assertEquals(10, mockRuntCmd.getWatchdogTimeout(), 0.001);

		// both timeouts > 0, command timeout < action timeout
		try {
			mockRuntCmd.setWatchdogTimeout(10);
		} catch (RoboticsException e) {
			fail("Exception while setting watchdogTimeout. Test will be aborted.");
		}

		mockRuntCmd.getAction().setWatchdogTimeout(20);

		assertNotNull(mockRuntCmd.getWatchdogTimeout());

		assertEquals(10, mockRuntCmd.getWatchdogTimeout(), 0.001);

		// both timeouts > 0, command timeout > action timeout
		try {
			mockRuntCmd.setWatchdogTimeout(30);
		} catch (RoboticsException e) {
			fail("Exception while setting watchdogTimeout. Test will be aborted.");
		}

		mockRuntCmd.getAction().setWatchdogTimeout(20);

		assertEquals(20, mockRuntCmd.getWatchdogTimeout(), 0.001);
	}

	@Test(timeout = 15000)
	public void testOverrideWatchdogTimeout() {
		// both timeouts > 0, command timeout < action timeout
		try {
			mockRuntCmd.setWatchdogTimeout(10);
		} catch (RoboticsException e) {
			fail("Exception while setting watchdogTimeout. Test will be aborted.");
		}

		mockRuntCmd.getAction().setWatchdogTimeout(20);

		assertEquals(20, mockRuntCmd.getAction().getWatchdogTimeout(), 0.001);

		assertEquals(10, mockRuntCmd.getWatchdogTimeout(), 0.001);

		// override timeout > current command timeout
		try {
			mockRuntCmd.overrideWatchdogTimeout(15);
		} catch (RoboticsException e) {
			fail("Exception while overriding watchdogTimeout. Test failed.");
		}

		assertEquals(15, mockRuntCmd.getAction().getWatchdogTimeout(), 0.001);

		assertEquals(15, mockRuntCmd.getWatchdogTimeout(), 0.001);
	}

	@Test(timeout = 15000)
	public void testRelaxWatchdogTimeout() {
		// both timeouts > 0, command timeout < action timeout
		try {
			mockRuntCmd.setWatchdogTimeout(10);
		} catch (RoboticsException e) {
			fail("Exception while setting watchdogTimeout. Test will be aborted.");
		}

		mockRuntCmd.getAction().setWatchdogTimeout(20);

		assertEquals(20, mockRuntCmd.getAction().getWatchdogTimeout(), 0.001);

		assertEquals(10, mockRuntCmd.getWatchdogTimeout(), 0.001);

		// override timeout > current timeout
		try {
			mockRuntCmd.relaxWatchdogTimeout(30);
		} catch (RoboticsException e) {
			fail("Exception while relaxing watchdogTimeout. Test failed.");
		}

		assertEquals(30, mockRuntCmd.getAction().getWatchdogTimeout(), 0.001);

		assertEquals(30, mockRuntCmd.getWatchdogTimeout(), 0.001);
	}

	@Test(timeout = 15000)
	public void testStateValidatorWithActuatorState() throws RoboticsException {
		ActuatorState actState = new ActuatorState() {
		};

		assertTrue(mockRuntCmd.validateState(actState));

		actState.setDevice(mockRuntCmd.getDevice());

		assertNotNull(actState.getDevice());

		assertTrue(mockRuntCmd.validateState(actState));
	}

	@Test(timeout = 10000, expected = IllegalArgumentException.class)
	public void testConstructorWithNullActionExpectingIllegalArgumentException() throws RoboticsException {
		Actuator testActuator = new MockActuatorImpl();

		new MockRuntimeCommand(null, testActuator, null);
	}

	@Test(timeout = 10000, expected = IllegalArgumentException.class)
	public void testConstructorWithNullActuatorExpectingIllegalArgumentException() throws RoboticsException {
		Action testAction = new MockAction(0);

		new MockRuntimeCommand(testAction, null, null);
	}
}
