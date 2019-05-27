/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

public class Point {
	private final Frame reference;
	private final Vector vector;

	public Point(Frame reference, Vector vector) {
		this.reference = reference;
		this.vector = vector;

	}

	public Frame getReferenceFrame() {
		return reference;
	}

	public Vector getVector() {
		return vector;
	}

	public String getName() {
		return getReferenceFrame().getName() + "[" + vector.getX() + ":" + vector.getY() + ":" + vector.getZ() + "]";
	}

	public boolean isEqualPoint(Point other) {
		return getReferenceFrame().equals(other.getReferenceFrame()) && getVector().isEqualVector(other.getVector());
	}

	public Point plus(double x, double y, double z) {
		return plus(new Vector(x, y, z));
	}

	public Point plus(Vector vector) {
		return new Point(getReferenceFrame(), getVector().add(vector));
	}

	// @Override
	// public boolean equals(Object obj) {
	// if(!super.equals(obj))
	// return false;
	//
	// if (!(obj instanceof Point))
	// return false;
	//
	// Point p = (Point)obj;
	//
	// if (!p.getReferenceFrame().equals(getReferenceFrame()))
	// return false;
	//
	// if (!p.getTranslation().equals(getTranslation()))
	// return false;
	//
	// return true;
	// }
	//
	// @Override
	// public int hashCode() {
	// int hash = HashCodeUtil.SEED;
	//
	// hash = HashCodeUtil.hash(hash, super.hashCode());
	// hash = HashCodeUtil.hash(hash, getClass());
	// hash = HashCodeUtil.hash(hash, getReferenceFrame());
	// return HashCodeUtil.hash(hash, getTranslation());
	// }
}
