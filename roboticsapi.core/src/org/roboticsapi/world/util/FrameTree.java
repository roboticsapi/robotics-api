/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.util.FrameGraph.FrameGraphAdapter;

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
	private final Set<Frame> knownFrames = new WeakEntitySet<Frame>();
	private final Map<Relation, WeakReference<Frame>> spanningTree = new LinkedHashMap<Relation, WeakReference<Frame>>();

	private final List<SpanningTreeListener> listeners = new ArrayList<SpanningTreeListener>();
	private final Object monitor = new Object();

	private final FrameGraphAdapter addListener = new FrameGraphAdapter() {

		private final Queue<Relation> queue = new LinkedList<Relation>();

		@Override
		public void onRelationAdded(Relation relation) {
			queue.add(relation);
		}

		@Override
		public void onFrameGraphUpdating() {
			notifyListenersOnChanging();
			queue.clear();
		}

		@Override
		public void onFrameGraphUpdated() {
			synchronized (monitor) {
				while (!queue.isEmpty()) {
					Relation relation = queue.poll();
					Frame frame = relation.getTo();

					// see if we know the first frame...
					if (knownFrames.contains(frame)) {
						frame = relation.getFrom();

						// see if we also know the second frame...
						if (knownFrames.contains(frame)) {
							// continue with the next relation in the queue
							continue;
						}
					}
					// check if frame is not null
					if (frame != null) {
						// mark the frame to be known
						knownFrames.add(frame);
						// add the relation into the spanning tree map
						spanningTree.put(relation, new WeakReference<Frame>(frame));

						// notify listeners...
						notifyListenersOnAdded(relation, frame);
					}
				}
			}

			notifyListenersOnChanged();
		}

	};

	private final FrameGraphAdapter removeListener = new FrameGraphAdapter() {

		private final Deque<Relation> queue = new LinkedList<Relation>();

		@Override
		public void onFrameGraphUpdating() {
			queue.clear();
		}

		@Override
		public void onFrameRemoved(Frame frame) {
			knownFrames.remove(frame);
		}

		@Override
		public void onRelationRemoved(Relation relation) {
			queue.push(relation);
		}

		@Override
		public void onFrameGraphUpdated() {
			synchronized (monitor) {
				// queue with open relations for updating the spanning tree
				Set<Frame> pendingFrames = new HashSet<Frame>();
				Deque<Relation> candidates = new LinkedList<Relation>();

				// Remove deprecated parts of the spanning tree
				while (!queue.isEmpty()) {
					Relation relation = queue.poll();
					WeakReference<Frame> ref = spanningTree.remove(relation);

					// in case the relation was never part of the spanning tree
					if (ref == null) {
						// potential candidate
						candidates.add(relation);
						continue;
					}
					// get the frame added to the spanning tree by the relation
					Frame frame = ref.get();

					// Case 1: if we do not know it anymore, just notify
					// listeners and continue...
					if (!knownFrames.contains(frame)) {
						notifyListenersOnRemoved(relation, frame);
						continue;
					}

					// Case 2: Frame is still part of the frame graph. Hence, we
					// need to rebuild a part of our spanning tree.
					List<Relation> relations = frame.getRelations();
					boolean relationsAdded = false;

					// First put it back into spanning tree
					spanningTree.put(relation, new WeakReference<Frame>(frame));
					queue.push(relation);

					// iterate over all relations to add the ones which are part
					// of
					// the spanning tree
					for (Relation r : relations) {
						if (spanningTree.containsKey(r)) {
							relationsAdded = true;
						}
						queue.push(r);
					}

					// if relations added, continue and visit it later again...
					if (relationsAdded) {
						continue;
					}

					// remove again from spanning tree..
					spanningTree.remove(relation);
					// remove it from the ToDo list
					queue.remove(relation);
					// notify listeners
					notifyListenersOnRemoved(relation, frame);
					// mark frame for later insertion into spanning tree
					pendingFrames.add(frame);
				}

				// Rebuild the spanning tree
				while (!candidates.isEmpty()) {
					Relation candidate = candidates.poll();
					Frame frame = candidate.getTo();

					if (!pendingFrames.contains(frame)) {
						frame = candidate.getFrom();

						if (!pendingFrames.contains(frame)) {
							continue;
						}
					}

					// add the relation into the spanning tree map
					spanningTree.put(candidate, new WeakReference<Frame>(frame));
					// notify listeners about new relation AND frame
					notifyListenersOnAdded(candidate, frame);

					Frame connectedFrame;
					// visit all connected frames
					for (Relation r : frame.getRelations()) {
						// only add frames, which are present in pendingFrames
						connectedFrame = r.getFrom();
						if (connectedFrame == frame) {
							connectedFrame = r.getTo();
						}

						if (r != candidate && pendingFrames.contains(connectedFrame)) {
							candidates.add(r);
						}
					}
				}
			}
		}

	};

	public FrameTree(FrameGraph graph) {
		if (graph == null) {
			throw new IllegalArgumentException("Frame graph must be not null.");
		}
		this.graph = graph;
		knownFrames.add(graph.getRoot());

		this.graph.addFrameGraphListener(addListener);
		this.graph.addFrameGraphListener(removeListener);
	}

	public Set<Relation> getSpanningTreeEdges() {
		return spanningTree.keySet();
	}

	public Collection<WeakReference<Frame>> getSpanningTreeVertices() {
		return spanningTree.values();
	}

	@Override
	protected void finalize() throws Throwable {
		synchronized (monitor) {
			this.graph.removeFrameGraphListener(addListener);
			this.graph.removeFrameGraphListener(removeListener);
		}
	}

	public void addSpanningTreeListener(SpanningTreeListener l) {
		synchronized (monitor) {
			listeners.add(l);

			Set<Entry<Relation, WeakReference<Frame>>> set = this.spanningTree.entrySet();

			if (!set.isEmpty()) {
				l.onSpanningTreeUpdating();

				for (Entry<Relation, WeakReference<Frame>> entry : set) {
					Frame frame = entry.getValue().get();
					Relation relation = entry.getKey();
					Frame parent = relation.getOther(frame);
					l.onFrameAdded(frame, relation, parent);
				}
				l.onSpanningTreeUpdated();
			}
		}
	}

	public void removeSpanningTreeListener(SpanningTreeListener l) {
		synchronized (monitor) {
			listeners.remove(l);

			List<Entry<Relation, WeakReference<Frame>>> list = new ArrayList<Entry<Relation, WeakReference<Frame>>>(
					spanningTree.entrySet());
			Collections.reverse(list);

			if (!list.isEmpty()) {
				l.onSpanningTreeUpdating();

				for (Entry<Relation, WeakReference<Frame>> entry : list) {
					Frame frame = entry.getValue().get();
					Relation relation = entry.getKey();
					Frame parent = relation.getOther(frame);
					l.onFrameRemoved(frame, relation, parent);
				}
				l.onSpanningTreeUpdated();
			}
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
		Iterator<Relation> set = spanningTree.keySet().iterator();

		while (set.hasNext()) {
			s += set.next().getName();
			if (set.hasNext()) {
				s += ", ";
			}
		}
		return s;
	}
}
