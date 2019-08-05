/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.util.EntitySet;
import org.roboticsapi.mockclass.MockComposedEntityImpl;
import org.roboticsapi.mockclass.MockEntityImpl;

public class EntitySetTest {
	private class MockEntitySet extends EntitySet {
		public MockEntitySet(ComposedEntity parent) {
			super(parent);
		}

		private boolean acceptedAddChild = false;
		private final boolean acceptedRemoveChilde = false;

		public void enableAddingChild() {
			acceptedAddChild = true;
		}

		@Override
		protected void acceptAddingChild(Entity child) throws EntityException {
			if (!acceptedAddChild) {
				throw new EntityException("TestEntityException");
			}
		}

		@Override
		protected void acceptRemovingChild(Entity child) throws EntityException {
			if (!acceptedRemoveChilde) {
				throw new EntityException("TestEntityException");
			}
		}
	}

	private MockComposedEntityImpl mockParent = null;
	private MockEntitySet mockEntitySet = null;

	@Before
	public void setup() {
		mockParent = new MockComposedEntityImpl();
		mockEntitySet = new MockEntitySet(mockParent);
	}

	@After
	public void teardown() {
		mockEntitySet = null;
		mockParent = null;
	}

	@Test
	public void testCanAddExpectingTrue() {
		mockEntitySet.enableAddingChild();

		assertTrue(mockEntitySet.canAdd(new MockEntityImpl()));
	}

	@Test
	public void testCanAddExpectingFalse() {
		assertFalse(mockEntitySet.canAdd(new MockEntityImpl()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveWithTestChildWithAnotherParentExpectingException() throws EntityException {
		Entity testChild = new MockEntityImpl();
		testChild.setParent(new MockComposedEntityImpl());

		mockEntitySet.remove(testChild);
	}

	@Test
	public void testRemoveWithTestChildWithMockParentAsParentSimpleTest() throws EntityException {
		Entity testChild = new MockEntityImpl();
		testChild.setParent(mockParent);

		mockEntitySet.remove(testChild);
	}

	@Test
	public void testCanRemoveExpectingFalse() {
		assertFalse(mockEntitySet.canRemove(new MockEntityImpl()));
	}
}
