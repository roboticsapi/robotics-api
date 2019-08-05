/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.mockclass.MockDeviceImpl;
import org.roboticsapi.mockclass.MockRelation;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.RelationListener;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.TeachingInfo;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Vector;

public class FrameTest {

	// private FrameStore frameStore;
	List<StaticConnection> relations = new ArrayList<StaticConnection>();

	private final RoboticsContext context;

	public FrameTest() {
		context = new RoboticsContextImpl("dummy");
	}

	@After
	public void deinit() {
	}

	@Test
	public void testAddedRelationContainedInRelations() throws InitializationException {
		Frame f1 = new Frame("f1");
		Frame f2 = new Frame("f1");

		context.initialize(f1);
		context.initialize(f2);

		StaticConnection relation = new StaticConnection();
		f1.addRelation(relation, f2);

		Assert.assertTrue(f1.getRelations().contains(relation));

	}

	@Test
	public void testGetConnectedFramesReturnsEmptyListIfNoFramesConnected() {
		Frame f1 = new Frame("test");

		List<Frame> connectedFrames = f1.getConnectedFrames();

		Assert.assertEquals(0, connectedFrames.size());
	}

	@Test
	public void testGetConnectedFramesReturnsAllConnectedFrames() {
		Frame f1 = new Frame("f1");

		Frame f2 = new Frame("f2", f1, new Transformation());
		Frame f3 = new Frame("f3", f2, new Transformation());
		Frame f4 = new Frame("f4", f3, new Transformation());
		Frame f5 = new Frame("f5", f3, new Transformation());

		List<Frame> connectedFrames = f1.getConnectedFrames();

		Assert.assertEquals(4, connectedFrames.size());
		Assert.assertTrue(connectedFrames.contains(f2));
		Assert.assertTrue(connectedFrames.contains(f3));
		Assert.assertTrue(connectedFrames.contains(f4));
		Assert.assertTrue(connectedFrames.contains(f5));

	}

	@Test
	public void testGetConnectedFramesReturnsAllConnectedFramesWithCycle() {
		Frame f1 = new Frame("test");
		Frame f2 = new Frame(f1, new Transformation());
		Frame f3 = new Frame(f2, new Transformation());
		Frame f4 = new Frame(f3, new Transformation());
		Frame f5 = new Frame(f4, new Transformation());
		Frame f6 = new Frame(f5, new Transformation());

		try {
			context.initialize(f5);
			f5.addRelation(new StaticConnection(new Transformation()), f1);
		} catch (InitializationException e) {
			Assert.fail();
		}

		List<Frame> connectedFrames = f1.getConnectedFrames();

		Assert.assertEquals(5, connectedFrames.size());
		Assert.assertTrue(connectedFrames.contains(f2));
		Assert.assertTrue(connectedFrames.contains(f3));
		Assert.assertTrue(connectedFrames.contains(f4));
		Assert.assertTrue(connectedFrames.contains(f5));
		Assert.assertTrue(connectedFrames.contains(f6));
	}

	@Test
	public void testGetConnectedFramesObeysForbiddenRelations() {
		Frame f1 = new Frame("test");
		Frame f2 = new Frame(f1, new Transformation());

		Frame f3 = new Frame("test");
		Frame f4 = new Frame(f3, new Transformation());

		StaticConnection f2f3 = new StaticConnection(new Transformation());

		try {
			context.initialize(f2);
			f2.addRelation(f2f3, f3);
		} catch (InitializationException e) {
			Assert.fail();
		}

		List<Frame> connectedFrames = f1.getConnectedFrames(f2f3);

		Assert.assertEquals(1, connectedFrames.size());
		Assert.assertTrue(connectedFrames.contains(f2));
		Assert.assertFalse(connectedFrames.contains(f3));
		Assert.assertFalse(connectedFrames.contains(f4));
	}

	@Test
	public void testGetConnectedFramesObeysForbiddenRelationWhenNoOtherRelationPresent() {
		Frame f1 = new Frame("test");
		Frame f2 = new Frame("test");

		StaticConnection f1f2 = new StaticConnection(new Transformation());
		try {
			context.initialize(f1);
			f1.addRelation(f1f2, f2);
		} catch (InitializationException e) {
			Assert.fail();
		}

		List<Frame> connectedFrames = f1.getConnectedFrames(f1f2);

		Assert.assertEquals(0, connectedFrames.size());
	}

