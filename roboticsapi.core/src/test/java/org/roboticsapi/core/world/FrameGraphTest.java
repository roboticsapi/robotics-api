/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.RoboticsEntity;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.world.relation.ConfiguredStaticConnection;
import org.roboticsapi.core.world.util.FrameGraph;
import org.roboticsapi.core.world.util.FrameGraph.FrameGraphAdapter;

public class FrameGraphTest {

	Frame[] frames = new Frame[7];
	ConfiguredStaticConnection[] relations = new ConfiguredStaticConnection[7];

	private static class CheckingListenener extends FrameGraphAdapter {
		private final Frame root;

		public CheckingListenener(Frame root) {
			this.root = root;
		}

		Set<Frame> knownFrames = new HashSet<>();
		Set<Relation> seenRelations = new HashSet<>();

		@Override
		public void onRelationRemoved(Relation relation) {
			if (!knownFrames.contains(relation.getFrom()) || !knownFrames.contains(relation.getTo())) {
				throw new IllegalArgumentException();
			}
			if (!seenRelations.contains(relation)) {
				throw new IllegalArgumentException();
			}
			seenRelations.remove(relation);
		}

		@Override
		public void onRelationAdded(Relation relation) {
			if (!knownFrames.contains(relation.getFrom()) || !knownFrames.contains(relation.getTo())) {
				throw new IllegalArgumentException();
			}
			if (seenRelations.contains(relation)) {
				throw new IllegalArgumentException();
			}
			boolean allowed = false;
			if (relation.getFrom() == root || relation.getTo() == root) {
				allowed = true;
			}
			for (Relation r : seenRelations) {
				if (r.getFrom() == relation.getFrom() || r.getFrom() == relation.getTo()
						|| r.getTo() == relation.getFrom() || r.getTo() == relation.getTo()) {
					allowed = true;
				}
			}
			if (!allowed) {
				throw new IllegalArgumentException();
			}
			seenRelations.add(relation);
		}

		@Override
		public void onFrameRemoved(Frame frame) {
			if (!knownFrames.contains(frame)) {
				throw new IllegalArgumentException();
			}
			for (Relation r : seenRelations) {
				if (frame == r.getFrom() || frame == r.getTo()) {
					throw new IllegalArgumentException();
				}
			}
			knownFrames.remove(frame);
		}

		@Override
		public void onFrameGraphUpdating() {
		}

		@Override
		public void onFrameGraphUpdated() {
		}

		@Override
		public void onFrameAdded(Frame frame) {
			if (knownFrames.contains(frame)) {
				throw new IllegalArgumentException();
			}
			knownFrames.add(frame);
		}
	}

	private static abstract class FrameGraphTestListener<E extends RoboticsEntity> extends FrameGraphAdapter {

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
			context.initialize(frames[i]);
			relations[i] = new ConfiguredStaticConnection();
			relations[i].setName("Relation " + i);
		}

	}

	private void addFrames() throws InitializationException {
		addRelation(root, frames[0], relations[0]);
		addRelation(frames[0], frames[1], relations[1]);
		addRelation(frames[1], frames[2], relations[2]);
		addRelation(frames[1], frames[3], relations[3]);
		addRelation(frames[2], frames[4], relations[4]);
		addRelation(frames[3], frames[5], relations[5]);
		addRelation(frames[6], frames[0], relations[6]);
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
		CheckingListenener checkingListener = new CheckingListenener(root);

		graph = new FrameGraph(root);
		graph.addFrameGraphListener(checkingListener);
		graph.addFrameGraphListener(addListener);
		graph.addFrameGraphListener(remListener);

		addFrames();
		removeFrames();

		addListener.test();
		remListener.test();
		graph.removeFrameGraphListener(checkingListener);
	}

	@Test
	public void testAddingAndRemovingSingleFrame() throws InitializationException {
		Frame frame = new Frame();
		ConfiguredStaticConnection relation = new ConfiguredStaticConnection();

		TestFramesAddedListener addListener = new TestFramesAddedListener(frames, frame, root);
		TestFramesRemovedListener remListener = new TestFramesRemovedListener(frame);
		CheckingListenener checkingListener = new CheckingListenener(root);

		graph = new FrameGraph(root);
		addFrames();

		graph.addFrameGraphListener(checkingListener);
		graph.addFrameGraphListener(addListener);
		graph.addFrameGraphListener(remListener);

		addRelation(frames[5], frame, relation);
		context.uninitialize(relation);

		addListener.test();
		remListener.test();
		graph.removeFrameGraphListener(checkingListener);
	}

	private void addRelation(Frame from, Frame to, ConfiguredStaticConnection relation) throws InitializationException {
		relation.setFrom(from);
		relation.setTo(to);
		context.initialize(relation);
	}

	@Test
	public void testNotifyingFrameGraphListeners() throws InitializationException {
		TestFramesAddedListener addListener = new TestFramesAddedListener(frames, root);
		TestFramesRemovedListener remListener = new TestFramesRemovedListener(frames);
		CheckingListenener checkingListener = new CheckingListenener(root);

		graph = new FrameGraph(root);

		addFrames();

		graph.addFrameGraphListener(checkingListener);
		graph.addFrameGraphListener(addListener);
		graph.addFrameGraphListener(remListener);

		removeFrames();

		addListener.test();
		remListener.test();
		graph.removeFrameGraphListener(checkingListener);
	}

}
