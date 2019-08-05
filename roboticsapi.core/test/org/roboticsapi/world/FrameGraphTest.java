/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.util.FrameGraph;
import org.roboticsapi.world.util.FrameGraph.FrameGraphAdapter;

public class FrameGraphTest {

	Frame[] frames = new Frame[7];
	Relation[] relations = new Relation[7];

	private static abstract class FrameGraphTestListener<E extends Entity> extends FrameGraphAdapter {

		protected final Set<E> expected = new HashSet<E>();

		public FrameGraphTestListener(E[] elements, E... additionals) {
			Collections.addAll(expected, elements);
			Collections.addAll(expected, additionals);
		}

		public void test() {
			Assert.assertTrue(expected.isEmpty());
		}

	}

	private static final class TestFramesAddedListener extends FrameGraphTestListener<Frame> {

		public TestFramesAddedListener(Frame[] all, Frame... frames) {
			super(all, frames);
		}

		@Override
		public void onFrameAdded(Frame frame) {
			boolean remove = expected.remove(frame);
			Assert.assertTrue(remove);
		}

	}

	private static final class TestFramesRemovedListener extends FrameGraphTestListener<Frame> {

		public TestFramesRemovedListener(Frame[] all, Frame... frames) {
			super(all, frames);
		}

		public TestFramesRemovedListener(Frame... frames) {
			super(new Frame[0], frames);
		}

		@Override
		public void onFrameRemoved(Frame frame) {
			boolean remove = expected.remove(frame);
			Assert.assertTrue(remove);
		}

	}

	Frame root;
	FrameGraph graph;

	private final RoboticsContext context;

	public FrameGraphTest() {
		context = new RoboticsContextImpl("dummy");
	}

	@Before
	public void init() throws InitializationException {
		root = new Frame("root");
		context.initialize(root);

		for (int i = 0; i < 7; i++) {
			frames[i] = new Frame("Frame " + i);
			relations[i] = new StaticConnection();
			relations[i].setName("Relation " + i);
		}

	}

	private void addFrames() throws InitializationException {
		root.addRelation(relations[0], frames[0]);
		frames[0].addRelation(relations[1], frames[1]);
		frames[1].addRelation(relations[2], frames[2]);
		frames[1].addRelation(relations[3], frames[3]);
		frames[2].addRelation(relations[4], frames[4]);
		frames[3].addRelation(relations[5], frames[5]);

		frames[0].addRelation(relations[6], frames[6]);
	}

	private void removeFrames() throws InitializationException {
		for (int i = 0; i < 7; i++) {
			context.uninitialize(relations[i]);
		}
	}

	@After
	public void deinit() throws InitializationException {
		// root.uninitialize();
		root = null;
		graph = null;

		for (int i = 0; i < 7; i++) {
			frames[i] = null;
			relations[i] = null;
		}
	}

	@Test
	public void testAddingAndRemovingAllFrames() throws InitializationException {
		TestFramesAddedListener addListener = new TestFramesAddedListener(frames, root);
		TestFramesRemovedListener remListener = new TestFramesRemovedListener(frames);

		graph = new FrameGraph(root);
		graph.addFrameGraphListener(addListener);
		graph.addFrameGraphListener(remListener);

		addFrames();
		removeFrames();

		addListener.test();
		remListener.test();
	}

	@Test
	public void testAddingAndRemovingSingleFrame() throws InitializationException {
		Frame frame = new Frame();
		StaticConnection relation = new StaticConnection();

		TestFramesAddedListener addListener = new TestFramesAddedListener(frames, frame, root);
		TestFramesRemovedListener remListener = new TestFramesRemovedListener(frame);

		graph = new FrameGraph(root);
		addFrames();

		graph.addFrameGraphListener(addListener);
		graph.addFrameGraphListener(remListener);

		frames[5].addRelation(relation, frame);
		context.uninitialize(relation);

		addListener.test();
		remListener.test();
	}

	@Test
	public void testNotifyingFrameGraphListeners() throws InitializationException {
		TestFramesAddedListener addListener = new TestFramesAddedListener(frames, root);
		TestFramesRemovedListener remListener = new TestFramesRemovedListener(frames);

		graph = new FrameGraph(root);

		addFrames();

		graph.addFrameGraphListener(addListener);
		graph.addFrameGraphListener(remListener);

		removeFrames();

		addListener.test();
		remListener.test();
	}

}
