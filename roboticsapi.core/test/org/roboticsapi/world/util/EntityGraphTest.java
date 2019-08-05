/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.util.EntityGraph;

public class EntityGraphTest {
	private Frame testRootFrame = null;
	private EntityGraph testEntityGraph = null;

	@Before
	public void setup() {
		testRootFrame = new Frame();
		testEntityGraph = new EntityGraph(testRootFrame);
	}

	@After
	public void teardown() {
		testRootFrame = null;
		testEntityGraph = null;
	}

	@Test
	public void testGetFrameGraphExpectingGotRootEqualsTestRootFrame() {
		Frame testFrame = testEntityGraph.getFrameGraph().getRoot();

		assertNotNull(testFrame);

		assertEquals(testRootFrame, testFrame);
	}
}
