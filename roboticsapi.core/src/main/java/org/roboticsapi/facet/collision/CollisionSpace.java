/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.collision;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.facet.collision.shapes.Shape;

/**
 * Interface for a collision space. It allows to add both static and kinematic
 * objects as well as check kinematic objects for collisions.
 *
 * While static object are fixed at a initial position, kinematic objects may be
 * moving, i.e., their positions can be updated. Moreover, collisions between
 * static objects are not checked.
 *
 * @param <T> The type of objects to add.
 */
public interface CollisionSpace<T> {

	/**
	 * Adds a static object with a given (global) transformation to the collision
	 * world.
	 *
	 * @param staticObject the object to add
	 * @param position     the object's position
	 * @return <code>true</code> if the object was successfully added
	 */
	boolean addStaticObject(T staticObject, Transformation position);

	/**
	 * Adds a kinematic object with a given (global) transformation to the collision
	 * world.
	 *
	 * @param kinematicObject the object to add
	 * @param initialPosition the object's initial position
	 * @return <code>true</code> if the object was successfully added
	 */
	boolean addKinematicObject(T kinematicObject, Transformation initialPosition);

	boolean removeCollisionObject(T object);

	boolean updateTransformation(T kinematicObject, Transformation newPosition);

	/**
	 * Add a filter which ignores any collisions between the specified objects.
	 *
	 * @param objects The objects for which a new filter has to be added.
	 *
	 * @return <code>true</code> if a filter was added.
	 */
	boolean ignoreCollisions(T... objects);

	boolean unignoreCollisions(T... objects);

	/**
	 * Returns all static objects.
	 *
	 * @return A {@link Set} with all static objects.
	 */
	Set<T> getStaticObjects();

	Set<T> getKinematicObjects();

	/**
	 * Returns all collision objects.
	 *
	 * @return A {@link Set} with all collision objects.
	 */
	default Set<T> getCollisionObjects() {
		Set<T> ret = new HashSet<T>();
		ret.addAll(getStaticObjects());
		ret.addAll(getKinematicObjects());

		return Collections.unmodifiableSet(ret);
	}

	Set<T> getIgnoredObjects(T object);

	default boolean isStaticObject(T object) {
		return getStaticObjects().contains(object);
	}

	default boolean isKinematicObject(T object) {
		return getKinematicObjects().contains(object);
	}

	/**
	 * Based on the known collision objects and their current positions, a collision
	 * check is performed. Found collisions which have not been filtered are
	 * returned.
	 *
	 * @return a {@link Set} of found {@link Collision}s.
	 */
	Set<Collision<T>> checkCollisions();

	default boolean hasCollisions() {
		return !checkCollisions().isEmpty();
	}

	/**
	 * This method takes a set of {@link Shape}s and checks with which static
	 * objects these shapes are colliding.
	 *
	 * This method can, e.g., be used for cell decomposition.
	 *
	 * @param shapes The {@link Shape}s to check
	 * @return A {@link Map} which contains for every specified {@link Shape} a
	 *         {@link Set} with colliding static objects.
	 */
	default Map<Shape, Set<T>> checkShapesWithStaticObjects(Shape... shapes) {
		return checkShapesWithStaticObjects(Transformation.IDENTITY, shapes);
	}

	/**
	 * This method takes a set of {@link Shape}s and checks with which static
	 * objects these shapes are colliding.
	 *
	 * This method can, e.g., be used for cell decomposition.
	 *
	 * @param globalTransformation A global transformation which is applied to every
	 *                             {@link Shape}
	 * @param shapes               The {@link Shape}s to check
	 * @return A {@link Map} which contains for every specified {@link Shape} a
	 *         {@link Set} with colliding static objects.
	 */
	default Map<Shape, Set<T>> checkShapesWithStaticObjects(Transformation globalTransformation, Shape... shapes) {
		return checkShapes(globalTransformation, getKinematicObjects(), shapes);
	}

	/**
	 * This method takes a set of {@link Shape}s and checks with which others
	 * collision objects these shapes are colliding.
	 *
	 * This method can, e.g., be used for cell decomposition.
	 *
	 * @param globalTransformation A global transformation which is applied to every
	 *                             {@link Shape}
	 * @param ignoredObjects       A {@link Set} of collision objects which will be
	 *                             ignored.
	 * @param shapes               The {@link Shape}s to check
	 * @return A {@link Map} which contains for every specified {@link Shape} a
	 *         {@link Set} with colliding objects.
	 */
	Map<Shape, Set<T>> checkShapes(Transformation globalTransformation, Set<T> ignoredObjects, Shape... shapes);

	/**
	 * Performs a ray test along a given vector. The direction of the ray has to be
	 * specified in the coordinate system of the starting position.
	 *
	 * @param position Position where the ray test starts.
	 * @param ray      The direction and max. length of the ray.
	 * @return
	 */
	default List<RayTestResult<T>> rayTest(Transformation position, Vector ray) {
		Vector start = position.getTranslation();
		Vector goal = position.apply(ray);

		return rayTest(start, goal);
	}

	/**
	 * Performs a ray test from a given starting point to a goal point.
	 *
	 * @param start The starting point
	 * @param goal  The goal
	 * @return A {@link RayTestResult}
	 */
	List<RayTestResult<T>> rayTest(Vector start, Vector goal);

}
