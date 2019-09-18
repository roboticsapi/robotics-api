/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import java.util.Arrays;

import org.roboticsapi.core.util.HashCodeUtil;

/**
 * A Robotics API 3x3 matrix (not necessarily orthonormal)
 */
public class Matrix3x3 {
	private double[][] matrix;

	protected void setMatrix(double[][] matrix) {
		this.matrix = matrix;
	}

	/**
	 * Creates a new identity matrix
	 */
	public Matrix3x3() {
		this(1, 0, 0, 0, 1, 0, 0, 0, 1);
	}

	/**
	 * Creates a new matrix from matrix values
	 *
	 * @param m11 matrix entry in row 1, column 1
	 * @param m12 matrix entry in row 1, column 2
	 * @param m13 matrix entry in row 1, column 3
	 * @param m21 matrix entry in row 2, column 1
	 * @param m22 matrix entry in row 2, column 2
	 * @param m23 matrix entry in row 2, column 3
	 * @param m31 matrix entry in row 3, column 1
	 * @param m32 matrix entry in row 3, column 2
	 * @param m33 matrix entry in row 3, column 3
	 */
	public Matrix3x3(final double m11, final double m12, final double m13, final double m21, final double m22,
			final double m23, final double m31, final double m32, final double m33) {
		matrix = new double[][] { { m11, m12, m13 }, { m21, m22, m23 }, { m31, m32, m33 } };
	}

	public double get(int row, int col) {
		return matrix[row][col];
	}

	/**
	 * Subtracts another rotation matrix
	 *
	 * @param other other rotation matrix
	 * @return new rotation matrix
	 */
	public Matrix3x3 sub(final Matrix3x3 other) {
		return new Matrix3x3(get(0, 0) - other.get(0, 0), get(0, 1) - other.get(0, 1), get(0, 2) - other.get(0, 2),
				get(1, 0) - other.get(1, 0), get(1, 1) - other.get(1, 1), get(1, 2) - other.get(1, 2),
				get(2, 0) - other.get(2, 0), get(2, 1) - other.get(2, 1), get(2, 2) - other.get(2, 2));
	}

	/**
	 * Applies the rotation to a given vector
	 *
	 * @param pos vector to rotate
	 * @return new, rotated vector
	 */
	public Vector apply(final Vector pos) {
		if (pos == Vector.ZERO) {
			return pos;
		}
		return new Vector(pos.getX() * get(0, 0) + pos.getY() * get(0, 1) + pos.getZ() * get(0, 2),
				pos.getX() * get(1, 0) + pos.getY() * get(1, 1) + pos.getZ() * get(1, 2),
				pos.getX() * get(2, 0) + pos.getY() * get(2, 1) + pos.getZ() * get(2, 2));
	}

	/**
	 * Multiplies the rotation with another rotation matrix
	 *
	 * @param other other rotation matrix
	 * @return new, combined rotation matrix
	 */
	public Matrix3x3 multiply(final Matrix3x3 other) {
		return new Matrix3x3(get(0, 0) * other.get(0, 0) + get(0, 1) * other.get(1, 0) + get(0, 2) * other.get(2, 0),
				get(0, 0) * other.get(0, 1) + get(0, 1) * other.get(1, 1) + get(0, 2) * other.get(2, 1),
				get(0, 0) * other.get(0, 2) + get(0, 1) * other.get(1, 2) + get(0, 2) * other.get(2, 2),

				get(1, 0) * other.get(0, 0) + get(1, 1) * other.get(1, 0) + get(1, 2) * other.get(2, 0),
				get(1, 0) * other.get(0, 1) + get(1, 1) * other.get(1, 1) + get(1, 2) * other.get(2, 1),
				get(1, 0) * other.get(0, 2) + get(1, 1) * other.get(1, 2) + get(1, 2) * other.get(2, 2),

				get(2, 0) * other.get(0, 0) + get(2, 1) * other.get(1, 0) + get(2, 2) * other.get(2, 0),
				get(2, 0) * other.get(0, 1) + get(2, 1) * other.get(1, 1) + get(2, 2) * other.get(2, 1),
				get(2, 0) * other.get(0, 2) + get(2, 1) * other.get(1, 2) + get(2, 2) * other.get(2, 2));
	}

	/**
	 * Calculates the inverted rotation matrix
	 *
	 * @return new, inverted matrix
	 */
	public Matrix3x3 invert() {
		double det = getDeterminant();
		if (det == 0) {
			throw new ArithmeticException("Determinant is 0, matrix cannot be inverted.");
		}
		double factor = 1 / det;
		return new Matrix3x3(//
				factor * (get(1, 1) * get(2, 2) - get(2, 1) * get(1, 2)), //
				factor * (get(1, 2) * get(2, 0) - get(2, 2) * get(1, 0)), //
				factor * (get(1, 0) * get(2, 1) - get(2, 0) * get(1, 1)), //
				factor * (get(0, 2) * get(2, 1) - get(2, 2) * get(0, 1)), //
				factor * (get(0, 0) * get(2, 2) - get(2, 0) * get(0, 2)), //
				factor * (get(0, 1) * get(2, 0) - get(2, 1) * get(0, 0)), //
				factor * (get(0, 1) * get(1, 2) - get(1, 1) * get(0, 2)), //
				factor * (get(0, 2) * get(1, 0) - get(1, 2) * get(0, 0)), //
				factor * (get(0, 0) * get(1, 1) - get(1, 0) * get(0, 1))).transpose();
	}

	/**
	 * Calculates the inverted rotation matrix (requires the unit vectors to be
	 * orthonormal)
	 *
	 * @return new, transposed matrix
	 */
	public Matrix3x3 transpose() {
		return new Matrix3x3(get(0, 0), get(1, 0), get(2, 0), get(0, 1), get(1, 1), get(2, 1), get(0, 2), get(1, 2),
				get(2, 2));
	}

	public double getDeterminant() {
		return get(0, 0) * get(1, 1) * get(2, 2) + get(0, 1) * get(1, 2) * get(2, 0) + get(0, 2) * get(1, 0) * get(2, 1)
				- get(0, 2) * get(1, 1) * get(2, 0) - get(0, 1) * get(1, 0) * get(2, 2)
				- get(0, 0) * get(1, 2) * get(2, 1);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Matrix3x3 && Arrays.equals(matrix[0], ((Matrix3x3) obj).matrix[0])
				&& Arrays.equals(matrix[1], ((Matrix3x3) obj).matrix[1])
				&& Arrays.equals(matrix[2], ((Matrix3x3) obj).matrix[2]);
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(HashCodeUtil.hash(HashCodeUtil.hash(HashCodeUtil.SEED, matrix[0]), matrix[1]),
				matrix[2]);
	}

}