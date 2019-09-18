/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

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
import org.roboticsapi.core.world.mockclass.MockDevice;
import org.roboticsapi.core.world.relation.ConfiguredStaticConnection;
import org.roboticsapi.core.world.relation.StaticConnection;
import org.roboticsapi.core.world.relation.StaticPosition;

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

		StaticConnection relation = new StaticConnection(f1, f2);
		relation.establish();

		Assert.assertTrue(f1.getRelations().contains(relation));

	}

	private void addRelation(Frame from, Frame to, ConfiguredStaticConnection relation) throws InitializationException {
		relation.setFrom(from);
		relation.setTo(to);
		context.initialize(relation);
	}

	@Test
	public void testGetConnectedFramesReturnsEmptyListIfNoFramesConnected() {
		Frame f1 = new Frame("test");

		List<Frame> connectedFrames = f1.getConnectedFrames();

		Assert.assertEquals(0, connectedFrames.size());
	}

	@Test
	public void testGetConnectedFramesReturnsAllConnectedFrames() throws InitializationException {
		Frame f1 = new Frame("f1");

		Frame f2 = createFrame("f2", f1, Transformation.IDENTITY);
		Frame f3 = createFrame("f3", f2, Transformation.IDENTITY);
		Frame f4 = createFrame("f4", f3, Transformation.IDENTITY);
		Frame f5 = createFrame("f5", f3, Transformation.IDENTITY);

		List<Frame> connectedFrames = f1.getConnectedFrames();

		Assert.assertEquals(4, connectedFrames.size());
		Assert.assertTrue(connectedFrames.contains(f2));
		Assert.assertTrue(connectedFrames.contains(f3));
		Assert.assertTrue(connectedFrames.contains(f4));
		Assert.assertTrue(connectedFrames.contains(f5));

	}

	public Frame createFrame(String name, Frame parent, Transformation place) {
		Frame f = new Frame("name");
		new StaticPosition(parent, f, place).establish();
		return f;
	}

	@Test
	public void testGetConnectedFramesReturnsAllConnectedFramesWithCycle() {
		Frame f1 = new Frame("test");
		Frame f2 = createFrame("f2", f1, Transformation.IDENTITY);
		Frame f3 = createFrame("f3", f2, Transformation.IDENTITY);
		Frame f4 = createFrame("f4", f3, Transformation.IDENTITY);
		Frame f5 = createFrame("f5", f4, Transformation.IDENTITY);
		Frame f6 = createFrame("f6", f5, Transformation.IDENTITY);

		try {
			context.initialize(f5);
			new StaticPosition(f5, f1, Transformation.IDENTITY).establish();
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
		Frame f2 = createFrame("f2", f1, Transformation.IDENTITY);

		Frame f3 = new Frame("test");
		Frame f4 = createFrame("f4", f3, Transformation.IDENTITY);

		StaticPosition f2f3 = new StaticPosition(f2, f3, Transformation.IDENTITY);
		f2f3.establish();

		List<Frame> connectedFrames = f1.getConnectedFrames(World.getCommandedTopology().without(f2f3));

		Assert.assertEquals(1, connectedFrames.size());
		Assert.assertTrue(connectedFrames.contains(f2));
		Assert.assertFalse(connectedFrames.contains(f3));
		Assert.assertFalse(connectedFrames.contains(f4));
	}

	@Test
	public void testGetConnectedFramesObeysForbiddenRelationWhenNoOtherRelationPresent() {
		Frame f1 = new Frame("test");
		Frame f2 = new Frame("test");

		StaticPosition f1f2 = new StaticPosition(f1, f2, Transformation.IDENTITY);
		f1f2.establish();

		List<Frame> connectedFrames = f1.getConnectedFrames(World.getCommandedTopology().without(f1f2));

		Assert.assertEquals(0, connectedFrames.size());
	}

	@Test
	public void testPlusWithReferenceFrameTranslatesInReferenceFrame() throws RoboticsException {
		Frame f1 = new Frame("f1");
		Frame f2 = new Frame("f2");

		context.initialize(f1);
		new StaticPosition(f1, f2, new Transformation(1, 2, 3, 3, 2, 1)).establish();

		Pose f3 = f2.asPose().plus(f1.asOrientation(), 1, 2, 3);

		Pose f4 = f2.asPose().plus(new Orientation(f1, new Rotation(Math.PI / 2f, 0, 0)), 1, 0, 0);

		Transformation t = f1.asPose().getCommandedTransformationTo(f3);
		Transformation t2 = f1.asPose().getCommandedTransformationTo(f4);

		assertTrue(t.getTranslation().isEqualVector(new Vector(2, 4, 6)));
		assertTrue(t.getRotation().isEqualRotation(new Rotation(3, 2, 1)));

		assertTrue(t2.getTranslation().isEqualVector(new Vector(1, 3, 3)));
		assertTrue(t.getRotation().isEqualRotation(new Rotation(3, 2, 1)));

	}

	@Test
	public void testConstructorWithReferenceFrameAndDoubleValuesForXYZABCArgumentsExpectionNotNull()
			throws TransformationException {
		Frame referenceFrame = new Frame();

		assertNotNull(referenceFrame.asPose().plus(0.1, 0.2, 0.3, 0.4, 0.5, 0.6));
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
	public void testBothSignaturesOfMethodAddTeachingInfoExpectingNotEmptyMap() throws TransformationException {
		Device testDevice = new MockDevice();
		Frame motionCenter = new Frame();
		double[] hintParameters = new double[] { 2.5, 1.5, 0.5 };

		Frame testFrame = new Frame();

		Pose pose = testFrame.asPose().plus(1, 0, 0);

		assertTrue(testFrame.getAllTeachingInfos().isEmpty());

		testFrame.addTeachingInfo(testDevice, pose, motionCenter.asPose(), hintParameters);

		assertFalse(testFrame.getAllTeachingInfos().isEmpty());

		testFrame.addTeachingInfo(testDevice, pose, motionCenter.asPose(), hintParameters);
	}

	@Test
	public void testRemoveTeachingInfoAfterAddingTeachingInfoExpectingEmptyMap() {
		Device testDevice = new MockDevice();
		Frame motionCenter = new Frame();
		Frame testFrame = new Frame();
		Pose pose = testFrame.asPose();

		double[] hintParameters = new double[] { 2.5, 1.5, 0.5 };

		TeachingInfo testTeachingInfo = new TeachingInfo(testDevice, pose, motionCenter.asPose(), hintParameters);

		assertTrue(testFrame.getAllTeachingInfos().isEmpty());

		testFrame.addTeachingInfo(testTeachingInfo);

		assertFalse(testFrame.getAllTeachingInfos().isEmpty());

		testFrame.removeTeachingInfo(testTeachingInfo);

		assertTrue(testFrame.getAllTeachingInfos().isEmpty());
	}
}
