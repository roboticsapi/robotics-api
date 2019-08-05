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
import org.roboticsapi.core.entity.AbstractEntity;
import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.entity.EntityListener;
import org.roboticsapi.core.entity.Property;
import org.roboticsapi.core.entity.PropertyListener;
import org.roboticsapi.mockclass.TestComposedEntity;

public class AbstractEntityTest {
	private class MockEntity extends AbstractEntity {
		public void notifyListenersOnRelationAddedSubclass(ComposedEntity parent, Entity child) {
			super.notifyListenersOnRelationAdded(parent, child);
		}

		public void notifyListenersOnRelationRemovedSubclass(ComposedEntity parent, Entity child) {
			super.notifyListenersOnRelationRemoved(parent, child);
		}
	}

	private class MockEntityListener implements EntityListener {
		@Override
		public void onRelationAdded(ComposedEntity parent, Entity child) {
			notifyListenersOnRelationAddedDone = true;
		}

		@Override
		public void onRelationRemoved(ComposedEntity parent, Entity child) {
			notifyListenersOnRelationRemovedDone = true;
		}
	}

	private MockEntity mockEntity = null;
	private EntityListener mockListener = null;

	// for test method testNotifyListenersOnRelationAddedWithMockEntities() and
	// testNotifyListenersOnRelationAddedWithMockEntitiesAndTwoMockListener():
	private boolean notifyListenersOnRelationAddedDone = false;

	// for test method
	// testNotifyListenersOnRelationAddedWithMockEntitiesAndTwoMockListener():
	private boolean notifyListenersOnRelationAddedDoneTwice = false;

	// for test method testNotifyListenersOnRelationRemovedWithMockEntities and
	// testNotifyListenersOnRelationRemovedWithMockEntitiesAndTwoMockListener():
	private boolean notifyListenersOnRelationRemovedDone = false;

	// for test method
	// testNotifyListenersOnRelationRemovedWithMockEntitiesAndTwoMockListener():
	private boolean notifyListenersOnRelationRemovedDoneTwice = false;

	@Before
	public void setup() {
		mockEntity = new MockEntity();
		mockListener = new MockEntityListener();
	}

	@After
	public void teardown() {
		mockEntity = null;
		mockListener = null;
	}

	@Test
	public void testAddEntityListenerAndTestRemoveEntityListenerSimpleTest() {
		mockEntity.addEntityListener(mockListener);

		mockEntity.removeEntityListener(mockListener);
	}

	@Test
	public void testNotifyListenersOnRelationAddedWithMockEntities() {
		ComposedEntity mockParent = new TestComposedEntity(true, true);
		Entity mockChild = new MockEntity();

		mockEntity.addEntityListener(mockListener);

		assertFalse(notifyListenersOnRelationAddedDone);

		mockEntity.notifyListenersOnRelationAddedSubclass(mockParent, mockChild);

		assertTrue(notifyListenersOnRelationAddedDone);
	}

	@Test
	public void testNotifyListenersOnRelationRemovedWithMockEntities() {
		ComposedEntity mockParent = new TestComposedEntity(true, true);
		Entity mockChild = new MockEntity();

		mockEntity.addEntityListener(mockListener);

		assertFalse(notifyListenersOnRelationRemovedDone);

		mockEntity.notifyListenersOnRelationRemovedSubclass(mockParent, mockChild);

		assertTrue(notifyListenersOnRelationRemovedDone);
	}

	@Test
	public void testAddPropertyListenerAndRemovePropertyListenerSimpleTest() {
		PropertyListener testListener = new PropertyListener() {
			@Override
			public void onPropertyAdded(Entity entity, Property p) {
				// empty implementation
			}
		};

		mockEntity.addPropertyListener(testListener);

		mockEntity.removePropertyListener(testListener);
	}

	@Test
	public void testNotifyListenersOnRelationAddedWithMockEntitiesAndTwoMockListener() {
		EntityListener secondMockListener = new EntityListener() {
			@Override
			public void onRelationAdded(ComposedEntity parent, Entity child) {
				notifyListenersOnRelationAddedDoneTwice = notifyListenersOnRelationAddedDone && true;
			}

			@Override
			public void onRelationRemoved(ComposedEntity parent, Entity child) {
				// empty implementation
			}
		};

		ComposedEntity mockParent = new TestComposedEntity(true, true);
		Entity mockChild = new MockEntity();

		mockEntity.addEntityListener(mockListener);
		mockEntity.addEntityListener(secondMockListener);

		assertFalse(notifyListenersOnRelationAddedDone);
		assertFalse(notifyListenersOnRelationAddedDoneTwice);

		mockEntity.notifyListenersOnRelationAddedSubclass(mockParent, mockChild);

		assertTrue(notifyListenersOnRelationAddedDone);
		assertTrue(notifyListenersOnRelationAddedDoneTwice);
	}

	@Test
	public void testNotifyListenersOnRelationRemovedWithMockEntitiesAndTwoMockListener() {
		EntityListener secondMockListener = new EntityListener() {
			@Override
			public void onRelationAdded(ComposedEntity parent, Entity child) {
				// empty implementation
			}

			@Override
			public void onRelationRemoved(ComposedEntity parent, Entity child) {
				notifyListenersOnRelationRemovedDoneTwice = notifyListenersOnRelationRemovedDone && true;
			}
		};

		ComposedEntity mockParent = new TestComposedEntity(true, true);
		Entity mockChild = new MockEntity();

		mockEntity.addEntityListener(mockListener);
		mockEntity.addEntityListener(secondMockListener);

		assertFalse(notifyListenersOnRelationRemovedDone);
		assertFalse(notifyListenersOnRelationRemovedDoneTwice);

		mockEntity.notifyListenersOnRelationRemovedSubclass(mockParent, mockChild);

		assertTrue(notifyListenersOnRelationRemovedDone);
		assertTrue(notifyListenersOnRelationRemovedDoneTwice);
	}
}
