/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.world.relation.StaticConnection;
import org.roboticsapi.core.world.util.FrameGraph;
import org.roboticsapi.core.world.util.FrameGraph.FrameGraphListener;
import org.roboticsapi.core.world.util.FrameTree;
import org.roboticsapi.core.world.util.FrameTree.SpanningTreeListener;

public class FrameTreeTest {

	Frame root;
	FrameGraph graph;
	FrameTree tree;
	TestFrameGraphListener listener;

	/**
	 * Frames added by the FrameGraphListener
	 */
	protected final Set<Frame> sceneGraphFrames = new HashSet<Frame>();

	/**
	 * Relations added by the FrameGraphListener
	 */
	protected final Set<Relation> sceneGraphRelations = new HashSet<Relation>();

	/**
	 * Frames added by the SpanningTreeListener
	 */
	protected final Set<Frame> spanningFrames = new HashSet<Frame>();

	Frame[] frames = new Frame[15];
	LogicalRelation[] relations = new LogicalRelation[20];

	private final RoboticsContext context;

	public FrameTreeTest() {
		context = new RoboticsContextImpl("dummy");
	}

	@Before
	public void init() throws InitializationException {
		root = new Frame("root");
		context.initialize(root);

		addFrames();

		graph = new FrameGraph(root);
		tree = new FrameTree(graph);
		listener = new TestFrameGraphListener();
		tree.addSpanningTreeListener(listener);
		graph.addFrameGraphListener(listener);

	}

	private void addFrames() {
		for (int i = 0; i < frames.length; i++) {
			frames[i] = new Frame("Frame " + i);
		}
	}

	LogicalRelation addRelation(Frame from, Frame to) throws InitializationException {
		StaticConnection ret = new StaticConnection(from, to);
		ret.establish();
		return ret;
	}

	void removeRelation(LogicalRelation relation) throws InitializationException {
		relation.remove();
	}

