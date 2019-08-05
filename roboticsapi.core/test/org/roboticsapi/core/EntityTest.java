/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.entity.Property;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.mockclass.TestComposedEntity;
import org.roboticsapi.mockclass.TestEntity;

public class EntityTest {

	protected Entity entity;
	protected Property valid, invalid;

	@Before
	public void setup() {
		this.entity = createEntity();
		this.valid = createValidProperty();
		this.invalid = createInvalidProperty();
	}

	protected Entity createEntity() {
		return new TestEntity();
	}

	protected Property createValidProperty() {
		return new TestEntity.ValidTestProperty();
	}

	protected Property createInvalidProperty() {
		return new TestEntity.InvalidTestProperty();
	}

	@After
	public void teardown() {
		this.entity = null;
		this.valid = null;
		this.invalid = null;
	}

	@Test
	public void testSettingParent() {
		try {
			ComposedEntity parent = new TestComposedEntity(true, true);

			entity.setParent(parent);
			Assert.assertEquals(parent, entity.getParent());

			entity.setParent(null);
			Assert.assertNull(parent.getParent());
		} catch (EntityException e) {
			Assert.fail("Setting and unsetting parent has to work");
		}

		try {
			ComposedEntity parent = new TestComposedEntity(false, false);
			entity.setParent(parent);
			Assert.fail("Setting parent should throw exception");
		} catch (EntityException e) {
		}

		try {
			ComposedEntity parent = new TestComposedEntity(true, false);
			entity.setParent(parent);

			entity.setParent(null);
			Assert.fail("Unsetting parent should throw exception");
		} catch (EntityException e) {
		}

	}

	@Test
	public void testSettingParentWhileEntityHasAlreadyParent() throws EntityException {
		ComposedEntity parent = new TestComposedEntity(true, true);

		entity.setParent(parent);
		Assert.assertEquals(parent, entity.getParent());

		try {
			entity.setParent(new TestComposedEntity(true, true));
			Assert.fail("Cannot set parent");
		} catch (EntityException e) {
		}

		Assert.assertEquals(parent, entity.getParent());
	}

	@Test
	public void testNewEntityReturnsEmptyPropertyCollection() {
		Collection<Property> properties = entity.getProperties();

		Assert.assertNotNull(properties);
		Assert.assertTrue(properties.size() == 0);
	}

	@Test
	public void testPropertyCollectionIsReadOnly() {
		Collection<Property> properties = entity.getProperties();

		try {
			properties.add(valid);
			Assert.fail("Cannot add property directly");
			properties.add(invalid);
			Assert.fail("Cannot add property directly");
		} catch (UnsupportedOperationException e) {
		}
	}

	@Test
	public void testGettingPropertyOfGivenType() {
		entity.addProperty(valid);

		Set<? extends Property> properties = entity.getProperties(valid.getClass());

		Assert.assertNotNull(properties);
		Assert.assertEquals(1, properties.size());

		Iterator<? extends Property> iterator = properties.iterator();
		Property property = iterator.next();

		Assert.assertNotNull(property);
		Assert.assertEquals(valid, property);

		properties = entity.getProperties(invalid.getClass());

		Assert.assertNotNull(properties);
		Assert.assertEquals(0, properties.size());
	}
}