	@Test
	public void testPlusWithReferenceFrameTranslatesInReferenceFrame() throws RoboticsException {
		Frame f1 = new Frame("f1");
		Frame f2 = new Frame("f2");

		context.initialize(f1);
		f1.addRelation(new StaticConnection(new Transformation(1, 2, 3, 3, 2, 1)), f2);

		Frame f3 = f2.plus(1, 2, 3, f1.getOrientation());

		Frame f4 = f2.plus(1, 0, 0, new Orientation(f1, new Rotation(Math.PI / 2f, 0, 0)));

		Transformation t = f1.getTransformationTo(f3);
		Transformation t2 = f1.getTransformationTo(f4);

		assertTrue(t.getTranslation().isEqualVector(new Vector(2, 4, 6)));
		assertTrue(t.getRotation().isEqualRotation(new Rotation(3, 2, 1)));

		assertTrue(t2.getTranslation().isEqualVector(new Vector(1, 3, 3)));
		assertTrue(t.getRotation().isEqualRotation(new Rotation(3, 2, 1)));

	}

	@Test
	public void testConstructorWithReferenceFrameAndDoubleValuesForXYZABCArgumentsExpectionNotNull() {
		Frame referenceFrame = new Frame();

		assertNotNull(new Frame(referenceFrame, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6));
	}

	@Test(expected = InitializationException.class)
	public void testMethodAddRelationWithNotInitializedFrameExceptingInitializationException()
			throws InitializationException {
		Relation testRelation = new MockRelation();
		Frame goalFrame = new Frame();

		Frame testFrame = new Frame();

		testFrame.addRelation(testRelation, goalFrame);
	}

	private class MockRelationListenerImpl implements RelationListener {
		@Override
		public void relationAdded(Relation addedRelation, Frame endpoint) {
			throw new RuntimeException("TestException");
		}

		@Override
		public void relationRemoved(Relation removedRelation, Frame endpoint) {
			// Auto-generated method stub
		}
	}

	@Test
	public void testProtectedMethodNotifyRelationAddedByUsingInheritedMethodInitializeOfTestRelationExpectingCatchExceptionCase()
			throws InitializationException {
		RelationListener testRelationListener = new MockRelationListenerImpl();

		RoboticsContext testInitializationContext = new RoboticsContextImpl("dummy");

		Frame fromFrame = new Frame();
		fromFrame.addRelationListener(testRelationListener);
		testInitializationContext.initialize(fromFrame);

		Frame toFrame = new Frame();
		toFrame.addRelationListener(testRelationListener);
		testInitializationContext.initialize(toFrame);

		Relation testRelation = new MockRelation();
		testRelation.setFrom(fromFrame);
		testRelation.setTo(toFrame);

		testInitializationContext.initialize(testRelation);
	}

	@Test
	public void testBothSignaturesOfMethodAddTeachingInfoExpectingNotEmptyMap() {
		Device testDevice = new MockDeviceImpl();
		Frame motionCenter = new Frame();
		double[] hintParameters = new double[] { 2.5, 1.5, 0.5 };

		Frame testFrame = new Frame();

		assertTrue(testFrame.getAllTeachingInfos().isEmpty());

		testFrame.addTeachingInfo(testDevice, motionCenter, hintParameters);

		assertFalse(testFrame.getAllTeachingInfos().isEmpty());

		testFrame.addTeachingInfo(testDevice, motionCenter, hintParameters);
	}

	@Test
	public void testRemoveTeachingInfoAfterAddingTeachingInfoExpectingEmptyMap() {
		Device testDevice = new MockDeviceImpl();
		Frame motionCenter = new Frame();
		double[] hintParameters = new double[] { 2.5, 1.5, 0.5 };

		TeachingInfo testTeachingInfo = new TeachingInfo(testDevice, motionCenter, hintParameters);

		Frame testFrame = new Frame();

		assertTrue(testFrame.getAllTeachingInfos().isEmpty());

		testFrame.addTeachingInfo(testTeachingInfo);

		assertFalse(testFrame.getAllTeachingInfos().isEmpty());

		testFrame.removeTeachingInfo(testTeachingInfo);

		assertTrue(testFrame.getAllTeachingInfos().isEmpty());
	}
}
