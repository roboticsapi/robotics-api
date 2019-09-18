/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.collision.world;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.roboticsapi.core.Relationship;
import org.roboticsapi.core.RelationshipView;
import org.roboticsapi.core.RelationshipView.DerivedRelationshipView;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.RoboticsObject.RelationshipListener;

/**
 * This class represents a special {@link RelationshipView} containing all
 * {@link RoboticsObject} which are part of the connected component of a given
 * root object.
 *
 * This class creates a connected component during creation. However, it will
 * not update the connected component at runtime. Only listeners are available
 * to get notified if a change occurs.
 *
 * @param <T> Type of {@link Relationship}
 */
public class ConnectivityRelationshipView<T extends Relationship> extends DerivedRelationshipView<T> {

	/**
	 * The listener interface for receiving events regarding changes of the initial
	 * connected component, i.e., the adding or removal of {@link Relationship}s.
	 */
	public interface ConnectivityRelationshipListener<T extends Relationship> {

		/**
		 * Called when a {@link Relationship} has been added to a {@link RoboticsObject}
		 * of the connected component.
		 *
		 * @param from The first {@link RoboticsObject} of the new {@link Relationship}
		 * @param r    The newly added {@link Relationship}.
		 * @param to   The second {@link RoboticsObject} of the new {@link Relationship}
		 */
		public void onRelationshipAdded(RoboticsObject from, T r, RoboticsObject to);

		/**
		 * Called when a {@link Relationship} has been from a {@link RoboticsObject} of
		 * the connected component.
		 *
		 * @param from The first {@link RoboticsObject} of the removed
		 *             {@link Relationship}
		 * @param r    The removed {@link Relationship}.
		 * @param to   The second {@link RoboticsObject} of the removed
		 *             {@link Relationship}
		 */
		public void onRelationshipRemoved(RoboticsObject from, T r, RoboticsObject to);

	}

	private final RoboticsObject root;
	private final Map<RoboticsObject, List<T>> knownObjects;

	private final Set<ConnectivityRelationshipListener<T>> listeners = new CopyOnWriteArraySet<ConnectivityRelationshipListener<T>>();

	// adapter for notifications
	private final RelationshipListener<T> adapter = new RelationshipListener<T>() {

		@Override
		public void onAdded(T r) {
			for (ConnectivityRelationshipListener<T> l : listeners) {
				l.onRelationshipAdded(r.getFrom(), r, r.getTo());
			}
		}

		@Override
		public void onRemoved(T r) {
			for (ConnectivityRelationshipListener<T> l : listeners) {
				l.onRelationshipRemoved(r.getFrom(), r, r.getTo());
			}
		}

	};

	/**
	 * Constructor in which the connected component is created using depth-first
	 * search.
	 *
	 * @param root       The root node of the connected component
	 * @param innerGraph The underlying graph
	 */
	public ConnectivityRelationshipView(RoboticsObject root, RelationshipView<T> innerGraph) {
		super(innerGraph);
		this.root = root;
		this.knownObjects = new HashMap<RoboticsObject, List<T>>();

		traversAndCollect(root);
	}

	/**
	 * Return the root node of the connected component.
	 *
	 * @return The root node.
	 */
	public RoboticsObject getRoot() {
		return root;
	}

	/**
	 * Return an unmodifiable {@link Set} with all known {@link RoboticsObject}s.
	 *
	 * @return The {@link Set} of known {@link RoboticsObject}s.
	 */
	public Set<RoboticsObject> getObjects() {
		return Collections.unmodifiableSet(this.knownObjects.keySet());
	}

	/**
	 * Returns <code>true</code> if the connected component of the
	 * {@link RelationshipView} contains the specified {@link RoboticsObject}.
	 *
	 * @param object The {@link RoboticsObject} whose presence is to be tested
	 * @return <code>true</code> if the connected component contains the specified
	 *         {@link RoboticsObject}.
	 */
	public boolean contains(RoboticsObject object) {
		return knownObjects.containsKey(object);
	}

	@Override
	public List<T> getRelationships(RoboticsObject o) {
		return knownObjects.get(o);
	}

	/**
	 * Adds a new {@link ConnectivityRelationshipListener}.
	 *
	 * @param listener the listener to add
	 */
	public void addListener(ConnectivityRelationshipListener<T> listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a {@link ConnectivityRelationshipListener}.
	 *
	 * @param listener the listener to remove
	 */
	public void removeListener(ConnectivityRelationshipListener<T> listener) {
		listeners.remove(listener);
	}

	private void traversAndCollect(RoboticsObject node) {
		// check if we know this node already
		if (knownObjects.containsKey(node)) {
			return;
		}

		// add the node to our known objects
		// including its currently available relationships
		List<T> edges = super.getRelationships(node);
		knownObjects.put(node, edges);

		// add a relationship listener for listening to changes
		node.addRelationshipListener(getRelationshipType(), adapter);

		// continue depth-first search
		for (T edge : edges) {
			RoboticsObject neighbor = getOther(edge, node);
			traversAndCollect(neighbor);
		}
	}

	private static <T extends Relationship> RoboticsObject getOther(T edge, RoboticsObject object) {
		if (object == edge.getFrom()) {
			return edge.getTo();
		} else {
			return edge.getFrom();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		// remove the listener from all known objects.
		for (RoboticsObject o : knownObjects.keySet()) {
			o.removeRelationshipListener(getRelationshipType(), adapter);
		}

	}

	@Override
	public void addRelationshipListener(RoboticsObject o, RelationshipListener<T> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRelationshipListener(RoboticsObject o, RelationshipListener<T> listener) {
		// TODO Auto-generated method stub

	}

}
