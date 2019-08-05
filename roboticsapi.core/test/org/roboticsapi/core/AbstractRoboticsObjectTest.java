/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.RoboticsObject.InitializationListener;
import org.roboticsapi.core.exception.InitializationException;

public abstract class AbstractRoboticsObjectTest<T extends RoboticsObject> {

	private class TestInitializationListener implements InitializationListener {

		boolean onInitialized = false, onUninitialized = false;

		@Override
		public void onInitialized(RoboticsObject o) {
			onInitialized = true;
		}

		@Override
		public void onUninitialized(RoboticsObject o) {
			onUninitialized = true;
		}

	}

	private RoboticsContext context;
	protected TestInitializationListener listener;
	protected T roboticsObject;

	/**
	 * Creates a new (i.e. not yet initialized) and working {@link RoboticsObject}.
	 *
	 * @return a new {@link RoboticsObject}
	 */
	protected T createRoboticsObject() {
		return createRoboticsObject(true, false, true, false);
	}

	/**
	 * Creates a new (i.e. not yet initialized) {@link RoboticsObject}.
	 *
	 * @param init        if <code>false</code> the object will throw
	 *                    {@link InitializationException} during initialization
	 * @param initError   if <code>true</code> unexpected error occurs during
	 *                    initialization
	 * @param uninit      if <code>false</code> the object will throw
	 *                    {@link InitializationException} during uninitialization
	 * @param uninitError if <code>true</code> unexpected error occurs during
	 *                    uninitialization
	 *
	 * @return a new {@link RoboticsObject}
	 */
	protected abstract T createRoboticsObject(boolean init, boolean initError, boolean uninit, boolean uninitError);

	@Before
	public void setup() {
		this.context = new RoboticsContextImpl("test");
		this.roboticsObject = createRoboticsObject();
		this.listener = new TestInitializationListener();
	}

	@After
	public void teardown() {
		try {
			this.context.destroy();
		} catch (IllegalStateException e) {
		}
		this.context = null;
		this.roboticsObject = null;
		this.listener = null;
	}

	@Test
	public void testInitializingAndUninitializingRoboticsObject() throws InitializationException {
		Assert.assertFalse(roboticsObject.isInitialized());
		roboticsObject.addInitializationListener(listener);

		context.initialize(roboticsObject);
		Assert.assertTrue(roboticsObject.isInitialized());
		Assert.assertTrue(listener.onInitialized);

		context.uninitialize(roboticsObject);
		Assert.assertFalse(roboticsObject.isInitialized());
		Assert.assertTrue(listener.onUninitialized);
	}

	@Test
	public void testInitializingRoboticsObjectTwice() throws InitializationException {
		context.initialize(roboticsObject);
		Assert.assertTrue(roboticsObject.isInitialized());

		roboticsObject.addInitializationListener(listener);
		Assert.assertFalse(listener.onInitialized);

		try {
			context.initialize(roboticsObject);
			Assert.fail();
		} catch (InitializationException e) {
		}
		Assert.assertTrue(roboticsObject.isInitialized());
		Assert.assertFalse(listener.onInitialized);
	}

	@Test
	public void testUninitializingRoboticsObjectTwice() throws InitializationException {
		context.initialize(roboticsObject);

		Assert.assertTrue(roboticsObject.isInitialized());

		context.uninitialize(roboticsObject);

		Assert.assertFalse(roboticsObject.isInitialized());

		roboticsObject.addInitializationListener(listener);
		Assert.assertFalse(listener.onUninitialized);

		try {
			context.uninitialize(roboticsObject);
			Assert.fail();
		} catch (InitializationException e) {
		}
		Assert.assertFalse(roboticsObject.isInitialized());
		Assert.assertFalse(listener.onUninitialized);
	}

	@Test
	public void testValidationBeforeInitialization() {
		roboticsObject = createRoboticsObject(false, false, true, false);
		roboticsObject.addInitializationListener(listener);

		try {
			context.initialize(roboticsObject);
			Assert.fail();
		} catch (InitializationException e) {
		}
		Assert.assertFalse(roboticsObject.isInitialized());
		Assert.assertFalse(listener.onInitialized);
	}

	@Test
	public void testErrorDuringInitializationLeadsToRuntimeException() {
		roboticsObject = createRoboticsObject(true, true, true, false);
		roboticsObject.addInitializationListener(listener);

		try {
			context.initialize(roboticsObject);
			Assert.fail();
		} catch (RuntimeException e) {
			// should go here...
		} catch (InitializationException e) {
			Assert.fail();
		}
		Assert.assertFalse(roboticsObject.isInitialized());
		Assert.assertFalse(listener.onInitialized);
	}

	@Test
	public void testValidationBeforeUninitialization() {
		roboticsObject = createRoboticsObject(true, false, false, false);
		roboticsObject.addInitializationListener(listener);

		try {
			context.initialize(roboticsObject);

			Assert.assertTrue(roboticsObject.isInitialized());
			Assert.assertTrue(listener.onInitialized);
		} catch (InitializationException e) {
			Assert.fail();
		}

		try {
			context.uninitialize(roboticsObject);
			Assert.fail();
		} catch (InitializationException e) {
		}
		Assert.assertTrue(roboticsObject.isInitialized());
		Assert.assertFalse(listener.onUninitialized);
	}

	@Test
	public void testErrorDuringUninitializationLeadsToRuntimeException() {
		roboticsObject = createRoboticsObject(true, false, true, true);
		roboticsObject.addInitializationListener(listener);

		try {
			context.initialize(roboticsObject);

			Assert.assertTrue(roboticsObject.isInitialized());
			Assert.assertTrue(listener.onInitialized);
		} catch (InitializationException e) {
			Assert.fail();
		}

		try {
			context.uninitialize(roboticsObject);
			Assert.fail();
		} catch (RuntimeException e) {
			// should go here...
		} catch (InitializationException e) {
			Assert.fail();
		}
		Assert.assertFalse(roboticsObject.isInitialized());
		Assert.assertTrue(listener.onUninitialized);
	}

	@Test
	public void testInitializingUnnamedRoboticsObject() {
		Assert.assertNull(roboticsObject.getName());

		try {
			context.initialize(roboticsObject);
		} catch (InitializationException e) {
			Assert.fail();
		}
		Assert.assertNull(roboticsObject.getName());
	}

	@Test
	public void testSettingNameOfRoboticsObject() throws InitializationException {
		String name = "Test";

		roboticsObject.setName(name);
		context.initialize(roboticsObject);

		Assert.assertEquals(name, roboticsObject.getName());

		try {
			roboticsObject.setName("Another name");
			Assert.fail();
		} catch (IllegalStateException e) {
		}

	}

}
