/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.util.FrameGraph.FrameGraphListener;

/**
 * Spanning tree for {@link Frame}s based on a {@link FrameGraph}.
 */
public class FrameTree {

	/**
	 * Listener for spanning tree events.
	 */
	public static interface SpanningTreeListener {

		/**
		 * Invoked before the spanning tree is changed due to added or removed
		 * relations.
		 */
		void onSpanningTreeUpdating();

		/**
		 * Receives notification that a frame was added to the spanning tree of the
		 * frame graph.
		 *
		 * @param frame    The added frame
		 * @param relation The relation adding the frame to the spanning tree
		 * @param parent   The spanning tree's frame the newly added frame is connected
		 *                 to
		 */
		void onFrameAdded(Frame frame, Relation relation, Frame parent);

		/**
		 * Receives notification that a frame was removed from the spanning tree of the
		 * frame graph.
		 *
		 * @param frame    The frame that was removed
		 * @param relation The relation removing the frame from the spanning tree
		 * @param parent   The former parent
		 */
		void onFrameRemoved(Frame frame, Relation relation, Frame parent);

		/**
		 * Invoked after the spanning tree was changed due to added or removed
		 * relations.
		 */
		void onSpanningTreeUpdated();

	}

	/**
	 * Adapter for {@link SpanningTreeListener}s.
	 */
	public static abstract class SpanningTreeAdapter implements SpanningTreeListener {

		@Override
		public void onSpanningTreeUpdating() {
			// do nothing
		}

		@Override
		public void onFrameAdded(Frame frame, Relation relation, Frame parent) {
			// do nothing
		}

		@Override
		public void onFrameRemoved(Frame frame, Relation relation, Frame parent) {
			// do nothing
		}

		@Override
		public void onSpanningTreeUpdated() {
			// do nothing
		}

	}

	private final FrameGraph graph;

	private final Map<Frame, Relation> parents = new HashMap<>();
	private final Map<Frame, Set<Relation>> relations = new HashMap<>();

	private final List<SpanningTreeListener> listeners = new ArrayList<SpanningTreeListener>();
	private final Object monitor = new Object();

	private final FrameGraphListener listener = new FrameGraphListener() {

		private final Object monitor = new Object();

		@Override
		public void onRelationAdded(Relation relation) {
			synchronized (monitor) {
				// add relation to our list
				getRelationList(relation.getFrom()).add(relation);
				getRelationList(relation.getTo()).add(relation);
				if (isKnown(relation.getFrom()) && isKnown(relation.getTo())) {
					// if we know both from and to... so nothing to do
				} else if (isKnown(relation.getFrom())) {
					// if it provides a new 'to' frame
					parents.put(relation.getTo(), relation);
					notifyListenersOnAdded(relation, relation.getTo());
				} else if (isKnown(relation.getTo())) {
					// if it provides a new 'from' frame
					parents.put(relation.getFrom(), relation);
					notifyListenersOnAdded(relation, relation.getFrom());
				} else {
					throw new IllegalStateException("Received a relation we know neither from nor to for");
				}
			}
		}

		@Override
		public void onRelationRemoved(Relation relation) {
			synchronized (monitor) {
				// remove relation from our list
				getRelationList(relation.getFrom()).remove(relation);
				getRelationList(relation.getTo()).remove(relation);

				if (parents.get(relation.getFrom()) == relation) {
					// if it was our parent relation for its from frame...
					handleRemove(relation.getFrom(), relation);
				} else if (parents.get(relation.getTo()) == relation) {
					// if it was our parent relation for its to frame...
					handleRemove(relation.getTo(), relation);
				}
			}
		}

		private void handleRemove(Frame frame, Relation parentRelation) {
			Frame parent = parentRelation.getOther(frame);
			// if we find another relation to the same parent, switch to it
			for (Relation r : getRelationList(frame)) {
				if (r.getOther(frame) == parent) {
					notifyListenersOnRemoved(parentRelation, frame);
					notifyListenersOnAdded(r, frame);
					return;
				}
			}
			// if we find our children
			List<Frame> children = new ArrayList<>();
			collectChildren(frame, children);

			// if we have a relation that does not go to our children, rewire to there
			for (Relation r : getRelationList(frame)) {
				if (!children.contains(r.getOther(frame))) {
					notifyListenersOnRemoved(parentRelation, frame);
					notifyListenersOnAdded(r, frame);
					parents.put(frame, r);
					return;
				}
			}

			// otherwise remove us and all children
			for (Frame f : children) {
				notifyListenersOnRemoved(parents.remove(f), f);
				relations.remove(f);
			}
		}

		@Override
		public void onFrameRemoved(Frame frame) {
			// do nothing
		}

		@Override
		public void onFrameAdded(Frame frame) {
			// do nothing
		}

		@Override
		public void onFrameGraphUpdating() {
			synchronized (monitor) {
				notifyListenersOnChanging();
			}
		}

		@Override
		public void onFrameGraphUpdated() {
			synchronized (monitor) {
				notifyListenersOnChanged();
			}
		}

		private Set<Relation> getRelationList(Frame frame) {
			if (!relations.containsKey(frame)) {
				relations.put(frame, new HashSet<>());
			}
			return relations.get(frame);
		}

		private void collectChildren(Frame frame, List<Frame> children) {
			children.add(frame);
			for (Relation r : getRelationList(frame)) {
				if (parents.get(r.getOther(frame)) == r) {
					collectChildren(r.getOther(frame), children);
				}
			}
		}

		private boolean isKnown(Frame frame) {
			return frame == graph.getRoot() || parents.containsKey(frame);
		}

	};

