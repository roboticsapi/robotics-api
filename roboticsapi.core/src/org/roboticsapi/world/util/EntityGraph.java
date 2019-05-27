/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.core.entity.Property;
import org.roboticsapi.core.entity.PropertyListener;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.util.FrameGraph.FrameGraphListener;

/**
 * Based on a {@link FrameGraph}, this class creates and updates a graph of
 * {@link ComposedEntity}s.
 */
public class EntityGraph {

	/**
	 * Listener for entity graph events
	 */
	public static interface EntityGraphListener {

		/**
		 * Called if a new entity was added.
		 *
		 * @param entity The newly found entity.
		 */
		public void onEntityAdded(ComposedEntity entity);

		/**
		 * Called if a new entity was removed.
		 *
		 * @param entity The newly removed entity.
		 */
		public void onEntityRemoved(ComposedEntity entity);

		/**
		 * Called if a new property was added.
		 *
		 * @param entity   The entity
		 * @param property The newly found property.
		 */
		public void onPropertyAdded(ComposedEntity entity, Property property);

		public void onEntitiesUpdating();

		public void onEntitiesUpdated();

	}

	/**
	 * Adapter for {@link EntityGraphListener} with empty method implementation.
	 */
	public abstract static class EntityGraphAdapter implements EntityGraphListener {

		@Override
		public void onEntityAdded(ComposedEntity entity) {
			// do nothing...
		}

		@Override
		public void onEntityRemoved(ComposedEntity entity) {
			// do nothing...
		}

