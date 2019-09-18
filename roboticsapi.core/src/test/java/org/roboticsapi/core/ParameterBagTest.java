/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.mockclass.MockParameterBag;

public class ParameterBagTest {

	private TestInterfaceParameterBag ibag;
	private TestClassParameterBag cbag;

	@Before
	public void init() {
		ibag = new TestInterfaceParameterBag();
		cbag = new TestClassParameterBag();
	}

	@Test
	public void testInterfaceGettableAfterWithParameters() {
		IBImpl b = new IBImpl();
		ParameterBag<IA> newBag = ibag.withParameters(b);
		IA b2 = newBag.get(IB.class);

		Assert.assertEquals(b, b2);
	}

	@Test
	public void testSubinterfaceItemReplacesSuperinterfaceItem() {
		IBImpl b = new IBImpl();
		ICaImpl c = new ICaImpl();

		ParameterBag<IA> newBag = ibag.withParameters(b).withParameters(c);

		Assert.assertEquals(c, newBag.get(IB.class));
		Assert.assertEquals(c, newBag.get(ICa.class));
	}

	@Test
	public void testSuperinterfaceItemDoesNotReplaceSubinterfaceItem() {
		IBImpl b = new IBImpl();
		ICaImpl c = new ICaImpl();

		ParameterBag<IA> newBag = ibag.withParameters(c).withParameters(b);

		Assert.assertEquals(b, newBag.get(IB.class));
		Assert.assertEquals(c, newBag.get(ICa.class));
	}

	@Test
	public void testSiblingInterfaceItemsCoexist() {
		ICaImpl ca = new ICaImpl();
		ICbImpl cb = new ICbImpl();

		ParameterBag<IA> newBag = ibag.withParameters(ca).withParameters(cb);

		Assert.assertEquals(ca, newBag.get(ICa.class));
		Assert.assertEquals(cb, newBag.get(ICb.class));
		// The item last added is delivered as superclass item
		Assert.assertEquals(cb, newBag.get(IB.class));
	}

	@Test
	public void testClassGettableAfterWithParameters() {
		B b = new B();
		ParameterBag<A> newBag = cbag.withParameters(b);
		A b2 = newBag.get(B.class);

		Assert.assertEquals(b, b2);
	}

	@Test
	public void testSubclassItemReplacesSuperclassItem() {
		B b = new B();
		Ca c = new Ca();

		ParameterBag<A> newBag = cbag.withParameters(b).withParameters(c);

		Assert.assertEquals(c, newBag.get(B.class));
		Assert.assertEquals(c, newBag.get(Ca.class));
	}

	@Test
	public void testSuperclassItemDoesNotReplaceSubclassItem() {
		B b = new B();
		Ca c = new Ca();

		ParameterBag<A> newBag = cbag.withParameters(c).withParameters(b);

		Assert.assertEquals(b, newBag.get(B.class));
		Assert.assertEquals(c, newBag.get(Ca.class));
	}

	@Test
	public void testSiblingClassItemsCoexist() {
		Ca ca = new Ca();
		Cb cb = new Cb();

		ParameterBag<A> newBag = cbag.withParameters(ca).withParameters(cb);

		Assert.assertEquals(ca, newBag.get(Ca.class));
		Assert.assertEquals(cb, newBag.get(Cb.class));
		// The item last added is delivered as superclass item
		Assert.assertEquals(cb, newBag.get(B.class));
	}

	@Test
	public void testSiblingClassItemsCoexistInInterfaceBag() {
		IAImpl1 ca = new IAImpl1();
		IAImpl2 cb = new IAImpl2();

		ParameterBag<IA> newBag = ibag.withParameters(ca).withParameters(cb);

		Assert.assertEquals(ca, newBag.get(IAImpl1.class));
		Assert.assertEquals(cb, newBag.get(IAImpl2.class));
		// The item last added is delivered as superclass item
		Assert.assertEquals(cb, newBag.get(IA.class));
	}

	public class TestInterfaceParameterBag extends ParameterBag<IA> {

		@Override
		protected ParameterBag<IA> createNew() {
			return new TestInterfaceParameterBag();
		}

		@Override
		protected boolean isElementAssignableFrom(Class<?> target) {
			return IA.class.isAssignableFrom(target);
		}

		@Override
		public IA[] getArray() {
			List<IA> all = getAll();

			return all.toArray(new IA[all.size()]);
		}

	}

	public class TestClassParameterBag extends ParameterBag<A> {

		@Override
		protected ParameterBag<A> createNew() {
			return new TestClassParameterBag();
		}

		@Override
		protected boolean isElementAssignableFrom(Class<?> target) {
			return A.class.isAssignableFrom(target);
		}

		@Override
		public A[] getArray() {
			List<A> all = getAll();

			return all.toArray(new A[all.size()]);
		}
	}

	public interface IA {
	}

	public interface IB extends IA {
	}

	public interface ICa extends IB {
	}

	public interface ICb extends IB {
	}

	public class IAImpl1 implements IA {
	}

	public class IAImpl2 implements IA {
	}

	public class IBImpl implements IB {
	}

	public class ICaImpl implements ICa {
	}

	public class ICbImpl implements ICb {
	}

	public class A {
	}

	public class B extends A {
	}

	public class Ca extends B {
	}

	public class Cb extends B {
	}

	@Test
	public void testOverrideMethodToStringExpectingEmptyResult() {
		ParameterBag<Integer> testBag = new MockParameterBag<Integer>();

		assertEquals("[]", testBag.toString());
	}

	@Test
	public void testOverrideMethodToStringExpectingTestStringAfterAddingAParameter() {
		ParameterBag<Integer> testBag = new MockParameterBag<Integer>();

		ParameterBag<Integer> testBag2 = testBag.withParameters(2);

		assertEquals("[2]", testBag2.toString());
	}
}