	public FrameTree(FrameGraph graph) {
		if (graph == null) {
			throw new IllegalArgumentException("Frame graph must be not null.");
		}
		this.graph = graph;

		this.graph.addFrameGraphListener(listener);
	}

	public Collection<Relation> getSpanningTreeEdges() {
		return parents.values();
	}

	public Collection<Frame> getSpanningTreeVertices() {
		return parents.keySet();
	}

	@Override
	protected void finalize() throws Throwable {
		synchronized (monitor) {
			this.graph.removeFrameGraphListener(listener);
		}
	}

	public void addSpanningTreeListener(SpanningTreeListener l) {
		synchronized (monitor) {
			listeners.add(l);
			// collect all tree items and notify in the right order
			Queue<Frame> todo = new LinkedList<>(parents.keySet());
			Set<Frame> seen = new HashSet<Frame>();
			seen.add(graph.getRoot());
			l.onSpanningTreeUpdating();
			while (!todo.isEmpty()) {
				Frame frame = todo.poll();
				Relation relation = parents.get(frame);
				Frame parent = relation.getOther(frame);
				if (seen.contains(parent)) {
					l.onFrameAdded(frame, relation, parent);
					seen.add(frame);
				} else {
					todo.add(frame);
				}
			}
			l.onSpanningTreeUpdated();
		}
	}

	public void removeSpanningTreeListener(SpanningTreeListener l) {
		synchronized (monitor) {
			listeners.remove(l);

			// collect all tree items and remove in the opposite order
			Queue<Frame> todo = new LinkedList<>(parents.keySet());
			List<Frame> seen = new ArrayList<Frame>();
			seen.add(graph.getRoot());
			l.onSpanningTreeUpdating();
			while (!todo.isEmpty()) {
				Frame frame = todo.poll();
				Relation relation = parents.get(frame);
				Frame parent = relation.getOther(frame);
				if (seen.contains(parent)) {
					l.onFrameAdded(frame, relation, parent);
					seen.add(frame);
				} else {
					todo.add(frame);
				}
			}
			Collections.reverse(seen);
			l.onSpanningTreeUpdating();
			for (Frame frame : seen) {
				Relation relation = parents.get(frame);
				if (relation == null) {
					continue;
				}
				Frame parent = relation.getOther(frame);
				l.onFrameRemoved(frame, relation, parent);
			}
			l.onSpanningTreeUpdated();
		}
	}

	@Override
	public String toString() {
		return "Frame tree with root '" + this.graph.getRoot().getName() + "'";
	}

	protected void notifyListenersOnChanging() {
		for (SpanningTreeListener l : new ArrayList<SpanningTreeListener>(this.listeners)) {
			l.onSpanningTreeUpdating();
		}
	}

	protected void notifyListenersOnAdded(Relation relation, Frame frame) {
		Frame parent = relation.getOther(frame);

		for (SpanningTreeListener l : new ArrayList<SpanningTreeListener>(this.listeners)) {
			l.onFrameAdded(frame, relation, parent);
		}
	}

	protected void notifyListenersOnRemoved(Relation relation, Frame frame) {
		Frame parent = null;

		if (frame != null) {
			parent = relation.getOther(frame);
		} else {
			parent = relation.getFrom();
		}

		for (SpanningTreeListener l : new ArrayList<SpanningTreeListener>(this.listeners)) {
			l.onFrameRemoved(frame, relation, parent);
		}
	}

	protected void notifyListenersOnChanged() {
		for (SpanningTreeListener l : new ArrayList<SpanningTreeListener>(this.listeners)) {
			l.onSpanningTreeUpdated();
		}
	}

	public String toStringSpanningTree() {
		String s = "";
		Iterator<Relation> set = parents.values().iterator();

		while (set.hasNext()) {
			s += set.next();
			if (set.hasNext()) {
				s += ", ";
			}
		}
		return s;
	}
}
