/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.collision;

import org.roboticsapi.core.world.Vector;

/**
 * This class represents the result of a ray test.
 *
 * @param <T> The type of colliding objects
 */
public class RayTestResult<T> {

	private final T object;
	private final Vector hitNormalWorld, hitPointWorld;
	private final double distance;

	public RayTestResult(T object, double distance, Vector hitNormalWorld, Vector hitPointWorld) {
		this.object = object;
		this.distance = distance;
		this.hitNormalWorld = hitNormalWorld;
		this.hitPointWorld = hitPointWorld;
	}

	/**
	 * Describes the distance you had to travel from the given start vector to the
	 * given goal vector to reach the hit-point.
	 *
	 * @return distance traveled to reach the hit-point.
	 */
	public double getDistance() {
		return distance;
	}

	public Vector getHitNormalWorld() {
		return hitNormalWorld;
	}

	/**
	 * Returns a vector which describes the global position of the hit-point between
	 * ray and some object.
	 * 
	 * @return
	 */
	public Vector getHitPointWorld() {
		return hitPointWorld;
	}

	public T getObject() {
		return object;
	}
}
