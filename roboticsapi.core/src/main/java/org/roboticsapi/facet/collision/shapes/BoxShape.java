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
 * Creates a box shape.
 */
public final class BoxShape extends Shape {

	// the half extents
	private final Vector boxHalfExtents;

	/**
	 * Creates a new box shaped.
	 *
	 * @param localTransform local transformation of the shape (e.g., from a
	 *                       reference frame to the shapes origin).
	 * @param halfExtents    {@link Vector} with half the length of the edges.
	 */
	public BoxShape(Transformation localTransform, Vector halfExtents) {
		super(localTransform, 0d);
		this.boxHalfExtents = halfExtents;
	}

	/**
	 * Creates a new box shaped.
	 *
	 * @param localTransform local transformation of the shape (e.g., from a
	 *                       reference frame to the shapes origin).
	 * @param xHalfExtent    half the length of the edge in x-direction
	 * @param yHalfExtent    half the length of the edge in y-direction
	 * @param zHalfExtent    half the length of the edge in z-direction
	 */
	public BoxShape(Transformation transformation, double xHalfExtent, double yHalfExtent, double zHalfExtent) {
		this(transformation, new Vector(xHalfExtent, yHalfExtent, zHalfExtent));
	}

	/**
	 * Returns half the length of the edges.
	 *
	 * @return {@link Vector} with the half extents of the box.
	 */
	public Vector getBoxHalfExtents() {
		return boxHalfExtents;
	}

	/**
	 * Returns a list of the 8 box vertices relating to the center of the box
	 *
	 * @return {@link Vector}[]
	 */
	@Override
	public Vector[] getVertices() {
		Vector[] v = new Vector[8];
		double x = boxHalfExtents.getX();
		double y = boxHalfExtents.getY();
		double z = boxHalfExtents.getZ();

		v[0] = transform(+x, +y, +z);
		v[1] = transform(+x, +y, -z);
		v[2] = transform(+x, -y, +z);
		v[3] = transform(+x, -y, -z);
		v[4] = transform(-x, +y, +z);
		v[5] = transform(-x, +y, -z);
		v[6] = transform(-x, -y, +z);
		v[7] = transform(-x, -y, -z);

		return v;
	}

}
