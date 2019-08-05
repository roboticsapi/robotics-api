/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.roboticsapi.core.ImmediateFuture;

public class ImmediateFutureTest {
	// private ImmediateFuture future;
	private static List<Object> list;

	@BeforeClass
	public static void setUp() {
		list = new ArrayList<Object>();

		list.add(new Integer(1));
		list.add("Test-String");
		list.add(new Exception());
		list.add(new Double(Math.PI));
		list.add(new Date());
	}

	@AfterClass
	public static void tearDown() {
		list = null;
	}

	@Test
	public void testConstructorWithSeveralParameterClassesList() {
		for (Object o : list) {
			assertNotNull(new ImmediateFuture<Object>(o));
		}
	}

	@Test
	public void testOverridedCancelWithTrueAndFalseArgument() {
		ImmediateFuture<String> imf = new ImmediateFuture<String>("test");

		assertTrue(imf.cancel(true));
		assertTrue(imf.cancel(false));
	}

	@Test
	public void testOverridedGetWithTwoValues() throws InterruptedException, ExecutionException {
		ImmediateFuture<String> str = new ImmediateFuture<String>("test");
		ImmediateFuture<Double> pi = new ImmediateFuture<Double>(1.0);

		assertEquals("test", str.get());
		assertEquals(1.0, pi.get(), 0.001);
	}

	@Test
	public void testOverridedGetWithNullValue() throws InterruptedException, ExecutionException {
		assertNull(null, new ImmediateFuture<Object>(null).get());
	}

	@Test
	public void testOverridedGetWithParameters() throws InterruptedException, ExecutionException, TimeoutException {
		ImmediateFuture<String> str = new ImmediateFuture<String>("test");

		assertEquals("test", str.get(1, null));
	}

	@Test
	public void testOverridedIsCancelled() {
		assertFalse(new ImmediateFuture<Object>(null).isCancelled());
	}

	@Test
	public void testOverridedIsDone() {
		assertTrue(new ImmediateFuture<Object>(null).isDone());
	}

	@Test
	public void testOverridedEqualsWithNonImmediateFutureObject() {
		assertFalse(new ImmediateFuture<Double>(1.0).equals("test"));
	}

	@Test
	public void testOverridedEqualsWithTwoSimilarValues() {
		int x = 1;

		ImmediateFuture<Integer> imf1 = new ImmediateFuture<Integer>(x);
		ImmediateFuture<Double> imf2 = new ImmediateFuture<Double>(new Double(x));

		assertFalse(imf1.equals(imf2));
	}

	@Test
	public void testOverridedEqualsWithTwoEqualValues() {
		int x = 1;

		ImmediateFuture<Integer> imf1 = new ImmediateFuture<Integer>(x);
		ImmediateFuture<Integer> imf2 = new ImmediateFuture<Integer>(1);

		assertTrue(imf1.equals(imf2));
	}

	@Test
	public void testOverridedEqualsWithTwoDifferentValues() {
		ImmediateFuture<String> imf1 = new ImmediateFuture<String>("test");
		ImmediateFuture<String> imf2 = new ImmediateFuture<String>("test2");

		assertFalse(imf1.equals(imf2));
	}
}
