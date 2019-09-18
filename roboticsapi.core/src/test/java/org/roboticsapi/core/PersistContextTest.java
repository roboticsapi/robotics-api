/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.PersistContext.PersistedRealtimeValueFactory;
import org.roboticsapi.core.mockclass.MockCommand;
import org.roboticsapi.core.mockclass.MockCommandHandleImpl;
import org.roboticsapi.core.mockclass.MockRealtimeValue;
import org.roboticsapi.core.mockclass.MockRoboticsRuntime;

public class PersistContextTest {
	private PersistContext<Double> pci = null;
	private Command cmd = null;

	@Before
	public void setup() {
		cmd = new MockCommand("MockingCommand", null);
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

		pci.getValue();
	}

	@Test
	public void testGetSensorWithPciAndMockingDoubleSensorAndMockingFactory() {
		PersistedRealtimeValueFactory<Double> factory = new PersistedRealtimeValueFactory<Double>() {
			@Override
			public RealtimeValue<Double> createRealtimeValue(Command command) {
				return new MockRealtimeValue<Double>(null);
			}
		};

		assertNotNull(factory);

		pci.setFactory(factory);

		assertNotNull(pci.getValue());

		// getRuntime() must return null because of the implementation of
		// factory
		assertNull(pci.getValue().getRuntime());
	}

	@Test
	public void testGetSensorWithPciAndMockingRuntimeAndMockingDoubleSensorAndMockingFactory() {
		PersistedRealtimeValueFactory<Double> factory = new PersistedRealtimeValueFactory<Double>() {
			@Override
			public RealtimeValue<Double> createRealtimeValue(Command command) {
				return new MockRealtimeValue<Double>(new MockRoboticsRuntime());
			}
		};

		assertNotNull(factory);

		pci.setFactory(factory);

		assertNotNull(pci.getValue());

		assertNotNull(pci.getValue().getRuntime());
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
