/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

public class Matrix3x3Test {
	private Matrix3x3 testMatrix = null;

	// @Before
	// public void setup() {}

	@After
	public void teardown() {
		testMatrix = null;
	}

	@Test
	public void testInvertExpectingInvertedTestMatrixEqualsExpectingInvertedMatrix() {
		testMatrix = new Matrix3x3(2, 2, 0, 2, 0, 2, 0, 2, 2);
		Matrix3x3 expectedInvertedMatrix = new Matrix3x3(0.25, 0.25, -0.25, 0.25, -0.25, 0.25, -0.25, 0.25, 0.25);

		Matrix3x3 invertedTestMatrix = testMatrix.invert();

		assertEquals(expectedInvertedMatrix, invertedTestMatrix);
	}

	@Test(expected = ArithmeticException.class)
	public void testInvertWithMatrixHavingZeroDeterminantExpectingArithmeticException() {
		testMatrix = new Matrix3x3(1, 2, 3, 1, 2, 3, 1, 2, 3);

		testMatrix.invert();
	}
}
