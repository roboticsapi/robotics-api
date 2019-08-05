/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.mockclass.TestRuntime;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.JavaTransformationSensorConnection;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.util.WorldUtils;

public class WorldUtilsTest {

	private TestRuntime runtime;
	private Frame root;

	List<Frame> frames = new ArrayList<Frame>();

	List<Relation> statRelations = new ArrayList<Relation>();
	List<Relation> tempRelations = new ArrayList<Relation>();
	List<Relation> dynaRelations = new ArrayList<Relation>();

	RoboticsContext context;

	public WorldUtilsTest() {
		context = new RoboticsContextImpl("dummy");
	}

	@Before
	public void init() throws RoboticsException {
		runtime = new TestRuntime();
		context.initialize(runtime);

		root = new Frame("ROOT");
		context.initialize(root);

		Frame frame = new Frame("0");
		add(root, frame, new StaticConnection(), statRelations);
		add(frame, new Frame("1"), new StaticConnection(), statRelations);

		frame = new Frame("2");
		add(root, frame, new JavaTransformationSensorConnection(runtime), dynaRelations);
		add(frame, new Frame("3"), new StaticConnection(), statRelations);

		frame = new Frame("4", root, new Transformation());
		frames.add(frame);
		tempRelations.addAll(frame.getRelations());

		frame = new Frame("5", frame, new Transformation());
		frames.add(frame);
		tempRelations.addAll(frame.getRelations());
	}

	private void add(Frame base, Frame frame, Relation relation, List<Relation> list) throws RoboticsException {
		base.addRelation(relation, frame);

		frames.add(frame);
		list.add(relation);
	}

	@After
	public void deinit() {
		runtime = null;
		root = null;
	}

	@Test
	public void testGetConnectedFramesAndRelations() {
		Set<Entity> result = WorldUtils.getConnectedFramesAndRelations(root);

		assertSetContainsAllFrames(result);
		Assert.assertTrue(result.containsAll(statRelations));
		Assert.assertTrue(result.containsAll(dynaRelations));
		Assert.assertTrue(result.containsAll(tempRelations));
		Assert.assertEquals(12, result.size());

		result = WorldUtils.getConnectedFramesAndRelations(root, false, true);

		assertSetContainsOnlySpecifiedFrames(result, 0, 1, 4, 5);
		assertSetContainsOnlySpecifiedStaticConnections(result, 0, 1);
		Assert.assertFalse(result.containsAll(dynaRelations));
		Assert.assertTrue(result.containsAll(tempRelations));
		Assert.assertEquals(8, result.size());

		result = WorldUtils.getConnectedFramesAndRelations(root, true, false);

		assertSetContainsOnlySpecifiedFrames(result, 0, 1, 2, 3);
		Assert.assertTrue(result.containsAll(statRelations));
		Assert.assertTrue(result.containsAll(dynaRelations));
		Assert.assertFalse(result.containsAll(tempRelations));
		Assert.assertEquals(8, result.size());

		result = WorldUtils.getConnectedFramesAndRelations(root, false, false);

		assertSetContainsOnlySpecifiedFrames(result, 0, 1);
		assertSetContainsOnlySpecifiedStaticConnections(result, 0, 1);
		Assert.assertFalse(result.containsAll(dynaRelations));
		Assert.assertFalse(result.containsAll(tempRelations));
		Assert.assertEquals(4, result.size());
	}

	@Test
	public void testGetConnectedFramesAndRelationsWithForbiddenRelations() {
		Set<Entity> result = WorldUtils.getConnectedFramesAndRelations(root, false, false, statRelations.get(0));

		Assert.assertTrue(result.isEmpty());

		result = WorldUtils.getConnectedFramesAndRelations(root, true, true, statRelations.get(0),
				statRelations.get(2));

		assertSetContainsOnlySpecifiedFrames(result, 2, 4, 5);
		Assert.assertFalse(result.containsAll(statRelations));
		Assert.assertTrue(result.containsAll(dynaRelations));
		Assert.assertTrue(result.containsAll(tempRelations));
		Assert.assertEquals(6, result.size());
	}

	@Test
	public void testGetConnectedFramesAndRelationsWithForbiddenFrames() {
		Set<Entity> result = WorldUtils.getConnectedFramesAndRelations(root, true, true, frames.get(1), frames.get(3),
				frames.get(5));

		assertSetContainsOnlySpecifiedFrames(result, 0, 2, 4);
		Assert.assertTrue(result.containsAll(statRelations));
		Assert.assertTrue(result.containsAll(dynaRelations));
		Assert.assertTrue(result.containsAll(tempRelations));
		Assert.assertEquals(9, result.size());

		result = WorldUtils.getConnectedFramesAndRelations(root, false, false, frames.get(0));

		assertSetContainsOnlySpecifiedStaticConnections(result, 0);
		Assert.assertEquals(1, result.size());
	}

	private void assertSetContainsAllFrames(Set<Entity> result) {
		Assert.assertTrue(result.containsAll(frames));
	}

	private void assertSetContainsOnlySpecifiedFrames(Set<Entity> result, int... ids) {
		int count = 0;

		for (Entity entity : result) {
			if (entity instanceof Frame) {
				count++;
			}
		}
		Assert.assertTrue(count == ids.length);

		for (int i = 0; i < ids.length; i++) {
			int id = ids[i];

			Assert.assertTrue(result.contains(frames.get(id)));
		}
	}

	private void assertSetContainsOnlySpecifiedStaticConnections(Set<Entity> result, int... ids) {
		int count = 0;

		for (Entity entity : result) {
			if (entity instanceof StaticConnection) {
				count++;
			}
		}
		Assert.assertTrue(count == ids.length);

		for (int i = 0; i < ids.length; i++) {
			int id = ids[i];

			Assert.assertTrue(result.contains(statRelations.get(id)));
		}
	}

}
