/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.collision.shapes;

import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Point;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;

/**
 * Abstract super class for an convex collision shape based on an array of
 * vertices and a radius.
 */
public abstract class Shape {

	// transformation from a reference frame to the center of the shape
	private final Transformation transformation;
	// the radius
	private final double radius;

	/**
	 * Creates a new abstract shape.
	 *
	 * @param localTransform local transformation of the shape (e.g., from a
	 *                       reference frame to the shapes origin).
	 */
	public Shape(Transformation localTransform, double radius) {
		this.transformation = localTransform;
		this.radius = radius;
	}

	/**
	 * Returns the local transformation of the shape, i.e., from a reference frame
	 * to the center of the shape.
	 *
	 * @return the local transformation
	 */
	public Transformation getLocalTransformation() {
		return transformation;
	}

	/**
	 * Returns the vertices of the convex collision shape as an array of
	 * {@link Vector}s. The origin of the vectors is the center of the shape.
	 *
	 * @return an array of vertices as {@link Vector}s
	 */
	public abstract Vector[] getVertices();

	/**
	 * Returns the radius of the convex collision shape.
	 *
	 * @return the radius
	 */
	public double getRadius() {
		return this.radius;
	}

	/**
	 * Returns the vertices of the convex collision hull as an array of
	 * {@link Point}s to a given {@link Frame}.
	 *
	 * @param frame a reference frame.
	 * @return an array of vertices as {@link Point}s
	 */
	public final Point[] getPoints(Frame frame) {
		Vector v[] = getVertices();
		Point[] p = new Point[v.length];

		for (int i = 0; i < p.length; i++) {
			p[i] = new Point(frame, v[i]);
		}
		return p;
	}

	protected final Vector transform(double x, double y, double z) {
		return transform(new Vector(x, y, z));
	}

	protected final Vector transform(Vector v) {
		return this.transformation.apply(v);
	}

}
