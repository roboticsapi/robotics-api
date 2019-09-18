/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.util;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

public class HashCodeUtilTest {
	private enum Variation {
		TRUE_BOOLEAN, FALSE_BOOLEAN, CHAR, INT, FLOAT
	}

	private class MockHashMap extends HashMap<String, Integer> {
		private static final long serialVersionUID = 1210287522314861230L;

		private final Variation variation;
		private final int seed;

		MockHashMap(int seed, Variation variation) {
			this.seed = seed;
			this.variation = variation;
		}

		@Override
		public int hashCode() {
			switch (variation) {
			case TRUE_BOOLEAN:
				return HashCodeUtil.hash(seed, true);
			case FALSE_BOOLEAN:
				return HashCodeUtil.hash(seed, false);
			case CHAR:
				return HashCodeUtil.hash(seed, 'a');
			case INT:
				return HashCodeUtil.hash(seed, 1);
			case FLOAT:
				return HashCodeUtil.hash(seed, 2.5f);
			}
			return seed;
		}
	}

	private final int TEST_STRINGS_COUNT = 100;
	private final String[] testStrings = new String[TEST_STRINGS_COUNT];
	private Map<String, Integer> stringMap = null;

	public HashCodeUtilTest() {
		for (int i = 0; i < TEST_STRINGS_COUNT; i++) {
			testStrings[i] = "String" + (i + 1);
		}
	}

	@After
	public void teardown() {
		stringMap = null;
	}

	private int fillMockMapAndGetCountCollisions() {
		int countCollisions = 0;
		Map<Integer, Integer> intMap = new HashMap<Integer, Integer>();

		for (String s : testStrings) {
			stringMap.put(s, s.hashCode());
		}

		for (int i : stringMap.values()) {
			if (intMap.put(i, i) != null) {
				countCollisions++;
			}
		}

		return countCollisions;
	}

	@Test
	public void testHashOnCollisionOccurenceWithZeroSeedAndTrueBooleanExpectingAtMostThreeCollisions() {
		stringMap = new MockHashMap(0, Variation.TRUE_BOOLEAN);

		final int COUNT_COLLISIONS = fillMockMapAndGetCountCollisions();

		if (COUNT_COLLISIONS > 3) {
			fail("The actual count of collisions was " + COUNT_COLLISIONS);
		}
	}

	@Test
	public void testHashOnCollisionOccurenceWithZeroSeedAndFalseBooleanExpectingAtMostThreeCollisions() {
		stringMap = new MockHashMap(0, Variation.FALSE_BOOLEAN);

		final int COUNT_COLLISIONS = fillMockMapAndGetCountCollisions();

		if (COUNT_COLLISIONS > 3) {
			fail("The actual count of collisions was " + COUNT_COLLISIONS);
		}
	}

	@Test
	public void testHashOnCollisionOccurenceWithZeroSeedAndCharExpectingAtMostThreeCollisions() {
		stringMap = new MockHashMap(0, Variation.CHAR);

		final int COUNT_COLLISIONS = fillMockMapAndGetCountCollisions();

		if (COUNT_COLLISIONS > 3) {
			fail("The actual count of collisions was " + COUNT_COLLISIONS);
		}
	}

	@Test
	public void testHashOnCollisionOccurenceWithZeroSeedAndIntExpectingAtMostThreeCollisions() {
		stringMap = new MockHashMap(0, Variation.INT);

		final int COUNT_COLLISIONS = fillMockMapAndGetCountCollisions();

		if (COUNT_COLLISIONS > 3) {
			fail("The actual count of collisions was " + COUNT_COLLISIONS);
		}
	}

	@Test
	public void testHashOnCollisionOccurenceWithZeroSeedAndFloatExpectingAtMostThreeCollisions() {
		stringMap = new MockHashMap(0, Variation.FLOAT);

		final int COUNT_COLLISIONS = fillMockMapAndGetCountCollisions();

		if (COUNT_COLLISIONS > 3) {
			fail("The actual count of collisions was " + COUNT_COLLISIONS);
		}
	}
}
