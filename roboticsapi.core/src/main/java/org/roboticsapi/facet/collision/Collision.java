/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.collision;

import java.util.Arrays;
import java.util.List;

import org.roboticsapi.core.util.HashCodeUtil;
import org.roboticsapi.core.world.Vector;

/**
 * This class represents a collision between two objects
 *
 * @param <T> The type of the collided objects
 */
public class Collision<T> {
	public final T objectA;
	public final T objectB;

	public final Vector localPointA;
	public final Vector localPointB;

	/**
	 * Default constructor
	 *
	 * @param objectA     The first of the two collided objects
	 * @param objectB     The second of the two collided objects
	 * @param localPointA The local collision point of the first object
	 * @param localPointB The local collision point of the second object
	 */
	public Collision(T objectA, T objectB, Vector localPointA, Vector localPointB) {
		this.objectA = objectA;
		this.objectB = objectB;
		this.localPointA = localPointA;
		this.localPointB = localPointB;
	}

	/**
	 * Returns the first of the two collided objects.
	 *
	 * @return The first of the two collided objects
	 */
	public T getObjectA() {
		return objectA;
	}

	/**
	 * Returns the second of the two collided objects.
	 *
	 * @return The second of the two collided objects
	 */
	public T getObjectB() {
		return objectB;
	}

	/**
	 * Returns both collided objects in a {@link List}.
	 *
	 * @return a {@link List} with both collided objects.
	 */
	public List<T> getCollisionPair() {
		return Arrays.asList(objectA, objectB);
	}

	public Vector getLocalPointA() {
		return localPointA;
	}

	public Vector getLocalPointB() {
		return localPointB;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Collision) {
			Collision c = (Collision) obj;
			return (objectA.equals(c.objectA) && objectB.equals(c.objectB))
					|| (objectA.equals(c.objectB) && objectB.equals(c.objectA));
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		int hashCode = HashCodeUtil.hash(HashCodeUtil.SEED, getClass());
		hashCode = HashCodeUtil.hash(hashCode, objectA.hashCode() & objectB.hashCode());

		return hashCode;
	}

	@Override
	public String toString() {
		return "Collision between '" + objectA + "' and '" + objectB + "'";
	}

}
