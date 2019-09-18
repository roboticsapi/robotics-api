/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.collision.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.roboticsapi.core.Relationship;
import org.roboticsapi.core.RelationshipView;
import org.roboticsapi.core.RelationshipView.DerivedRelationshipView;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.RoboticsObject.RelationshipListener;
import org.roboticsapi.core.RoboticsObjectView;

public class DynamicConnectivityRelationshipView<T extends Relationship> extends DerivedRelationshipView<T>
		implements RoboticsObjectView<RoboticsObject> {

	/**
	 * The listener interface for receiving events regarding changes of the initial
	 * connected component, i.e., the adding or removal of {@link Relationship}s.
	 */
	public interface DynamicConnectivityRelationshipListener<T extends Relationship> {

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
	private final Set<RoboticsObject> knownObjects;

	private final Set<DynamicConnectivityRelationshipListener<T>> listeners = new CopyOnWriteArraySet<DynamicConnectivityRelationshipListener<T>>();

	// adapter for notifications
	private final RelationshipListener<T> adapter = new RelationshipListener<T>() {

		@Override
		public void onAdded(T r) {
			if (!knownObjects.contains(r.getTo()))
				findConnectedAndAdd(r.getTo());

			if (!knownObjects.contains(r.getFrom()))
				findConnectedAndAdd(r.getFrom());

			for (DynamicConnectivityRelationshipListener<T> l : listeners) {
				l.onRelationshipAdded(r.getFrom(), r, r.getTo());
			}
		}

		@Override
		public void onRemoved(T r) {
			findDisconnectedAndRemove(r.getFrom());
			findDisconnectedAndRemove(r.getTo());

			for (DynamicConnectivityRelationshipListener<T> l : listeners) {
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
	public DynamicConnectivityRelationshipView(RoboticsObject root, RelationshipView<T> innerGraph) {
		super(innerGraph);
		this.root = root;
		this.knownObjects = new HashSet<RoboticsObject>();

		findConnectedAndAdd(root);
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
	 * Return an unmodifiable {@link Set} with all known {@link RoboticsObject} s.
	 *
	 * @return The {@link Set} of known {@link RoboticsObject}s.
	 */
	public Set<RoboticsObject> getObjects() {
		return Collections.unmodifiableSet(this.knownObjects);
	}

	@Override
	public void addRelationshipListener(RoboticsObject o, RelationshipListener<T> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRelationshipListener(RoboticsObject o, RelationshipListener<T> listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<RoboticsObject> getObjectType() {
		// TODO Auto-generated method stub
		return null;
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
		return knownObjects.contains(object);
	}

	@Override
	public List<T> getRelationships(RoboticsObject o) {
		if (knownObjects.contains(o)) {
			return super.getRelationships(o);
		}
		return new ArrayList<>();
	}

	/**
	 * Adds a new {@link DynamicConnectivityRelationshipListener}.
	 *
	 * @param listener the listener to add
	 */
	public void addListener(DynamicConnectivityRelationshipListener<T> listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a {@link DynamicConnectivityRelationshipListener}.
	 *
	 * @param listener the listener to remove
	 */
	public void removeListener(DynamicConnectivityRelationshipListener<T> listener) {
		listeners.remove(listener);
	}

	private void findConnectedAndAdd(RoboticsObject node) {
		// check if we know this node already
		if (knownObjects.contains(node)) {
			return;
		}

		// create a new set for potentially connected nodes.
		Set<RoboticsObject> connected = new HashSet<>();

		// just search for all objects, which are connected to the given node
		traverseAndCollect(node, connected);

		for (RoboticsObject object : connected) {
			if (knownObjects.contains(node)) {
				continue;
			}
			knownObjects.add(object);
			object.addRelationshipListener(getRelationshipType(), adapter);
		}
	}

	private void findDisconnectedAndRemove(RoboticsObject node) {
		// create a new set for potentially disconnected nodes.
		Set<RoboticsObject> disconnected = new HashSet<>();

		// just search for all objects, which are still connected to the given
		// node and look if root is part of it - if not just delete them all.
		if (!traverseAndCollect(node, this.root, disconnected)) {
			// we have not found root. Hence, found nodes are disconnected
			for (RoboticsObject object : disconnected) {
				object.removeRelationshipListener(getRelationshipType(), adapter);
				knownObjects.remove(object);
			}
		}
	}

	private boolean traverseAndCollect(RoboticsObject node, Set<RoboticsObject> foundNodes) {
		return traverseAndCollect(node, null, foundNodes);
	}

	private boolean traverseAndCollect(RoboticsObject node, RoboticsObject stopNode, Set<RoboticsObject> foundNodes) {
		// if we found the stopNode, stop searching
		if (stopNode != null && stopNode.equals(node)) {
			return true;
		}

		// if node is already in the list, stop searching, too.
		if (!foundNodes.add(node)) {
			return false;
		}

		List<T> edges = super.getRelationships(node);
		for (T edge : edges) {
			RoboticsObject neighbor = getOther(edge, node);

			if (traverseAndCollect(neighbor, stopNode, foundNodes)) {
				return true;
			}
			// else continue to search further neighbors.
		}
		return false;
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
		for (RoboticsObject o : knownObjects) {
			o.removeRelationshipListener(getRelationshipType(), adapter);
		}

	}

}
