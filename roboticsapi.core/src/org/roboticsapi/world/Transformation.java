/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.roboticsapi.core.util.HashCodeUtil;

/**
 * A Robotics API world model transformation (immutable matrix)
 */
public class Transformation {

	/** rotation matrix */
	private final Rotation rotation;

	/** translation */
	private final Vector translation;

	/**
	 * Creates a new transformation (identity)
	 */
	public Transformation() {
		this(new Rotation(), new Vector());
	}

	/**
	 * Creates a new transformation from rotation and translation
	 *
	 * @param rotation    rotation matrix
	 * @param translation translation vector
	 *
	 * @throws IllegalArgumentException if the given {@link Rotation} or the given
	 *                                  {@link Vector} is <code>null</code>.
	 */
	public Transformation(final Rotation rotation, final Vector translation) {
		if (rotation == null) {
			throw new IllegalArgumentException("The given rotation cannot be null.");
		}
		if (translation == null) {
			throw new IllegalArgumentException("The given vector cannot be null.");
		}
		this.rotation = rotation;
		this.translation = translation;
	}

	/**
	 * Creates a new transformation from given values for rotation and translation.
	 *
	 * @param x The translation in direction x.
	 * @param y The translation in direction y.
	 * @param z The translation in direction z.
	 * @param a The rotation around axis z (Alpha).
	 * @param b The rotation around axis y (Beta).
	 * @param c The rotation around axis x (Gamma).
	 */
	public Transformation(final double x, final double y, final double z, final double a, final double b,
			final double c) {
		this(new Rotation(a, b, c), new Vector(x, y, z));
	}

	/**
	 * Retrieves the rotation
	 *
	 * @return rotation matrix
	 */
	public Rotation getRotation() {
		return rotation;
	}

	/**
	 * Retrieves the yaw angle
	 *
	 * @return yaw angle in rad
	 */
	public double getA() {
		return rotation.getA();
	}

	/**
	 * Retrieves the pitch angle
	 *
	 * @return pitch angle in rad
	 */
	public double getB() {
		return rotation.getB();
	}

	/**
	 * Retrieves the roll angle
	 *
	 * @return roll angle in rad
	 */
	public double getC() {
		return rotation.getC();
	}

	/**
	 * Retrieves the translation
	 *
	 * @return translation vector
	 */
	public Vector getTranslation() {
		return translation;
	}

	/**
	 * Retrieves the x component
	 *
	 * @return x component
	 */
	public double getX() {
		return translation.getX();
	}

	/**
	 * Retrieves the y component
	 *
	 * @return y component
	 */
	public double getY() {
		return translation.getY();
	}

	/**
	 * Retrieves the z component
	 *
	 * @return z component
	 */
	public double getZ() {
		return translation.getZ();
	}

	/**
	 * Applies the transformation to a vector
	 *
	 * @param pos vector to apply transformation to
	 * @return new, transformed vector
	 */
	public Vector apply(final Vector pos) {
		return rotation.apply(pos).add(translation);
	}

	/**
	 * Combines two transformations
	 *
	 * @param other other translation to execute afterwards
	 * @return new, combined transformation
	 */
	public Transformation multiply(final Transformation other) {
		return new Transformation(rotation.multiply(other.rotation),
				rotation.apply(other.translation).add(translation));
	}

	/**
	 * Calculates the inverted transformation
	 *
	 * @return new, inverted transformation
	 */
	public Transformation invert() {
		final Rotation rot = rotation.invert();
		return new Transformation(rot, rot.apply(translation).invert());
	}

	public boolean isEqualTransformation(Transformation other) {
		return getRotation().isEqualRotation(other.getRotation())
				&& getTranslation().isEqualVector(other.getTranslation());
	}

	@Override
	public String toString() {
		// output as translation vector and rotation angles
		return translation.toString() + " " + rotation.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Transformation && rotation.equals(((Transformation) obj).rotation)
				&& translation.equals(((Transformation) obj).translation);
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(HashCodeUtil.hash(HashCodeUtil.SEED, rotation), translation);
	}

}
