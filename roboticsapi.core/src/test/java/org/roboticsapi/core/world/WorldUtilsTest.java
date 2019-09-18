/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.RoboticsEntity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.relation.DynamicConnection;
import org.roboticsapi.core.world.relation.Placement;
import org.roboticsapi.core.world.relation.StaticConnection;
import org.roboticsapi.core.world.util.WorldUtils;

public class WorldUtilsTest {

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
		root = new Frame("ROOT");
		context.initialize(root);

		Frame zero = new Frame("0");
		add(new StaticConnection(root, zero), statRelations);
		Frame one = new Frame("1");
		add(new StaticConnection(zero, one), statRelations);

		Frame two = new Frame("2");
		add(new DynamicConnection(root, two) {
			@Override
			public RealtimeTransformation getTransformationForState(Map<StateVariable, RealtimeDouble> state) {
				return RealtimeTransformation.createfromConstant(Transformation.IDENTITY);
			}

			@Override
			public RealtimeTwist getTwistForState(Map<StateVariable, RealtimeDouble> state) {
				return RealtimeTwist.createFromConstant(new Twist());
			}

			@Override
			public Map<StateVariable, RealtimeDouble> getStateForTransformation(RealtimeTransformation pose,
					FrameTopology topology) {
				return new HashMap<>();
			}

			@Override
			public Map<StateVariable, RealtimeDouble> getStateForTwist(RealtimeTransformation pose,
					RealtimeTwist velocity, FrameTopology topology) {
				return new HashMap<>();
			}
		}, dynaRelations);
		add(new StaticConnection(two, new Frame("3")), statRelations);

		Frame four = new Frame("4");
		add(new Placement(root, four), tempRelations);

		Frame five = new Frame("5");
		add(new Placement(four, five), tempRelations);
	}

	private void add(Relation relation, List<Relation> list) throws RoboticsException {
		relation.establish();
		frames.add(relation.getTo());
		list.add(relation);
	}

	@After
	public void deinit() {
		root = null;
	}

	@Test
	public void testGetConnectedFramesAndRelations() {
		Set<RoboticsEntity> result = WorldUtils.getConnectedFrames(root);

		assertSetContainsAllFrames(result);

		result = WorldUtils.getConnectedFrames(root, false);

		assertSetContainsOnlySpecifiedFrames(result, 0, 1, 4, 5);
	}

	@Test
	public void testGetConnectedFramesAndRelationsWithForbiddenRelations() {
		Set<RoboticsEntity> result = WorldUtils.getConnectedFrames(root,
				World.getCommandedTopology().withoutDynamic().withoutRelations(Arrays.asList(statRelations.get(0))));

		assertSetContainsOnlySpecifiedFrames(result, 4, 5);

		result = WorldUtils.getConnectedFrames(root, World.getCommandedTopology()
				.withoutRelations(Arrays.asList(statRelations.get(0), statRelations.get(2))));

		assertSetContainsOnlySpecifiedFrames(result, 2, 4, 5);
	}

	@Test
	public void testGetConnectedFramesAndRelationsWithForbiddenFrames() {
		Set<RoboticsEntity> result = WorldUtils.getConnectedFrames(root, true, frames.get(1), frames.get(3),
				frames.get(5));

		assertSetContainsOnlySpecifiedFrames(result, 0, 2, 4);

		result = WorldUtils.getConnectedFrames(root, false, frames.get(0));

		assertSetContainsOnlySpecifiedFrames(result, 4, 5);
	}

	private void assertSetContainsAllFrames(Set<RoboticsEntity> result) {
		Assert.assertTrue(result.containsAll(frames));
	}

	private void assertSetContainsOnlySpecifiedFrames(Set<RoboticsEntity> result, int... ids) {
		int count = 0;

		for (RoboticsEntity entity : result) {
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

	private void assertSetContainsOnlySpecifiedStaticConnections(Set<RoboticsEntity> result, int... ids) {
		int count = 0;

		for (RoboticsEntity entity : result) {
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
