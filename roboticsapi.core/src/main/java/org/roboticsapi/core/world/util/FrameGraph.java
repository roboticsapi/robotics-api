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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.RelationListener;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;

public class FrameGraph {

	/**
	 * Listener for frame graph events.
	 */
	public static interface FrameGraphListener {

		/**
		 * Invoked before the frame graph is changed due to an added or removed
		 * relation.
		 */
		void onFrameGraphUpdating();

		/**
		 * Invoked when a new frame is added to the frame graph.
		 *
		 * @param frame the added frame
		 */
		void onFrameAdded(Frame frame);

		/**
		 * Invoked when a new relation is added to the frame graph.
		 *
		 * @param relation the added relation
		 */
		void onRelationAdded(Relation relation);

		/**
		 * Invoked when a frame is removed from the frame graph.
		 *
		 * @param frame the removed frame
		 */
		void onFrameRemoved(Frame frame);

		/**
		 * Invoked when a relation is removed from the frame graph.
		 *
		 * @param relation the removed relation
		 */
		void onRelationRemoved(Relation relation);

		/**
		 * Invoked after the frame graph was changed due to an added or removed
		 * relation.
		 */
		void onFrameGraphUpdated();

	}

	/**
	 * Adapter class for {@link FrameGraphListener}.
	 */
	public static abstract class FrameGraphAdapter implements FrameGraphListener {

		@Override
		public void onFrameGraphUpdating() {
			// do nothing
		}

		@Override
		public void onFrameAdded(Frame frame) {
			// do nothing
		}

		@Override
		public void onRelationAdded(Relation relation) {
			// do nothing
		}

		@Override
		public void onFrameRemoved(Frame frame) {
			// do nothing
		}

		@Override
		public void onRelationRemoved(Relation relation) {
			// do nothing
		}

		@Override
		public void onFrameGraphUpdated() {
			// do nothing
		}

	}

	private final Frame root;
	private final FrameTopology topology;

	private final Map<Frame, Set<Relation>> relations = new HashMap<>();

	private final List<FrameGraphListener> listeners = new ArrayList<FrameGraphListener>();
	private final Object monitor = new Object();

	private final RelationListener relationListener = new RelationListener() {

		@Override
		public void relationAdded(Relation relation, Frame child) {
			addRelation(relation);
		}

		@Override
		public void relationRemoved(Relation relation, Frame child) {
			removeRelation(relation);
		}

	};

	private Set<Relation> getRelationList(Frame frame) {
		if (!relations.containsKey(frame)) {
			relations.put(frame, new HashSet<>());
		}
		return relations.get(frame);
	}

	/**
	 * Constructor.
	 *
	 * @param root the root frame of the graph
	 */
	public FrameGraph(Frame root) {
		this(root, new HashMap<Relation, RealtimeTransformation>());
	}

	public FrameGraph(Frame root, final FrameTopology topology,
			final Map<Relation, RealtimeTransformation> substitutes) {
		this(root, topology.withSubstitution(substitutes, null));
	}

	public FrameGraph(Frame root, Map<Relation, RealtimeTransformation> substitutes) {
		this(root, World.getCommandedTopology(), substitutes);
	}

	/**
	 * Constructor.
	 *
	 * @param root        the root frame of the graph
	 * @param substitutes a map with sensor substitutes for relations
	 */
	public FrameGraph(Frame root, FrameTopology topology) {
		if (root == null) {
			throw new IllegalArgumentException("Root frame must be not null.");
		}
		this.root = root;
		this.topology = topology;

		topology.addRelationListener(root, relationListener);
		addRelations(topology.getRelations(root));
	}

	/**
	 * Returns the root frame.
	 *
	 * @return the root frame.
	 */
	public Frame getRoot() {
		return root;
	}

	public FrameTopology getTopology() {
		return topology;
	}

	public RealtimeTransformation getRealtimeTransformationTo(Frame to) {
		return root.getRealtimeTransformationTo(to, topology);
	}

	public RealtimeTransformation getRealtimeTransformation(Frame from, Frame to) {
		return from.getRealtimeTransformationTo(to, topology);
	}

	public Transformation getTransformation(Frame to) {
		return getTransformation(root, to);
	}

