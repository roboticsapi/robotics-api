/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.entity.AbstractComposedEntity;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.mockclass.MockEntityImpl;

public class AbstractComposedEntityTest {
	private AbstractComposedEntity mockEntity = null;

	@Before
	public void setup() {
		mockEntity = new AbstractComposedEntity() {
		};
	}

	@After
	public void teardown() {
		mockEntity = null;
	}

	@Test
	public void testCanAddChildSimpleTestExpectingTrue() {
		assertTrue(mockEntity.canAddChild(new MockEntityImpl()));
	}

	@Test
	public void testCanAddChildWithInitializedMockAbstractComposedEntityExpectingFalse()
			throws InitializationException {
		new RoboticsContextImpl("dummy").initialize(mockEntity);

		assertTrue(mockEntity.isInitialized());

		assertFalse(mockEntity.canAddChild(new MockEntityImpl()));
	}

	@Test
	public void testCanRemoveChildSimpleTestExpectingTrue() {
		assertTrue(mockEntity.canRemoveChild(new MockEntityImpl()));
	}

	@Test
	public void testCanRemoveChildWithInitializedMockAbstractComposedEntityExpectingFalse()
			throws InitializationException {
		new RoboticsContextImpl("dummy").initialize(mockEntity);

		assertTrue(mockEntity.isInitialized());

		assertFalse(mockEntity.canRemoveChild(new MockEntityImpl()));
	}
}
