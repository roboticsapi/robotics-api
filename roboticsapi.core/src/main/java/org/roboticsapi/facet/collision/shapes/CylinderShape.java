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
 * Creates a cylinder shape.
 */
public final class CylinderShape extends Shape {

	private final double xyHalfExtent;
	private final double zHalfExtent;

	/**
	 * Creates a new cylinder shaped.
	 *
	 * @param localTransform
	 *            local transformation of the shape (e.g., from a reference
	 *            frame to the shapes origin).
	 * @param xyHalfExtent
	 *            half the length of the edge in x- and y-direction (equal to the radius)
	 * @param zHalfExtent
	 *            half the length of the edge in z-direction
	 */
	public CylinderShape(Transformation transformation, double xyHalfExtent, double zHalfExtent) {
		super(transformation, 0);
		this.xyHalfExtent = xyHalfExtent;
		this.zHalfExtent = zHalfExtent;
	}

	/**
	 * Returns a list of the 8 convex surrounding box vertices relating to the center of the cylinder
	 *
	 * @return {@link Vector}[]
	 */
	@Override
	public Vector[] getVertices() {
		Vector[] v = new Vector[8];
		double r = getRadius();
		double z = getzHalfExtent();

		v[0] = transform(+r, +r, +z);
		v[1] = transform(+r, +r, -z);
		v[2] = transform(+r, -r, +z);
		v[3] = transform(+r, -r, -z);
		v[4] = transform(-r, +r, +z);
		v[5] = transform(-r, +r, -z);
		v[6] = transform(-r, -r, +z);
		v[7] = transform(-r, -r, -z);

		return v;
	}

	public double getRadius() {
		return xyHalfExtent;
	}

	public double getzHalfExtent() {
		return zHalfExtent;
	}

}
