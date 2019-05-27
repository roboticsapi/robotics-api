/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.util;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.roboticsapi.core.Predicate;
import org.roboticsapi.core.entity.Entity;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;

/**
 * Utility methods for searching a frame graph.
 */
public class EntitySearch {

	/**
	 * Visitor that is invoked for every visited entity (i.e. a frame or a relation)
	 * while searching in a frame graph.
	 */
	public interface EntityVisitor {

		/**
		 * Method that is called for every visited frame or relation.
		 *
		 * @param e the visited entity
		 */
		public void visit(Entity e);

	}

	/**
	 * Performs a breadth-first search in the frame graph starting at a given
	 * {@link Frame}.
	 *
	 * @param startNode       the starting frame.
	 * @param resultPredicate a predicate to determine the target entity (i.e. a
	 *                        frame or a relation) of this search.
	 * @param searchFilter    an (optional) filter to reduce the search space. If it
	 *                        evaluates to <code>true</code>, the frame or relation
	 *                        will be included from the search. If no filter is
	 *                        specified, every frame or relation will be included.
	 * @param v               an (optional) visitor that is invoked for every
	 *                        included frame or relation.
	 * @return the entity found using the given result predicate.
	 */
	public static Entity breadthFirstSearch(Frame startNode, Predicate<Entity> resultPredicate,
			Predicate<Entity> searchFilter, EntityVisitor v) {
		Queue<Frame> queue = new LinkedList<Frame>();
		Set<Entity> visited = new HashSet<Entity>();

		// check start node
		if (!isIncluded(startNode, searchFilter)) {
			return null;
		}
		// visit the start node
		visit(startNode, v, visited);

		// add start node to queue and ...
		queue.add(startNode);

		while (!queue.isEmpty()) {
			// get first element
			Frame node = queue.poll();

			// check if this frame is our target entity
			if (isFound(node, resultPredicate)) {
				return node;
			}

			// expand all child nodes...
			for (Relation r : node.getRelations()) {
				// check if we know this relation already
				if (visited.contains(r)) {
					continue;
				}

				// check if we include this relation into our search
				if (!isIncluded(r, searchFilter)) {
					continue;
				}
				// visit the relation and mark it
				visit(r, v, visited);

				// check if this relation is our target entity
				if (isFound(r, resultPredicate)) {
					return r;
				}
				// get the other node
				Frame other = r.getOther(node);

				// check if we know this frame already
				if (!visited.contains(other)) {
					// check if we include this frame into our search
					if (!isIncluded(other, searchFilter)) {
						continue;
					}
					// visit the frame and mark it
					visit(other, v, visited);

					// add frame to queue
					queue.add(other);
				}
			}
		}
		// return null if target entity was not found
		return null;
	}

	/**
	 * Performs a depth-first search in the frame graph starting at a given
	 * {@link Frame}.
	 *
	 * @param startNode       the starting frame.
	 * @param resultPredicate a predicate to determine the target entity (i.e. a
	 *                        frame or a relation) of this search.
	 * @param searchFilter    an (optional) filter to reduce the search space. If it
	 *                        evaluates to <code>true</code>, the frame or relation
	 *                        will be included from the search. If no filter is
	 *                        specified, every frame or relation will be included.
	 * @param v               an (optional) visitor that is invoked for every
	 *                        included frame or relation.
	 * @return the entity found using the given result predicate.
	 */
	public static Entity depthFirstSearch(Frame startNode, Predicate<Entity> resultPredicate,
			Predicate<Entity> searchFilter, EntityVisitor v) {
		Deque<Frame> stack = new LinkedList<Frame>();
		Set<Entity> visited = new HashSet<Entity>();

		// check start node
		if (!isIncluded(startNode, searchFilter)) {
			return null;
		}
		// push start node onto the stack...
		stack.push(startNode);

		while (!stack.isEmpty()) {
			// get top element from stack
			Frame node = stack.pop();

			// check if we have visited this frame already
			if (!visited.contains(node)) {
				// visit the node
				visit(node, v, visited);

				// check if this frame is our target entity
				if (isFound(node, resultPredicate)) {
					return node;
				}

				// expand all child nodes...
				for (Relation r : node.getRelations()) {
					// check if we know this relation already
					if (visited.contains(r)) {
						continue;
					}

					// check if we include this relation into our search
					if (!isIncluded(r, searchFilter)) {
						continue;
					}
					// visit the relation and mark it
					visit(r, v, visited);

					// check if this relation is our target entity
					if (isFound(r, resultPredicate)) {
						return node;
					}
					// get the other node
					Frame other = r.getOther(node);

					// check if we include this frame into our search
					if (!isIncluded(other, searchFilter)) {
						continue;
					}
					// push frame onto stack
					stack.push(other);
				}
			}
		}
		// return null if target entity was not found
		return null;
	}

	private static void visit(Entity node, EntityVisitor v, Set<Entity> visited) {
		if (v != null) {
			v.visit(node);
		}
		visited.add(node);
	}

	private static boolean isFound(Entity node, Predicate<Entity> predicate) {
		if (predicate != null) {

			return predicate.appliesTo(node);
		}
		return false;
	}

	private static boolean isIncluded(Entity node, Predicate<Entity> filter) {
		if (filter != null) {
			return filter.appliesTo(node);
		}
		return true;
	}

}
