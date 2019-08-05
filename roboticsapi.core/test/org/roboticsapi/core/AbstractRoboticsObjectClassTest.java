/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Test;
import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.entity.EntityListener;
import org.roboticsapi.core.entity.Property;
import org.roboticsapi.core.entity.PropertyListener;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.EntityException;

// only unit tests
public class AbstractRoboticsObjectClassTest {
	private class MockRoboticsObject extends AbstractRoboticsObject {
		private final RoboticsObject innerRoboticsObject = null;

		public boolean hasInnerRoboticsObject() {
			return (innerRoboticsObject != null);
		}

		@Override
		protected void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
			if (hasInnerRoboticsObject()) {
				createdObjects.put("MockRoboticsObject", innerRoboticsObject);
			}
		}

		public boolean checkInOpenIntervalMock(String key, int i, int min, int max) throws ConfigurationException {

			AbstractRoboticsObject.checkInOpenInterval(key, i, min, max);

			return true;
		}

		public boolean checkInClosedIntervalMock(String key, int i, int min, int max) throws ConfigurationException {

			AbstractRoboticsObject.checkInClosedInterval(key, i, min, max);

			return true;
		}

		public boolean checkNotNullAndInitializedMock(String key, RoboticsObject o) throws ConfigurationException {
			AbstractRoboticsObject.checkNotNullAndInitialized(key, o);

			return true;
		}

		public boolean checkInitializedIfNotNullMock(String key, RoboticsObject o) throws ConfigurationException {
			AbstractRoboticsObject.checkInitializedIfNotNull(key, o);

			return true;
		}

		public boolean checkNotNullWithObjectMock(String key, Object property) throws ConfigurationException {
			AbstractRoboticsObject.checkNotNull(key, property);

			return true;
		}

		public boolean checkNotNullWithRoboticsObjectsListMock(String key, List<? extends RoboticsObject> objects)
				throws ConfigurationException {
			AbstractRoboticsObject.checkNotNull(key, objects);

			return true;
		}

