/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.Property;
import org.roboticsapi.core.PropertyListener;
import org.roboticsapi.core.RoboticsEntity;
import org.roboticsapi.core.util.RoboticsEntityUtils;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.util.FrameGraph.FrameGraphListener;

/**
 * Based on a {@link FrameGraph}, this class creates and updates a graph of
 * {@link RoboticsEntity}s.
 */
public class RoboticsEntityGraph {

	/**
	 * Listener for entity graph events
	 */
	public static interface RoboticsEntityGraphListener {

		/**
		 * Called if a new entity was added.
		 *
		 * @param entity The newly found entity.
		 */
		public void onEntityAdded(RoboticsEntity entity);

		/**
		 * Called if a new entity was removed.
		 *
		 * @param entity The newly removed entity.
		 */
		public void onEntityRemoved(RoboticsEntity entity);

		/**
		 * Called if a new property was added.
		 *
		 * @param entity   The entity
		 * @param property The newly found property.
		 */
		public void onPropertyAdded(RoboticsEntity entity, Property property);

		public void onEntitiesUpdating();

		public void onEntitiesUpdated();

	}

	/**
	 * Adapter for {@link RoboticsEntityGraphListener} with empty method
	 * implementation.
	 */
	public abstract static class EntityGraphAdapter implements RoboticsEntityGraphListener {

		@Override
		public void onEntityAdded(RoboticsEntity entity) {
			// do nothing...
		}

		@Override
		public void onEntityRemoved(RoboticsEntity entity) {
			// do nothing...
		}

		@Override
		public void onPropertyAdded(RoboticsEntity entity, Property property) {
			// do nothing...
		}

		@Override
		public void onEntitiesUpdating() {
			// do nothing
		}

		@Override
		public void onEntitiesUpdated() {
			// do nothing
		}

	}

	private final FrameGraph graph;

	private final Map<RoboticsEntity, Set<RoboticsEntity>> knownEntities = new LinkedHashMap<RoboticsEntity, Set<RoboticsEntity>>();
	private final List<RoboticsEntityGraphListener> listeners = new ArrayList<RoboticsEntityGraphListener>();

	private final Object monitor = new Object();
	private final FrameGraphObserver observer = new FrameGraphObserver();

	/**
	 * Constructs a new entity graph based on a root frame.
	 *
	 * @param root the root {@link Frame}.
	 */
	public RoboticsEntityGraph(Frame root) {
		this(new FrameGraph(root));
	}

	/**
	 * Constructs a new entity graph based on a frame graph.
	 *
	 * @param graph the underlying {@link FrameGraph}.
	 */
	public RoboticsEntityGraph(FrameGraph graph) {
		this.graph = graph;
		this.graph.addFrameGraphListener(observer);
	}

	/**
	 * Returns the underlying {@link FrameGraph}.
	 *
	 * @return the {@link FrameGraph}.
	 */
	public FrameGraph getFrameGraph() {
		return graph;
	}

	/**
	 * Add a new {@link RoboticsEntityGraphListener}. By adding, this listener will
	 * be informed that all currently known entities are added.
	 *
	 * @param l the listener to add.
	 */
	public void addEntityGraphListener(RoboticsEntityGraphListener l) {
		synchronized (monitor) {
			listeners.add(l);
			Set<RoboticsEntity> entities = knownEntities.keySet();

			l.onEntitiesUpdating();
			for (RoboticsEntity e : entities) {
				notifyListenersOnEntityAdded(e, l);
			}
			l.onEntitiesUpdated();
		}
	}

	/**
	 * Removes a {@link RoboticsEntityGraphListener}. By removing, this listener
	 * will be informed that all currently known entities are removed.
	 *
	 * @param l the listener to remove.
	 */
	public void removeEntityGraphListener(RoboticsEntityGraphListener l) {
		synchronized (monitor) {
			listeners.remove(l);

			List<RoboticsEntity> entities = new ArrayList<RoboticsEntity>(knownEntities.keySet());
			Collections.reverse(entities);

			l.onEntitiesUpdating();
			for (RoboticsEntity e : entities) {
				l.onEntityRemoved(e);
			}
			l.onEntitiesUpdated();
		}
	}

	/**
	 * Retrieves a {@link Set} with all currently known composed entities.
	 *
	 * @return a (maybe empty) {@link Set} of currently known
	 *         {@link RoboticsEntity}s.
	 */
	public Set<RoboticsEntity> getAll() {
		return get(RoboticsEntity.class);
	}

	/**
	 * Retrieves a set of known composed entities that match the specified type.
	 *
	 * @param type The type of {@link RoboticsEntity} to look for (or its subtypes)
	 * @return A (maybe empty) {@link Set} of {@link RoboticsEntity}s matching the
	 *         criteria.
	 */
	public <T extends RoboticsEntity> Set<T> get(Class<T> type) {
		return get(type, null);
	}

