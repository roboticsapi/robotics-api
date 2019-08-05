/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Test;
import org.roboticsapi.world.Quaternion;
import org.roboticsapi.world.Vector;

public class QuaternionTest {
	private Quaternion testQuaternion = null;

	// @Before
	// public void setup() {}

	@After
	public void teardown() {
		testQuaternion = null;
	}

	@Test
	public void testGetAxisWithWGreaterThanOneExpectingZeroVector() {
		double x = 1;
		double y = 2;
		double z = 3;
		double w = 4;

		testQuaternion = new Quaternion(x, y, z, w);

		Vector zeroVector = new Vector();

		Vector testVector = testQuaternion.getAxis();

		assertEquals(zeroVector, testVector);
	}

	@Test
	public void testGetAxisWithWLessThanOneExpectingNormalizedVector() {
		double x = 1;
		double y = 2;
		double z = 4;
		double w = 0.5;

		Vector initVector = new Vector(x, y, z);

		double len = initVector.getLength();

		assertFalse("Test vector can't be used for this test because it's length is 0. Test will be aborted",
				(len == 0));

		testQuaternion = new Quaternion(x, y, z, w);

		Vector normalizedVector = new Vector((x / len), (y / len), (z / len));

		Vector testVector = testQuaternion.getAxis();

		assertEquals(normalizedVector, testVector);
	}

	@Test
	public void testGetAngleWithZeroWExpectingPI() {
		double x = 1;
		double y = 2;
		double z = 4;
		double w = 0;

		testQuaternion = new Quaternion(x, y, z, w);

		assertEquals(testQuaternion.getAngle(), Math.PI, 0.001);
	}
}