	/**
	 * Tests adding and removing of frames. Also checks, if listeners are called
	 * properly.
	 *
	 * @throws InitializationException
	 */
	@Test
	public void testAddFramesAndDeleteFrames() throws InitializationException {
		// System.out.println("Begin AddFramesAndDeleteFrames test.");
		ArrayList<ArrayList<LogicalRelation>> expected = new ArrayList<ArrayList<LogicalRelation>>();

		relations[0] = addRelation(root, frames[0]);
		Assert.assertTrue(spanningFrames.size() == 1);
		expected.add(new ArrayList<LogicalRelation>(Arrays.asList(relations[0])));
		assertSpanningTreeRelations(expected);

		relations[1] = addRelation(frames[0], frames[1]);
		Assert.assertTrue(spanningFrames.size() == 2);
		expected.clear();
		expected.add(new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[1])));
		assertSpanningTreeRelations(expected);

		relations[2] = addRelation(frames[1], frames[2]);
		Assert.assertTrue(spanningFrames.size() == 3);
		expected.clear();
		expected.add(new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[1], relations[2])));
		assertSpanningTreeRelations(expected);

		relations[3] = addRelation(frames[1], frames[3]);
		Assert.assertTrue(spanningFrames.size() == 4);
		expected.clear();
		expected.add(
				new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[1], relations[2], relations[3])));
		assertSpanningTreeRelations(expected);

		relations[4] = addRelation(frames[2], frames[4]);
		Assert.assertTrue(spanningFrames.size() == 5);
		expected.clear();
		expected.add(new ArrayList<LogicalRelation>(
				Arrays.asList(relations[0], relations[1], relations[2], relations[3], relations[4])));
		assertSpanningTreeRelations(expected);

		relations[5] = addRelation(frames[3], frames[5]);
		Assert.assertTrue(spanningFrames.size() == 6);
		expected.clear();
		expected.add(new ArrayList<LogicalRelation>(
				Arrays.asList(relations[0], relations[1], relations[2], relations[3], relations[4], relations[5])));
		assertSpanningTreeRelations(expected);

		relations[6] = addRelation(frames[0], frames[6]);
		Assert.assertTrue(spanningFrames.size() == 7);
		expected.clear();
		expected.add(new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[1], relations[2],
				relations[3], relations[4], relations[5], relations[6])));
		assertSpanningTreeRelations(expected);

		removeRelation(relations[6]);
		Assert.assertTrue(spanningFrames.size() == 6);
		expected.clear();
		expected.add(new ArrayList<LogicalRelation>(
				Arrays.asList(relations[0], relations[1], relations[2], relations[3], relations[4], relations[5])));
		assertSpanningTreeRelations(expected);

		removeRelation(relations[2]);
		Assert.assertTrue(spanningFrames.size() == 4);
		expected.clear();
		expected.add(
				new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[1], relations[3], relations[5])));
		assertSpanningTreeRelations(expected);

		removeRelation(relations[0]);
		Assert.assertTrue(spanningFrames.size() == 0);

		// System.out.println("End AddFramesAndDeleteFrames test.");
	}

	/**
	 * This test creates multiple loops and removes them again to check, whether the
	 * spanning tree is rebuilt correctly.
	 *
	 * @throws InitializationException
	 */
	@Test
	public void testCreateLoopAndRemoveRelation() throws InitializationException {
		// System.out.println("Begin CreateSimpleLoopAndRemoveRelation test.");
		ArrayList<ArrayList<LogicalRelation>> expected = new ArrayList<ArrayList<LogicalRelation>>();

		//
		// Create a graph with a simple loop (triangle)
		//
		relations[0] = addRelation(root, frames[0]);
		relations[1] = addRelation(frames[0], frames[1]);
		relations[2] = addRelation(frames[0], frames[2]);
		relations[3] = addRelation(frames[2], frames[1]);
		removeRelation(relations[2]);

		// Expect spanning tree containing the edges 0, 1 and 3
		expected.add(new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[1], relations[3])));
		assertSpanningTreeRelations(expected);

		addFrames();

		//
		// Create a graph with two loops
		//
		relations[0] = addRelation(root, frames[0]);
		relations[1] = addRelation(frames[0], frames[1]);
		relations[2] = addRelation(frames[0], frames[2]);
		relations[3] = addRelation(frames[0], frames[3]);
		relations[4] = addRelation(frames[1], frames[2]);
		relations[5] = addRelation(frames[2], frames[3]);
		removeRelation(relations[2]);

		// There are 4 possibilities to create a spanning tree
		expected.clear();
		expected.add(
				new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[1], relations[4], relations[5])));
		expected.add(
				new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[1], relations[4], relations[3])));
		expected.add(
				new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[1], relations[3], relations[5])));
		expected.add(
				new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[3], relations[4], relations[5])));
		assertSpanningTreeRelations(expected);

		addFrames();

		//
		// Create a graph with a loop along multiple vertices (indirect loop)
		//
		relations[0] = addRelation(root, frames[0]);
		relations[1] = addRelation(frames[0], frames[1]);
		relations[2] = addRelation(frames[0], frames[2]);
		relations[3] = addRelation(frames[0], frames[3]);
		relations[4] = addRelation(frames[1], frames[4]);
		relations[5] = addRelation(frames[2], frames[5]);
		relations[6] = addRelation(frames[4], frames[5]);
		removeRelation(relations[4]);

		// Only solution for a spanning tree is 0,1,2,3,5,6
		expected.clear();
		expected.add(new ArrayList<LogicalRelation>(
				Arrays.asList(relations[0], relations[1], relations[2], relations[3], relations[5], relations[6])));

		assertSpanningTreeRelations(expected);

		// System.out.println("End CreateSimpleLoopAndRemoveRelation test.");
	}

	/**
	 * Spanning tree must look like one of the given sets.
	 *
	 * @param relations allowed spanning tree variations
	 */
	private void assertSpanningTreeRelations(ArrayList<ArrayList<LogicalRelation>> relations) {
		Collection<Relation> sTreeRels = tree.getSpanningTreeEdges();
		boolean match = true;

		for (List<LogicalRelation> list : relations) {
			match = true;
			for (Relation rel : list) {
				if (!sTreeRels.contains(rel)) {
					match = false;
					break;
				}
			}
			if (match) {
				break;
			}
		}

		Assert.assertTrue("Spanning tree not like expected.", match);
	}

	@Test
	public void testCreateTreeAndRemoveSubTree() throws InitializationException {

		// System.out.println("Begin CreateSimpleLoopAndRemoveRelation test.");
		relations[0] = addRelation(root, frames[0]);
		relations[1] = addRelation(frames[0], frames[1]);
		relations[2] = addRelation(frames[1], frames[2]);
		relations[3] = addRelation(frames[1], frames[3]);
		relations[4] = addRelation(frames[2], frames[4]);
		relations[5] = addRelation(frames[3], frames[5]);
		relations[6] = addRelation(frames[0], frames[6]);

		ArrayList<ArrayList<LogicalRelation>> expected = new ArrayList<ArrayList<LogicalRelation>>();
		expected.add(new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[1], relations[2],
				relations[3], relations[4], relations[5], relations[6])));
		assertSpanningTreeRelations(expected);

		removeRelation(relations[1]);
		expected.clear();
		expected.add(new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[6])));
		assertSpanningTreeRelations(expected);

		// System.out.println("End CreateSimpleLoopAndRemoveRelation test.");
	}

	/**
	 * This test simulates two robots handing over an object.
	 *
	 * @throws InitializationException
	 */
	@Test
	public void testHandOverSubTree() throws InitializationException {
		ArrayList<ArrayList<LogicalRelation>> expected = new ArrayList<ArrayList<LogicalRelation>>();

		//
		// Create a simple handover scenario with two branches passing one frame
		//
		relations[0] = addRelation(root, frames[0]);
		// Branch 1
		relations[1] = addRelation(frames[0], frames[1]);
		relations[2] = addRelation(frames[1], frames[2]);
		// Branch 2
		relations[3] = addRelation(frames[0], frames[3]);
		relations[4] = addRelation(frames[3], frames[4]);
		// Object
		relations[5] = addRelation(frames[4], frames[5]);

		// Attach Branch 1 to Object
		relations[6] = addRelation(frames[2], frames[5]);

		// Detach Branch 2 from Object
		removeRelation(relations[5]);

		// Expect spanning tree containing the edges 0,1,2,3,4,6
		expected.add(new ArrayList<LogicalRelation>(
				Arrays.asList(relations[0], relations[1], relations[2], relations[3], relations[4], relations[6])));
		assertSpanningTreeRelations(expected);

		addFrames();

		//
		// Create a simple handover scenario with two branches passing a subtree
		//
		relations[0] = addRelation(root, frames[0]);
		// Branch 1
		relations[1] = addRelation(frames[0], frames[1]);
		relations[2] = addRelation(frames[1], frames[2]);
		// Branch 2
		relations[3] = addRelation(frames[0], frames[3]);
		relations[4] = addRelation(frames[3], frames[4]);
		// Object
		relations[5] = addRelation(frames[4], frames[5]);
		relations[6] = addRelation(frames[5], frames[6]);
		relations[7] = addRelation(frames[5], frames[7]);

		// Attach Branch 1 to Object
		relations[8] = addRelation(frames[2], frames[7]);

		// TODO loop in FrameTree.onFrameGraphUpdated()
		// Detach Branch 2 from Object
		removeRelation(relations[5]);

		// Expect spanning tree containing the edges 0,1,2,3,4,6,7,8
		expected.add(new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[1], relations[2],
				relations[3], relations[4], relations[6], relations[7], relations[8])));
		assertSpanningTreeRelations(expected);

		addFrames();

		//
		// Create a handover scenario with two branches passing a subtree
		// containing a loop
		//
		relations[0] = addRelation(root, frames[0]);
		// Branch 1
		relations[1] = addRelation(frames[0], frames[1]);
		relations[2] = addRelation(frames[1], frames[2]);
		// Branch 2
		relations[3] = addRelation(frames[0], frames[3]);
		relations[4] = addRelation(frames[3], frames[4]);
		// Object
		relations[5] = addRelation(frames[4], frames[5]);
		relations[6] = addRelation(frames[5], frames[6]);
		relations[7] = addRelation(frames[5], frames[7]);
		relations[8] = addRelation(frames[7], frames[6]);

		// Attach Branch 1 to Object
		relations[9] = addRelation(frames[2], frames[6]);

		// Detach Branch 2 from Object
		removeRelation(relations[5]);

		// Expect spanning tree containing the edges 0,1,2,3,4,6,7,8,9
		expected.add(new ArrayList<LogicalRelation>(Arrays.asList(relations[0], relations[1], relations[2],
				relations[3], relations[4], relations[6], relations[7], relations[8], relations[9])));
		assertSpanningTreeRelations(expected);

	}

	@After
	public void deinit() throws InitializationException {
		root = null;
		graph = null;

		for (int i = 0; i < frames.length; i++) {
			frames[i] = null;
		}
		for (int i = 0; i < relations.length; i++) {
			relations[i] = null;
		}
	}

	public class TestFrameGraphListener implements FrameGraphListener, SpanningTreeListener {

		@Override
		public void onFrameGraphUpdating() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFrameAdded(Frame frame) {
			if (!frame.equals(root)) {
				sceneGraphFrames.add(frame);
			}
		}

		@Override
		public void onRelationAdded(Relation relation) {
			sceneGraphRelations.add(relation);
		}

		@Override
		public void onFrameRemoved(Frame frame) {
			sceneGraphFrames.remove(frame);
		}

		@Override
		public void onRelationRemoved(Relation relation) {
			sceneGraphRelations.remove(relation);
		}

		@Override
		public void onFrameGraphUpdated() {
			// TODO not working; this method does not guarantee a consistent
			// spanning tree. why?
			// Some basic asserts which must always be satisfied
			// assertVerticeSizes();
			// assertVertices();
			// assertEdges();
		}

		@Override
		public void onSpanningTreeUpdating() {
		}

		@Override
		public void onFrameAdded(Frame frame, Relation relation, Frame parent) {
			// System.out.println("Connected " + frame.getName() + " to "
			// + parent.getName() + " by " + relation.getName());

			spanningFrames.add(frame);
			// spanningRelations.add(relation);
		}

		@Override
		public void onFrameRemoved(Frame frame, Relation relation, Frame parent) {
			// System.out.println("Disconnected " + frame.getName() + " from "
			// + parent.getName() + " by " + relation.getName());

			spanningFrames.remove(frame);
			// spanningRelations.remove(relation);
		}

		@Override
		public void onSpanningTreeUpdated() {
			// System.out.println(tree.toStringSpanningTree());
		}

		/**
		 * Number of vertices in spanning tree must always match number of vertices in
		 * frame graph
		 */
		private void assertVerticeSizes() {
			int frameSize = sceneGraphFrames.size();
			int spanningTreeFrameSize = tree.getSpanningTreeEdges().size();
			// Assert.assertTrue(
			// "Number of vertices in spanning tree and frame graph is not
			// equal.",
			// frameSize == spanningTreeFrameSize);
		}

		/**
		 * Set of vertices(Frames) in spanning tree must equal the set of vertices in
		 * frame graph
		 */
		private void assertVertices() {
			for (Frame f : tree.getSpanningTreeVertices()) {
				Assert.assertTrue("Vertex sets(spanning tree and frame graph) are not equal.",
						sceneGraphFrames.contains(f));
			}
		}

		/**
		 * Set of edges(Relations) in spanning tree must be part of the set of edges in
		 * frame graph
		 */
		private void assertEdges() {
			for (Relation r : tree.getSpanningTreeEdges()) {
				Assert.assertTrue("Edge sets(spanning tree and frame graph) are not equal.",
						sceneGraphRelations.contains(r));
			}
		}
	}

}