		@Override
		public void onPropertyAdded(ComposedEntity entity, Property property) {
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

	private final Map<ComposedEntity, Set<Entity>> knownEntities = new LinkedHashMap<ComposedEntity, Set<Entity>>();
	private final List<EntityGraphListener> listeners = new ArrayList<EntityGraphListener>();

	private final Object monitor = new Object();
	private final FrameGraphObserver observer = new FrameGraphObserver();

	/**
	 * Constructs a new entity graph based on a root frame.
	 *
	 * @param root the root {@link Frame}.
	 */
	public EntityGraph(Frame root) {
		this(new FrameGraph(root));
	}

	/**
	 * Constructs a new entity graph based on a frame graph.
	 *
	 * @param graph the underlying {@link FrameGraph}.
	 */
	public EntityGraph(FrameGraph graph) {
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
	 * Add a new {@link EntityGraphListener}. By adding, this listener will be
	 * informed that all currently known entities are added.
	 *
	 * @param l the listener to add.
	 */
	public void addEntityGraphListener(EntityGraphListener l) {
		synchronized (monitor) {
			listeners.add(l);
			Set<ComposedEntity> entities = knownEntities.keySet();

			l.onEntitiesUpdating();
			for (ComposedEntity e : entities) {
				notifyListenersOnEntityAdded(e, l);
			}
			l.onEntitiesUpdated();
		}
	}

	/**
	 * Removes a {@link EntityGraphListener}. By removing, this listener will be
	 * informed that all currently known entities are removed.
	 *
	 * @param l the listener to remove.
	 */
	public void removeEntityGraphListener(EntityGraphListener l) {
		synchronized (monitor) {
			listeners.remove(l);

			List<ComposedEntity> entities = new ArrayList<ComposedEntity>(knownEntities.keySet());
			Collections.reverse(entities);

			l.onEntitiesUpdating();
			for (ComposedEntity e : entities) {
				l.onEntityRemoved(e);
			}
			l.onEntitiesUpdated();
		}
	}

	/**
	 * Retrieves a {@link Set} with all currently known composed entities.
	 *
	 * @return a (maybe empty) {@link Set} of currently known
	 *         {@link ComposedEntity}s.
	 */
	public Set<ComposedEntity> getAll() {
		return get(ComposedEntity.class);
	}

	/**
	 * Retrieves a set of known composed entities that match the specified type.
	 *
	 * @param type The type of {@link ComposedEntity} to look for (or its subtypes)
	 * @return A (maybe empty) {@link Set} of {@link ComposedEntity}s matching the
	 *         criteria.
	 */
	public <T extends ComposedEntity> Set<T> get(Class<T> type) {
		return get(type, null);
	}

	/**
	 * Retrieves a set of known composed entities that match the specified type and
	 * that have a {@link Property} of the given type.
	 *
	 * @param type         The type of {@link ComposedEntity} to look for (or its
	 *                     subtypes)
	 * @param propertyType The type of {@link Property} to look for.
	 * @return A (maybe empty) {@link Set} of {@link ComposedEntity}s matching the
	 *         criteria.
	 */
	public <T extends ComposedEntity, P extends Property> Set<T> get(Class<T> type, Class<P> propertyType) {
		Set<T> results = new HashSet<T>();

		synchronized (monitor) {
			Set<ComposedEntity> entities = knownEntities.keySet();

			for (ComposedEntity entity : entities) {
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

	public void close() throws Throwable {
		this.getFrameGraph().removeFrameGraphListener(observer);
	}

	@Override
	protected void finalize() throws Throwable {
		close();
	}

	private void addEntitiesOf(Entity entity) {
		synchronized (monitor) {
			addRecursively(entity.getParent(), entity);
		}
	}

	private void addRecursively(ComposedEntity parent, Entity child) {
		if (parent == null) {
			return;
		}
		Set<Entity> set = knownEntities.get(parent);

		if (set == null) {
			set = new HashSet<Entity>();
			set.add(child);
			knownEntities.put(parent, set);
			parent.addPropertyListener(observer);
			notifyListenersOnEntityAdded(parent);

			addRecursively(parent.getParent(), parent);
		} else {
			set.add(child);
		}
	}

	private void removeEntitiesOf(Entity entity) {
		synchronized (monitor) {
			removeRecursively(entity.getParent(), entity);
		}
	}

	private void removeRecursively(ComposedEntity parent, Entity child) {
		if (parent == null) {
			return;
		}
		Set<Entity> set = knownEntities.get(parent);

		if (set != null) {
			set.remove(child);

			if (set.isEmpty()) {
				knownEntities.remove(parent);
				parent.removePropertyListener(observer);
				notifyListenersOnEntityRemoved(parent);

				removeRecursively(parent.getParent(), parent);
			}
		}
	}

	private void notifyListenersOnEntityAdded(ComposedEntity entity) {
		for (EntityGraphListener l : new ArrayList<EntityGraphListener>(this.listeners)) {
			notifyListenersOnEntityAdded(entity, l);
		}
	}

	private void notifyListenersOnEntityAdded(ComposedEntity entity, EntityGraphListener l) {
		l.onEntityAdded(entity);
		Set<Property> properties = entity.getProperties();

		for (Property property : properties) {
			l.onPropertyAdded(entity, property);
		}
	}

	private void notifyListenersOnEntityRemoved(ComposedEntity entity) {
		for (EntityGraphListener l : new ArrayList<EntityGraphListener>(this.listeners)) {
			l.onEntityRemoved(entity);
		}
	}

	private void notifyListenersOnPropertyAdded(ComposedEntity entity, Property property) {
		for (EntityGraphListener l : new ArrayList<EntityGraphListener>(this.listeners)) {
			l.onPropertyAdded(entity, property);
		}
	}

	private void notifyListenersOnEntitiesUpdating() {
		for (EntityGraphListener l : new ArrayList<EntityGraphListener>(this.listeners)) {
			l.onEntitiesUpdating();
		}
	}

	private void notifyListenersOnEntitiesUpdated() {
		for (EntityGraphListener l : new ArrayList<EntityGraphListener>(this.listeners)) {
			l.onEntitiesUpdated();
		}
	}

	private class FrameGraphObserver implements FrameGraphListener, PropertyListener {

		@Override
		public void onPropertyAdded(Entity entity, Property p) {
			if (entity instanceof ComposedEntity) {
				notifyListenersOnPropertyAdded((ComposedEntity) entity, p);
			}
		}

		@Override
		public void onFrameAdded(Frame frame) {
			addEntitiesOf(frame);
		}

		@Override
		public void onRelationAdded(Relation relation) {
			addEntitiesOf(relation);
		}

		@Override
		public void onFrameRemoved(Frame frame) {
			removeEntitiesOf(frame);
		}

		@Override
		public void onRelationRemoved(Relation relation) {
			removeEntitiesOf(relation);
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
