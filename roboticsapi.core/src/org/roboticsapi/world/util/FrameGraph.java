/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.RelationListener;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.sensor.RelationSensor;

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
	private final Map<Relation, RelationSensor> substitutes;

	private final Set<Frame> knownFrames = new WeakEntitySet<Frame>();
	private final Set<Relation> knownRelations = new LinkedHashSet<Relation>();

	private final List<FrameGraphListener> listeners = new ArrayList<FrameGraphListener>();
	private final Object monitor = new Object();

	// private final Map<Registration, RelationSensor> sensorMap = new
	// ConcurrentHashMap<Registration, RelationSensor>();
	// private final Queue<Registration> addQueue = new
	// ConcurrentLinkedQueue<Registration>();
	// private final Queue<Registration> removeQueue = new
	// ConcurrentLinkedQueue<Registration>();
	//
	// private final AtomicBoolean updateSensors = new AtomicBoolean();
	// private TransformationArraySensor sensor;
	// private SensorListener<Transformation[]> updater;

	private final RelationListener relationListener = new InternalRelationListener();

	/**
	 * Constructor.
	 *
	 * @param root the root frame of the graph
	 */
	public FrameGraph(Frame root) {
		this(root, new HashMap<Relation, RelationSensor>());
	}

	/**
	 * Constructor.
	 *
	 * @param root        the root frame of the graph
	 * @param substitutes a map with sensor substitutes for relations
	 */
	public FrameGraph(Frame root, Map<Relation, RelationSensor> substitutes) {
		if (root == null) {
			throw new IllegalArgumentException("Root frame must be not null.");
		}
		this.root = root;
		this.substitutes = new ConcurrentHashMap<Relation, RelationSensor>(substitutes);

		knownFrames.add(root);
		this.root.addRelationListener(relationListener);
		addRelations(this.root.getRelations());
	}

	/**
	 * Returns the root frame.
	 *
	 * @return the root frame.
	 */
	public Frame getRoot() {
		return root;
	}

	public RelationSensor getRelationSensor(Frame to) {
		return root.getRelationSensor(to, substitutes);
	}

	public RelationSensor getRelationSensor(Frame from, Frame to) {
		return from.getRelationSensor(to, substitutes);
	}

	public Transformation getTransformation(Frame to) {
		return getTransformation(root, to);
	}

	public Transformation getTransformation(Frame from, Frame to) {
		try {
			return from.getTransformationTo(to, true, substitutes);
		} catch (TransformationException e) {
			return null;
		}
	}

	public void update(Relation relation, RelationSensor sensor) {
		this.substitutes.put(relation, sensor);

		notifyListenersOnRemoved(relation);
		notifyListenersOnAdded(relation);
	}

	public void updateAll(Map<Relation, RelationSensor> substitutes) {
		Set<Entry<Relation, RelationSensor>> set = substitutes.entrySet();

		for (Entry<Relation, RelationSensor> e : set) {
			update(e.getKey(), e.getValue());
		}
	}

	// public void addTransformationListener(Frame target,
	// SensorListener<Transformation> listener) {
	// addTransformationListener(this.root, target, listener);
	// }
	//
	// public void addTransformationListener(Frame destination, Frame target,
	// SensorListener<Transformation> listener) {
	// Registration reg = new Registration(destination, target, listener);
	// this.addQueue.add(reg);
	// scheduleWorker();
	// }
	//
	// public void removeTransformationListener(Frame destination, Frame target,
	// SensorListener<Transformation> listener) {
	// Registration reg = new Registration(destination, target, listener);
	// this.removeQueue.add(reg);
	// scheduleWorker();
	// }

	public void close() throws Throwable {
		synchronized (monitor) {
			for (Frame frame : knownFrames) {
				if (frame != null) {
					frame.removeRelationListener(relationListener);
				}
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
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
			notifyListenersOnChanging();

			while (!queue.isEmpty()) {
				Relation relation = queue.poll();

				// only do something, if we do not know this relation
				// marks the relation to be known
				if (!knownRelations.add(relation)) {
					continue;
				}
				Frame frame = relation.getTo();

				// see if we know the first frame...
				if (knownFrames.contains(frame)) {
					frame = relation.getFrom();

					// see if we also know the second frame...
					if (knownFrames.contains(frame)) {
						// if we know both frames already, notify listeners
						// that there is only a new relation.
						notifyListenersOnAdded(relation);
						// continue with the next relation in the queue
						continue;
					}
				}
				// check if frame is not null.. just in case GC was fast
				if (frame != null) {
					// mark the frame to be known
					knownFrames.add(frame);
					// add relation listener to frame
					frame.addRelationListener(relationListener);

					// visit all connected frames
					for (Relation r : frame.getRelations()) {
						if (r != relation) {
							queue.add(r);
						}
					}
				}
				// notify listeners about new relation AND frame
				notifyListenersOnAdded(relation, frame);
			}

			notifyListenersOnChanged();
		}
	}

	protected void remove(Queue<Relation> queue) {
		synchronized (monitor) {
			notifyListenersOnChanging();

			while (!queue.isEmpty()) {
				Relation relation = queue.poll();

				// only do something, if we know this relation
				// marks the relation to be NOT known
				if (!knownRelations.remove(relation)) {
					continue;
				}
				Frame frame = relation.getTo();

				if (frame == null) {
					notifyListenersOnRemoved(relation, null);
					continue;
				}

				// see if the first frame is still connected to the root
				if (isConnectedWithRoot(frame) || !knownFrames.contains(frame)) {
					frame = relation.getFrom();

					// see if the second frame is still connected to the root
					if (isConnectedWithRoot(frame)) {
						// if both frames are still connected, notify listeners
						// that only a relation was removed.
						notifyListenersOnRemoved(relation, null);
						// continue with the next relation in the queue
						continue;
					}
				}
				// check if frame is not null
				if (frame != null) {
					// mark the frame to be NOT known
					knownFrames.remove(frame);
					// remove relation listener from frame
					frame.removeRelationListener(relationListener);

					// visit all connected frames
					for (Relation r : frame.getRelations()) {
						if (r != relation) {
							queue.add(r);
						}
					}
				}
				// notify listeners
				notifyListenersOnRemoved(relation, frame);
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

			for (Frame f : new ArrayList<Frame>(this.knownFrames)) {
				l.onFrameAdded(f);
			}

			for (Relation r : this.knownRelations) {
				l.onRelationAdded(r);
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

			// notify about frames in reverse order
			List<Frame> frames = new ArrayList<Frame>(knownFrames);
			Collections.reverse(frames);

			for (Frame f : frames) {
				l.onFrameRemoved(f);
			}

			// notify about relations in reverse order
			List<Relation> relations = new ArrayList<Relation>(knownRelations);
			Collections.reverse(relations);

			for (Relation r : relations) {
				l.onRelationRemoved(r);
			}
		}
	}

	@Override
	public String toString() {
		return "Frame graph with root '" + this.root.getName() + "'";
	}

	private boolean isConnectedWithRoot(Frame frame) {
		return this.root.getRelationsTo(frame) != null;
	}

	protected void notifyListenersOnChanging() {
		for (FrameGraphListener l : new ArrayList<FrameGraphListener>(this.listeners)) {
			l.onFrameGraphUpdating();
		}
	}

	protected void notifyListenersOnAdded(Relation relation, Frame frame) {
		for (FrameGraphListener l : new ArrayList<FrameGraphListener>(this.listeners)) {
			l.onRelationAdded(relation);
			l.onFrameAdded(frame);
		}
	}

	protected void notifyListenersOnRemoved(Relation relation, Frame frame) {
		for (FrameGraphListener l : new ArrayList<FrameGraphListener>(this.listeners)) {
			l.onRelationRemoved(relation);

			if (frame != null) {
				l.onFrameRemoved(frame);
			}
		}
	}

	protected void notifyListenersOnAdded(Relation relation) {
		for (FrameGraphListener l : new ArrayList<FrameGraphListener>(this.listeners)) {
			l.onRelationAdded(relation);
		}
	}

	protected void notifyListenersOnRemoved(Relation relation) {
		for (FrameGraphListener l : new ArrayList<FrameGraphListener>(this.listeners)) {
			l.onRelationRemoved(relation);
		}
	}

	protected void notifyListenersOnChanged() {
		for (FrameGraphListener l : new ArrayList<FrameGraphListener>(this.listeners)) {
			l.onFrameGraphUpdated();
		}
	}

	private class InternalRelationListener implements RelationListener {

		@Override
		public void relationAdded(Relation relation, Frame child) {
			addRelation(relation);
		}

		@Override
		public void relationRemoved(Relation relation, Frame child) {
			removeRelation(relation);
		}

	};

	// private static class InternalThreadFactory implements ThreadFactory {
	//
	// @Override
	// public Thread newThread(Runnable r) {
	// Thread t = new Thread(r);
	// t.setName("Robotics API FrameGraph Worker");
	// t.setDaemon(true);
	// t.setPriority(Thread.NORM_PRIORITY);
	//
	// return t;
	// }
	// };
	//
	// private class InternalWorker implements Runnable {
	//
	// @Override
	// public void run() {
	// for (Registration r : addQueue) {
	// sensorMap.put(r, null);
	// }
	//
	// for (Registration r : removeQueue) {
	// sensorMap.remove(r);
	// }
	//
	// Set<Entry<Registration, RelationSensor>> set = sensorMap.entrySet();
	//
	// for (Iterator<Entry<Registration, RelationSensor>> iterator = set
	// .iterator(); iterator.hasNext();) {
	// Entry<Registration, RelationSensor> entry = iterator.next();
	//
	// }
	//
	// }
	//
	// }
	//
	// private class InternalUpdater implements SensorListener<Transformation[]>
	// {
	//
	// private final SensorListener<Transformation>[] listeners;
	// private final Transformation[] values;
	//
	// protected InternalUpdater(SensorListener<Transformation>[] listeners) {
	// this.listeners = listeners;
	// this.values = new Transformation[listeners.length];
	// }
	//
	// @Override
	// public void onValueChanged(Transformation[] newValues) {
	// for (int i = 0; i < newValues.length; i++) {
	// Transformation newValue = newValues[i];
	//
	// if (!newValue.equals(values[i])) {
	// values[i] = newValue;
	// listeners[i].onValueChanged(newValue);
	// }
	// }
	// }
	//
	// }

}
