/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.util.EntityUtils;
import org.roboticsapi.mockclass.TestComposedEntity;

public class EntityUtilsTest {
	private ComposedEntity mockEntity = null;

	@Before
	public void setup() {
		mockEntity = new TestComposedEntity(true, true);
	}

	@After
	public void teardown() {
		mockEntity = null;
	}

	@Test
	public void testGetChildrenSimpleTestWithMockEntity() {
		Set<Entity> testSet = EntityUtils.getChildren(mockEntity, Entity.class);

		assertNotNull(testSet);

		assertEquals(0, testSet.size());
	}

	@Test
	public void testGetDescendantsSimpleTestWithMockEntity() {
		Set<Entity> testSet = EntityUtils.getDescendants(mockEntity);

		assertNotNull(testSet);

		assertEquals(0, testSet.size());
	}

	@Test
	public void testGetChildrenWithMockEntityAndMockChildren() throws EntityException {
		Entity mockChild = new TestComposedEntity(false, false);

		mockEntity.addChild(mockChild);

		Set<Entity> testSet = EntityUtils.getChildren(mockEntity, Entity.class);

		assertNotNull(testSet);

		assertEquals(1, testSet.size());

		assertTrue(testSet.contains(mockChild));
	}

	@Test
	public void testGetDescendantsWithMockEntityAndMockDescendants() throws EntityException {
		ComposedEntity mockChild = new TestComposedEntity(true, true);
		Entity mockGrandchild = new TestComposedEntity(false, false);

		mockEntity.addChild(mockChild);
		mockChild.addChild(mockGrandchild);

		Set<Entity> testSet = EntityUtils.getDescendants(mockEntity);

		assertNotNull(testSet);

		assertEquals(2, testSet.size());

		assertTrue(testSet.contains(mockChild));
		assertTrue(testSet.contains(mockGrandchild));
	}

	@Test
	public void testGetAncestorsWithMockEntityAndMockAncestors() throws EntityException {
		ComposedEntity mockGrandfather = new TestComposedEntity(true, true);
		ComposedEntity mockFather = new TestComposedEntity(true, true);

		mockGrandfather.addChild(mockFather);
		mockFather.addChild(mockEntity);

		Set<ComposedEntity> testSet = EntityUtils.getAncestors(mockEntity);

		assertNotNull(testSet);

		assertEquals(2, testSet.size());

		assertTrue(testSet.contains(mockFather));
		assertTrue(testSet.contains(mockGrandfather));
	}

	@Test
	public void testGetAncestorWithMockEntityAndMockFather() throws EntityException {
		ComposedEntity mockFather = new TestComposedEntity(true, true);

		mockFather.addChild(mockEntity);

		ComposedEntity testAncestor = EntityUtils.getAncestor(mockEntity, ComposedEntity.class);

		assertNotNull(testAncestor);

		assertSame(mockFather, testAncestor);
	}

	@Test
	public void testGetAncestorWithMockEntityAndMockAncestors() throws EntityException {

		ComposedEntity mockGrandfather = new TestComposedEntity(true, true);
		ComposedEntity mockFather = new TestComposedEntity(true, true);

		mockGrandfather.addChild(mockFather);
		mockFather.addChild(mockEntity);

		ComposedEntity testAncestor = EntityUtils.getAncestor(mockEntity, ComposedEntity.class);

		assertNotNull(testAncestor);

		assertSame(mockGrandfather, testAncestor);
	}
}
