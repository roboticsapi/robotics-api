/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.collision.shapes;

import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;

/**
 * Creates a sphere shape.
 */
public final class SphereShape extends Shape {

	/**
	 * Creates a new sphere shape.
	 *
	 * @param localTransform local transformation of the shape (e.g., from a
	 *                       reference frame to the shapes origin).
	 * @param radius         radius of the sphere
	 */
	public SphereShape(Transformation localTransform, double radius) {
		super(localTransform, radius);
	}

	@Override
	public Vector[] getVertices() {
		Vector[] v = new Vector[1];
		v[0] = transform(0, 0, 0);

		return v;
	}

}
