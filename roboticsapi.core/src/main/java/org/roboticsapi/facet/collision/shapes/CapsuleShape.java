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
 * Creates a capsule shape (in Z direction).
 */
public final class CapsuleShape extends Shape {

	private final double height;

	/**
	 * Creates a new capsule shape.
	 *
	 * @param localTransform local transformation of the shape (e.g., from a
	 *                       reference frame to the shapes origin).
	 * @param radius         radius of the capsule
	 * @param height         height of the capsule (height = total height - 2 *
	 *                       radius)
	 */
	public CapsuleShape(Transformation localTransform, double radius, double height) {
		super(localTransform, radius);

		this.height = height;
	}

	/**
	 * Returns the height of the capsule (height = total height - 2 * radius).
	 *
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	@Override
	public Vector[] getVertices() {
		Vector[] v = new Vector[2];
		v[0] = transform(0, 0, height * +0.5);
		v[1] = transform(0, 0, height * -0.5);

		return v;
	}

}
