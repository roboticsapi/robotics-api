/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.systemtest;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Assume;

public class RasAlGhul {

	public static void assumeArrayEquals(double[] actualValues, double[] expectedValues, double delta) {
		Assume.assumeTrue("Array lengths do not match: actualValues.length: " + actualValues.length
				+ ", expectedValues.length: " + expectedValues.length, actualValues.length == expectedValues.length);
		double[] deltas = new double[actualValues.length];
		for (int i = 0; i < actualValues.length; i++) {
			deltas[i] = delta;
		}
		assumeArrayEquals(actualValues, expectedValues, deltas);
	}

	public static void assumeArrayEquals(double[] actualValues, double[] expectedValues, double[] deltas) {
		Assume.assumeTrue(
				"Array lengths do not match: actualValues.length: " + actualValues.length + ", expectedValues.length: "
						+ expectedValues.length + ", deltas.length: " + deltas.length,
				(actualValues.length == expectedValues.length) && (expectedValues.length == deltas.length));
		for (int i = 0; i < actualValues.length; i++) {
			Assume.assumeTrue(
					"value[" + i + "] is not equal: actual " + Arrays.toString(actualValues) + ", expected "
							+ Arrays.toString(expectedValues),
					Math.abs(actualValues[i] - expectedValues[i]) <= deltas[i]);
		}
	}

	public static void assertArrayEquals(double[] actualValues, double[] expectedValues, double delta) {
		Assert.assertEquals("Array lengths do not match: actualValues.length: " + actualValues.length
				+ ", expectedValues.length: " + expectedValues.length, actualValues.length, expectedValues.length);
		double[] deltas = new double[actualValues.length];
		for (int i = 0; i < actualValues.length; i++) {
			deltas[i] = delta;
		}
		assertArrayEquals(actualValues, expectedValues, deltas);
	}

	public static void assertArrayEquals(double[] actualValues, double[] expectedValues, double[] deltas) {
		Assert.assertEquals(
				"Array lengths do not match: actualValues.length: " + actualValues.length + ", expectedValues.length: "
						+ expectedValues.length + ", deltas.length: " + deltas.length,
				actualValues.length, expectedValues.length, deltas.length);
		for (int i = 0; i < actualValues.length; i++) {
			Assert.assertTrue(
					"value[" + i + "] is not equal: actual " + Arrays.toString(actualValues) + ", expected "
							+ Arrays.toString(expectedValues),
					Math.abs(actualValues[i] - expectedValues[i]) <= deltas[i]);
		}
	}

}
