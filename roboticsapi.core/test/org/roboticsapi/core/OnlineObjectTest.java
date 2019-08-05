/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.mockclass.TestActuator;
import org.roboticsapi.mockclass.TestRuntime;

public class OnlineObjectTest {

	@Test
	public void testRoboticsObjectIsNewBeforeInitialization() {
		TestActuator act = new TestActuator(null);

		Assert.assertEquals(OperationState.NEW, act.getState());
	}

	@Test
	public void testRoboticsObjectIsOfflineAfterInitialization() throws InitializationException {
		RoboticsContext context = new RoboticsContextImpl("test");
		TestRuntime runtime = new TestRuntime();
		runtime.setName("TestRuntime");
		context.initialize(runtime);
		runtime.setActuatorPresency(true);

		Assert.assertEquals(OperationState.OFFLINE, runtime.getState());
	}

	@Test
	public void testRoboticsObjectIsSafeoperationalAfterGoSafeoperational() throws InitializationException {
		RoboticsContext context = new RoboticsContextImpl("test");
		TestRuntime runtime = new TestRuntime();
		runtime.setName("TestRuntime");
		context.initialize(runtime);
		runtime.setActuatorPresency(true);

		runtime.doGoSafeoperational();

		Assert.assertEquals(OperationState.SAFEOPERATIONAL, runtime.getState());
	}

	@Test
	public void testRoboticsObjectIsOperationalAfterGoOperational() throws InitializationException {
		RoboticsContext context = new RoboticsContextImpl("test");
		TestRuntime runtime = new TestRuntime();
		runtime.setName("TestRuntime");
		context.initialize(runtime);
		runtime.setActuatorPresency(true);

		runtime.doGoOperational();

		Assert.assertEquals(OperationState.OPERATIONAL, runtime.getState());
	}

	@Test
	public void testRoboticsObjectOfflineSafeoperationalOperationalCycle() throws InitializationException {
		RoboticsContext context = new RoboticsContextImpl("test");
		TestRuntime runtime = new TestRuntime();
		runtime.setName("TestRuntime");
		context.initialize(runtime);
		runtime.setActuatorPresency(true);

		Assert.assertEquals(OperationState.OFFLINE, runtime.getState());

		runtime.doGoSafeoperational();

		Assert.assertEquals(OperationState.SAFEOPERATIONAL, runtime.getState());

		runtime.doGoOperational();

		Assert.assertEquals(OperationState.OPERATIONAL, runtime.getState());

		runtime.doGoSafeoperational();

		Assert.assertEquals(OperationState.SAFEOPERATIONAL, runtime.getState());

		runtime.doGoOffline();

		Assert.assertEquals(OperationState.OFFLINE, runtime.getState());
	}

}
