/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.PersistContext.PersistedSensorFactory;
import org.roboticsapi.mockclass.MockCommand;
import org.roboticsapi.mockclass.MockCommandHandleImpl;
import org.roboticsapi.mockclass.MockRoboticsRuntime;
import org.roboticsapi.mockclass.MockSensor;
import org.roboticsapi.core.Sensor;

public class PersistContextTest {
	private PersistContext<Double> pci = null;
	private Command cmd = null;

	@Before
	public void setup() {
		cmd = new MockCommand("MockingCommand");
		pci = new PersistContext<Double>(cmd);
	}

	@After
	public void teardown() {
		cmd = null;
		pci = null;
	}

	@Test
	public void testConstructorWithDoubleParameterAndMockingCommand() {
		PersistContext<Double> p = new PersistContext<Double>(cmd);

		assertNotNull(p);

		assertEquals(cmd, p.getCommand());
	}

	@Test
	public void testConstructorWithDoubleParameterAndNullCommand() {
		PersistContext<Double> p = new PersistContext<Double>(null);

		assertNotNull(p);

		assertNull(p.getCommand());
	}

	@Test
	public void testGetCommandWithPci() {
		assertNotNull(pci.getCommand());

		assertEquals(cmd, pci.getCommand());
	}

	@Test(expected = IllegalStateException.class)
	public void testGetSensorWithPciAndNullFactoryExpectingException() {
		pci.setFactory(null);

		pci.getSensor();
	}

	@Test
	public void testGetSensorWithPciAndMockingDoubleSensorAndMockingFactory() {
		PersistedSensorFactory<Double> factory = new PersistedSensorFactory<Double>() {
			@Override
			public Sensor<Double> createSensor(Command command) {
				return new MockSensor<Double>(null);
			}
		};

		assertNotNull(factory);

		pci.setFactory(factory);

		assertNotNull(pci.getSensor());

		// getRuntime() must return null because of the implementation of
		// factory
		assertNull(pci.getSensor().getRuntime());
	}

	@Test
	public void testGetSensorWithPciAndMockingRuntimeAndMockingDoubleSensorAndMockingFactory() {
		PersistedSensorFactory<Double> factory = new PersistedSensorFactory<Double>() {
			@Override
			public Sensor<Double> createSensor(Command command) {
				return new MockSensor<Double>(new MockRoboticsRuntime());
			}
		};

		assertNotNull(factory);

		pci.setFactory(factory);

		assertNotNull(pci.getSensor());

		assertNotNull(pci.getSensor().getRuntime());
	}

	@Test(expected = IllegalStateException.class)
	public void testUnpersistWithNullCommandHandleExpectingException() {
		try {
			pci.unpersist();
		} catch (CommandException e) {
			fail("CommandException occured. Method unpersist() can't be tested.");
		}
	}

	@Test
	public void testUnpersistWithMockingCommandHandle() throws CommandException {
		CommandHandle handle = new MockCommandHandleImpl();

		cmd.setCommandHandle(handle);

		assertNotNull(cmd.getCommandHandle());

		PersistContext<Double> p = new PersistContext<Double>(cmd);

		assertNotNull(pci.getCommand().getCommandHandle());

		try {
			p.unpersist();
		} catch (CommandException e) {
			fail("CommandException occured. Method unpersist() can't be tested.");
		}

		p.unpersist();
	}
}
