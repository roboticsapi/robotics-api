/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.world.AbstractPhysicalObject;
import org.roboticsapi.world.Frame;

public class AbstractPhysicalObjectTest {
	private class MockAbstractPhysicalObject extends AbstractPhysicalObject {
	}

	private AbstractPhysicalObject mockAbstractPhysicalObject = null;

	@Before
	public void setup() {
		mockAbstractPhysicalObject = new MockAbstractPhysicalObject();
	}

	@After
	public void teardown() {
		mockAbstractPhysicalObject = null;
	}

	@Test
	public void testGetBaseExpectingBaseFrameSetBefore() {
		Frame testFrame = new Frame();

		mockAbstractPhysicalObject.setBase(testFrame);

		assertNotNull(mockAbstractPhysicalObject.getBase());

		assertEquals(testFrame, mockAbstractPhysicalObject.getBase());
	}

	@Test
	public void testInitializeUsingOverrideMethodFillAutomaticConfigurationPropertiesExpectingIsInitializedTrue()
			throws InitializationException {
		assertFalse(mockAbstractPhysicalObject.isInitialized());

		assertNull(mockAbstractPhysicalObject.getBase());

		new RoboticsContextImpl("dummy").initialize(mockAbstractPhysicalObject);

		assertTrue(mockAbstractPhysicalObject.isInitialized());

		assertNotNull(mockAbstractPhysicalObject.getBase());
	}
}