		public boolean checkNoParentMock(String key, Entity e) throws ConfigurationException {
			AbstractRoboticsObject.checkNoParent(key, e);

			return true;
		}
	}

	MockRoboticsObject mockRobObj = new MockRoboticsObject();

	@After
	public void teardown() {
		mockRobObj = null;
	}

	@Test
	public void testCheckInOpenIntervalWithValidValue() throws ConfigurationException {
		assertTrue(mockRobObj.checkInOpenIntervalMock("MockRoboticsObject", 2, 1, 3));
	}

	@Test
	public void testCheckInClosedIntervalWithValidValue() throws ConfigurationException {
		assertTrue(mockRobObj.checkInClosedIntervalMock("MockRoboticsObject", 2, 1, 3));
	}

	@Test(expected = ConfigurationException.class)
	public void testCheckInOpenIntervalWithToLowValueExpectingException() throws ConfigurationException {
		mockRobObj.checkInOpenIntervalMock("MockRoboticsObject", 1, 2, 3);
	}

	@Test(expected = ConfigurationException.class)
	public void testCheckInOpenIntervalWithToHighValueExpectingException() throws ConfigurationException {
		mockRobObj.checkInOpenIntervalMock("MockRoboticsObject", 3, 1, 2);
	}

	@Test(expected = ConfigurationException.class)
	public void testCheckInClosedIntervalWithToLowValueExpectingException() throws ConfigurationException {
		mockRobObj.checkInClosedIntervalMock("MockRoboticsObject", 1, 2, 3);
	}

	@Test(expected = ConfigurationException.class)
	public void testCheckInClosedIntervalWithToHighValueExpectingException() throws ConfigurationException {
		mockRobObj.checkInClosedIntervalMock("MockRoboticsObject", 3, 1, 2);
	}

	@Test(expected = ConfigurationException.class)
	public void testCheckNotNullAndInitializedWithNotInitializedRoboticsObjectExpectingException()
			throws ConfigurationException {
		assertFalse(mockRobObj.isInitialized());

		mockRobObj.checkNotNullAndInitializedMock("MockRoboticsObject", mockRobObj);
	}

	@Test(expected = ConfigurationException.class)
	public void testCheckInitializedIfNotNullWithNotInitializedRoboticsObjectExpectingException()
			throws ConfigurationException {
		assertFalse(mockRobObj.isInitialized());

		mockRobObj.checkInitializedIfNotNullMock("MockRoboticsObject", mockRobObj);
	}

	@Test(expected = ConfigurationException.class)
	public void testCheckNotNullWithObjectWithNullObjectExpectingException() throws ConfigurationException {
		mockRobObj.checkNotNullWithObjectMock("MockRoboticsObject", null);
	}

	@Test
	public void testCheckNotNullWithMockRoboticsObjectsList() throws ConfigurationException {
		List<RoboticsObject> objects = new ArrayList<RoboticsObject>();
		objects.add(mockRobObj);

		assertTrue(mockRobObj.checkNotNullWithRoboticsObjectsListMock("MockRoboticsObjects", objects));
	}

	@Test(expected = ConfigurationException.class)
	public void testCheckNoParentWithMockEntityWithParentExpectingException() throws ConfigurationException {
		class MockComposedEntity implements ComposedEntity {
			private ComposedEntity parent = null;
			private Set<Entity> children = null;

			@Override
			public ComposedEntity getParent() {
				return parent;
			}

			@Override
			public void setParent(ComposedEntity parent) throws EntityException {
				// only a simple set-method without respecting the
				// parent-child-assocation
				this.parent = parent;
			}

			@Override
			public void addEntityListener(EntityListener l) {
			}

			@Override
			public void removeEntityListener(EntityListener l) {
			}

			@Override
			public void addProperty(Property property) {
			}

			@Override
			public Set<Property> getProperties() {
				return null;
			}

			@Override
			public <T extends Property> Set<T> getProperties(Class<T> type) {
				return null;
			}

			@Override
			public void addPropertyListener(PropertyListener l) {
			}

			@Override
			public void removePropertyListener(PropertyListener l) {
			}

			@Override
			public Set<Entity> getChildren() {
				return children;
			}

			@Override
			public void addChild(Entity child) throws EntityException {
				// only a set-method without respecting the
				// parent-child-assocation
				if (child == null) {
					throw new NullPointerException("Entity child must not be null.");
				}

				if (children == null) {
					children = new HashSet<Entity>();
				}

				children.add(child);
			}

			@Override
			public boolean canAddChild(Entity child) {
				return true;
			}

			@Override
			public void removeChild(Entity child) throws EntityException {
				if (children == null) {
					throw new NullPointerException("This Entity has no children.");
				}

				if (child == null) {
					throw new NullPointerException("Entity child must not be null.");
				}

				children.remove(child);

				if (children.isEmpty()) {
					children = null;
				}
			}

			@Override
			public boolean canRemoveChild(Entity child) {
				return true;
			}

			@Override
			public <T extends Property> boolean hasProperty(Class<T> type) {
				return false;
			}

			@Override
			public void removeProperty(Property property) {

			}

			@Override
			public <T extends Property> void removeProperties(Class<T> type) {

			}
		}

		Entity child = new MockComposedEntity();
		ComposedEntity parent = new MockComposedEntity();

		try {
			child.setParent(parent);
		} catch (EntityException e) {
			fail("Exception while setting parent or child Entity. Test will be aborted.");
		}

		mockRobObj.checkNoParentMock("MockComposedEntity", child);
	}

	@Test
	public void testCheckInitializedIfNotNullWithNollObjectSimpleTest() throws ConfigurationException {
		mockRobObj.checkInitializedIfNotNullMock("Test Key", null);
	}
}