	/**
	 * Retrieves a set of known composed entities that match the specified type and
	 * that have a {@link Property} of the given type.
	 *
	 * @param type         The type of {@link RoboticsEntity} to look for (or its
	 *                     subtypes)
	 * @param propertyType The type of {@link Property} to look for.
	 * @return A (maybe empty) {@link Set} of {@link RoboticsEntity}s matching the
	 *         criteria.
	 */
	public <T extends RoboticsEntity, P extends Property> Set<T> get(Class<T> type, Class<P> propertyType) {
		Set<T> results = new HashSet<T>();

		synchronized (monitor) {
			Set<RoboticsEntity> entities = knownEntities.keySet();

			for (RoboticsEntity entity : entities) {
				if (type.isInstance(entity)) {
					if (propertyType == null) {
						results.add(type.cast(entity));
					} else {
						Set<P> properties = entity.getProperties(propertyType);

						if (!properties.isEmpty()) {
							results.add(type.cast(entity));
						}
					}
				}
			}
		}
		return results;
	}

	@Override
	protected void finalize() throws Throwable {
		this.getFrameGraph().removeFrameGraphListener(observer);
	}

	private void addEntitiesOf(RoboticsEntity entity) {
		synchronized (monitor) {
			addRecursively(RoboticsEntityUtils.getParent(entity), entity);
		}
	}

	private void addRecursively(RoboticsEntity parent, RoboticsEntity child) {
		if (parent == null) {
			return;
		}
		Set<RoboticsEntity> set = knownEntities.get(parent);

		if (set == null) {
			set = new HashSet<RoboticsEntity>();
			set.add(child);
			knownEntities.put(parent, set);
			parent.addPropertyListener(observer);
			notifyListenersOnEntityAdded(parent);

			addRecursively(RoboticsEntityUtils.getParent(parent), parent);
		} else {
			set.add(child);
		}
	}

	private void removeEntitiesOf(RoboticsEntity entity) {
		synchronized (monitor) {
			removeRecursively(RoboticsEntityUtils.getParent(entity), entity);
		}
	}

	private void removeRecursively(RoboticsEntity parent, RoboticsEntity child) {
		if (parent == null) {
			return;
		}
		Set<RoboticsEntity> set = knownEntities.get(parent);

		if (set != null) {
			set.remove(child);

			if (set.isEmpty()) {
				knownEntities.remove(parent);
				parent.removePropertyListener(observer);
				notifyListenersOnEntityRemoved(parent);

				removeRecursively(RoboticsEntityUtils.getParent(parent), parent);
			}
		}
	}

	private void notifyListenersOnEntityAdded(RoboticsEntity entity) {
		for (RoboticsEntityGraphListener l : new ArrayList<RoboticsEntityGraphListener>(this.listeners)) {
			notifyListenersOnEntityAdded(entity, l);
		}
	}

	private void notifyListenersOnEntityAdded(RoboticsEntity entity, RoboticsEntityGraphListener l) {
		l.onEntityAdded(entity);
		Set<Property> properties = entity.getProperties();
		for (Property property : properties) {
			l.onPropertyAdded(entity, property);
		}
	}

	private void notifyListenersOnEntityRemoved(RoboticsEntity entity) {
		for (RoboticsEntityGraphListener l : new ArrayList<RoboticsEntityGraphListener>(this.listeners)) {
			l.onEntityRemoved(entity);
		}
	}

	private void notifyListenersOnPropertyAdded(RoboticsEntity entity, Property property) {
		for (RoboticsEntityGraphListener l : new ArrayList<RoboticsEntityGraphListener>(this.listeners)) {
			l.onPropertyAdded(entity, property);
		}
	}

	private void notifyListenersOnEntitiesUpdating() {
		for (RoboticsEntityGraphListener l : new ArrayList<RoboticsEntityGraphListener>(this.listeners)) {
			l.onEntitiesUpdating();
		}
	}

	private void notifyListenersOnEntitiesUpdated() {
		for (RoboticsEntityGraphListener l : new ArrayList<RoboticsEntityGraphListener>(this.listeners)) {
			l.onEntitiesUpdated();
		}
	}

	private class FrameGraphObserver implements FrameGraphListener, PropertyListener {

		@Override
		public void onPropertyAdded(RoboticsEntity entity, Property p) {
			notifyListenersOnPropertyAdded(entity, p);
		}

		@Override
		public void onFrameAdded(Frame frame) {
			addEntitiesOf(frame);
		}

		@Override
		public void onRelationAdded(Relation relation) {
		}

		@Override
		public void onFrameRemoved(Frame frame) {
			removeEntitiesOf(frame);
		}

		@Override
		public void onRelationRemoved(Relation relation) {
		}

		@Override
		public void onFrameGraphUpdating() {
			notifyListenersOnEntitiesUpdating();
		}

		@Override
		public void onFrameGraphUpdated() {
			notifyListenersOnEntitiesUpdated();
		}

	}

}
