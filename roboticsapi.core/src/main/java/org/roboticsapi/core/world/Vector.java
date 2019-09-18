/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.util.HashCodeUtil;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

/**
 * A Robotics API world model vector (immutable, 3D)
 */
public class Vector {
	public static final Vector ZERO = new Vector();

	private static final double EPSILON = 0.00001;
	/** x, y and z components */
	private final double x, y, z;

	/**
	 * Creates a new vector (0,0,0)
	 */
	public Vector() {
		this(0, 0, 0);
	}

	/**
	 * Creates a new vector
	 *
	 * @param x x component in m
	 * @param y y component in m
	 * @param z z component in m
	 */
	public Vector(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Retrieves the x component
	 *
	 * @return x component
	 */
	public double getX() {
		return x;
	}

	/**
	 * Retrieves the y component
	 *
	 * @return y component
	 */
	public double getY() {
		return y;
	}

	/**
	 * Retrieves the z component
	 *
	 * @return z component
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Calculates a normalized vector
	 *
	 * @return new, normalized vector
	 */
	public Vector normalize() {
		final double len = getLength();
		if (len == 0) {
			return this;
		} else {
			return new Vector(x / len, y / len, z / len);
		}
	}

	/**
	 * Calculates the length of the vector
	 *
	 * @return length of the vector
	 */
	public double getLength() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Adds another vector
	 *
	 * @param other other vector
	 * @return new vector, sum of both vectors
	 */
	public Vector add(final Vector other) {
		if (this == Vector.ZERO) {
			return other;
		}
		if (other == Vector.ZERO) {
			return this;
		}
		return new Vector(x + other.getX(), y + other.getY(), z + other.getZ());
	}

	/**
	 * Subtracts another vector
	 *
	 * @param other other vector
	 * @return new vector, difference of both vectors
	 */
	public Vector sub(final Vector other) {
		return new Vector(x - other.getX(), y - other.getY(), z - other.getZ());
	}

	/**
	 * Calculates the inverse vector
	 *
	 * @return new, inverse vector
	 */
	public Vector invert() {
		return new Vector(-x, -y, -z);
	}

	/**
	 * Scales the vector
	 *
	 * @param factor scale factor
	 * @return new, scaled vector
	 */
	public Vector scale(final double factor) {
		return new Vector(factor * x, factor * y, factor * z);
	}

	/**
	 * Calculates the dot product
	 *
	 * @param other other vector
	 *
	 * @return dot product
	 */
	public double dot(final Vector other) {
		return getX() * other.getX() + getY() * other.getY() + getZ() * other.getZ();
	}

	/**
	 * Calculates the cross product
	 *
	 * @param other other vector
	 *
	 * @return new vector
	 */
	public Vector cross(final Vector other) {

		double x = getY() * other.getZ() - getZ() * other.getY();
		double y = getZ() * other.getX() - getX() * other.getZ();
		double z = getX() * other.getY() - getY() * other.getX();

		return new Vector(x, y, z);

	}

	@Override
	public String toString() {
		// output as x, y, z components
		return getLength() == 0 ? "[X 0, Y 0, Z 0]" : String.format("[X %1.6f, Y %1.6f, Z %1.6f]", x, y, z);
	}

	public boolean isNullVector() {
		return getLength() < EPSILON;
	}

	public boolean isEqualVector(Vector translation) {
		if (translation == this) {
			return true;
		}
		return Math.abs(translation.getX() - getX()) < EPSILON && Math.abs(translation.getY() - getY()) < EPSILON
				&& Math.abs(translation.getZ() - getZ()) < EPSILON;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		return obj instanceof Vector && x == ((Vector) obj).x && y == ((Vector) obj).y && z == ((Vector) obj).z;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(HashCodeUtil.hash(HashCodeUtil.hash(HashCodeUtil.SEED, x), y), z);
	}

	public static Vector getNullVector() {
		return ZERO;
	}

	public RealtimeVector asRealtimeValue() {
		return RealtimeVector.createFromConstant(this);
	}
}