	public Transformation getTransformation(Frame from, Frame to) {
		try {
			return from.getTransformationTo(to, topology);
		} catch (TransformationException e) {
			return null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		synchronized (monitor) {
			for (Frame frame : relations.keySet()) {
				if (frame != null) {
					topology.removeRelationListener(frame, relationListener);
				}
			}
			topology.removeRelationListener(root, relationListener);
		}
	}

	protected void addRelation(Relation relation) {
		Queue<Relation> queue = new LinkedList<Relation>();
		queue.add(relation);
		add(queue);
	}

	protected void addRelations(Collection<Relation> relations) {
		Queue<Relation> queue = new LinkedList<Relation>();
		queue.addAll(relations);
		add(queue);
	}

	protected void removeRelation(Relation relation) {
		Queue<Relation> queue = new LinkedList<Relation>();
		queue.add(relation);
		remove(queue);
	}

	protected void add(Queue<Relation> queue) {
		synchronized (monitor) {
			List<Frame> framesToAddListener = new ArrayList<>();
			notifyListenersOnChanging();

			while (!queue.isEmpty()) {
				Relation relation = queue.poll();
				// only do something, if we do not know this relation
				// marks the relation to be known
				if (getRelationList(relation.getFrom()).contains(relation)) {
					continue;
				}

				getRelationList(relation.getFrom()).add(relation);
				getRelationList(relation.getTo()).add(relation);

				Frame frame = relation.getTo();

				// see if we know the first frame...
				if (getRelationList(relation.getFrom()).size() > 1 && getRelationList(relation.getTo()).size() > 1) {
					// if we know both frames already, notify listeners
					// that there is only a new relation.
					notifyListenersOnRelationAdded(relation);
					// continue with the next relation in the queue
					continue;
				} else if (getRelationList(frame).size() > 1) {
					frame = relation.getOther(frame);
				}

				// add relation listener to frame
				// notify listeners about new relation AND frame
				notifyListenersOnFrameAdded(frame);
				notifyListenersOnRelationAdded(relation);
				framesToAddListener.add(frame);
				// and listen to changes
			}

			for (Frame frame : framesToAddListener) {
				topology.addRelationListener(frame, relationListener);
			}
			notifyListenersOnChanged();

		}
	}

	private void collectConnected(Frame frame, Collection<Frame> connected) {
		connected.add(frame);
		for (Relation r : getRelationList(frame)) {
			if (!connected.contains(r.getOther(frame))) {
				collectConnected(r.getOther(frame), connected);
			}
		}
	}

	protected void remove(Queue<Relation> queue) {
		synchronized (monitor) {
			List<Frame> framesToRemoveListener = new ArrayList<>();
			notifyListenersOnChanging();
			while (!queue.isEmpty()) {
				Relation relation = queue.poll();

				// only do something, if we know this relation
				if (!getRelationList(relation.getFrom()).contains(relation)) {
					continue;
				}

				getRelationList(relation.getFrom()).remove(relation);
				getRelationList(relation.getTo()).remove(relation);

				Frame to = relation.getTo(), from = relation.getFrom();

				// find frames connected to from and to
				Set<Frame> firstConnected = new HashSet<>(), secondConnected = new HashSet<>();
				collectConnected(to, firstConnected);
				collectConnected(from, secondConnected);

				if (firstConnected.contains(root) && secondConnected.contains(root)) {
					// if we are still connected to root
					notifyListenersOnRelationRemoved(relation);
				} else if (firstConnected.contains(root)) {
					// first is still connected, remove stuff around second

					notifyListenersOnRelationRemoved(relation);
					for (Frame f : secondConnected) {
						for (Relation r : getRelationList(f)) {
							notifyListenersOnRelationRemoved(r);
							relations.get(r.getOther(f)).remove(r);
						}
						relations.remove(f);
						framesToRemoveListener.add(f);
						notifyListenersOnFrameRemoved(f);
					}
				} else if (secondConnected.contains(root)) {
					// second is still connected, remove stuff around first

					notifyListenersOnRelationRemoved(relation);
					for (Frame f : firstConnected) {
						for (Relation r : getRelationList(f)) {
							notifyListenersOnRelationRemoved(r);
							relations.get(r.getOther(f)).remove(r);
						}
						relations.remove(f);
						framesToRemoveListener.add(f);
						notifyListenersOnFrameRemoved(f);
					}
				} else {
					throw new IllegalStateException("Received relation where neither side is connected to root");
				}

			}
			for (Frame frame : framesToRemoveListener) {
				topology.removeRelationListener(frame, relationListener);
			}

			notifyListenersOnChanged();
		}
	}

	public void addFrameGraphListener(FrameGraphListener l) {
		addFrameGraphListener(l, true);
	}

	public void addFrameGraphListener(FrameGraphListener l, boolean update) {
		synchronized (monitor) {
			listeners.add(l);

			if (!update) {
				return;
			}

			l.onFrameGraphUpdating();

			Queue<Frame> todo = new LinkedList<>();
			Set<Frame> seen = new HashSet<>();
			todo.add(root);
			l.onFrameAdded(root);
			while (!todo.isEmpty()) {
				Frame frame = todo.poll();
				if (!seen.add(frame)) {
					continue;
				}
				for (Relation r : getRelationList(frame)) {
					Frame other = r.getOther(frame);
					if (!seen.contains(other)) {
						if (!todo.contains(other)) {
							l.onFrameAdded(other);
						}
						l.onRelationAdded(r);
						todo.add(other);
					}
				}
			}

			l.onFrameGraphUpdated();
		}
	}

	public void removeFrameGraphListener(FrameGraphListener l) {
		removeFrameGraphListener(l, true);
	}

	public void removeFrameGraphListener(FrameGraphListener l, boolean update) {
		synchronized (monitor) {
			listeners.remove(l);

			if (!update) {
				return;
			}

			l.onFrameGraphUpdating();

			// notify about relations in reverse order
			Queue<Frame> todo = new LinkedList<>();
			List<Frame> seen = new ArrayList<>();
			todo.add(root);
			while (!todo.isEmpty()) {
				Frame frame = todo.poll();
				if (seen.contains(frame)) {
					continue;
				}
				seen.add(frame);
				for (Relation r : getRelationList(frame)) {
					if (!seen.contains(r.getOther(frame))) {
						todo.add(r.getOther(frame));
					}
				}
			}
			Collections.reverse(seen);
			while (!seen.isEmpty()) {
				Frame f = seen.remove(0);
				for (Relation r : getRelationList(f)) {
					if (seen.contains(r.getOther(f))) {
						l.onRelationRemoved(r);
					}
				}
				l.onFrameRemoved(f);
			}

		}
	}

	@Override
	public String toString() {
		return "Frame graph with root '" + this.root.getName() + "'";
	}

	int changeDepth = 0;

	protected void notifyListenersOnChanging() {
		changeDepth++;
		if (changeDepth == 1) {
			for (FrameGraphListener l : new ArrayList<FrameGraphListener>(this.listeners)) {
				l.onFrameGraphUpdating();
			}
		}
	}

	protected void notifyListenersOnFrameAdded(Frame frame) {
		for (FrameGraphListener l : new ArrayList<FrameGraphListener>(this.listeners)) {
			l.onFrameAdded(frame);
		}
	}

	protected void notifyListenersOnRelationAdded(Relation relation) {
		for (FrameGraphListener l : new ArrayList<FrameGraphListener>(this.listeners)) {
			l.onRelationAdded(relation);
		}
	}

	protected void notifyListenersOnFrameRemoved(Frame frame) {
		for (FrameGraphListener l : new ArrayList<FrameGraphListener>(this.listeners)) {
			l.onFrameRemoved(frame);
		}
	}

	protected void notifyListenersOnRelationRemoved(Relation relation) {
		for (FrameGraphListener l : new ArrayList<FrameGraphListener>(this.listeners)) {
			l.onRelationRemoved(relation);
		}
	}

	protected void notifyListenersOnChanged() {
		changeDepth--;
		if (changeDepth > 0) {
			return;
		}
		for (FrameGraphListener l : new ArrayList<FrameGraphListener>(this.listeners)) {
			l.onFrameGraphUpdated();
		}
	}

}
