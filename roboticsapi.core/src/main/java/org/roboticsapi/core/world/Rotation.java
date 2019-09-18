/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;

/**
 * A Robotics API world model rotation (immutable matrix)
 */
public class Rotation extends Matrix3x3 {
	public static final Rotation IDENTITY = new Rotation();

	/**
	 * Creates a new rotation matrix (identity)
	 */
	private Rotation() {
		this(1, 0, 0, 0, 1, 0, 0, 0, 1);
	}

	/**
	 * Creates a new rotation matrix from three unit vectors
	 *
	 * @param x rotated x unit vector
	 * @param y rotated y unit vector
	 * @param z rotated z unit vector
	 */
	public Rotation(final Vector x, final Vector y, final Vector z) {
		setMatrix(x, y, z);
	}

	public Rotation(Quaternion q) {
		this(q.getAxis(), q.getAngle());
	}

	public Quaternion getQuaternion() {
		double angle = getAngle();
		Vector axis = getAxis();
		double s = Math.sin(angle / 2);
		return new Quaternion(axis.getX() * s, axis.getY() * s, axis.getZ() * s, Math.cos(angle / 2));
	}

	private void setMatrix(final Vector x, final Vector y, final Vector z) {
		setMatrix(new double[][] { { x.getX(), y.getX(), z.getX() }, { x.getY(), y.getY(), z.getY() },
				{ x.getZ(), y.getZ(), z.getZ() } });

		if (Math.abs(getDeterminant() - 1) > 0.001) {
			throw new IllegalArgumentException("Invalid rotation matrix");
		}
	}

	/**
	 * Creates a new rotation matrix from matrix values
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
	public Rotation(final double m11, final double m12, final double m13, final double m21, final double m22,
			final double m23, final double m31, final double m32, final double m33) {
		super(m11, m12, m13, m21, m22, m23, m31, m32, m33);

		if (Math.abs(getDeterminant() - 1) > 0.001) {
			throw new IllegalArgumentException("Invalid rotation matrix");
		}
	}

	/**
	 * Creates a new rotation matrix from yaw-pitch-roll angles
	 *
	 * @param a yaw angle in rad
	 * @param b pich angle in rad
	 * @param c roll angle in rad
	 */
	public Rotation(final double a, final double b, final double c) {
		final double sa = Math.sin(a), sb = Math.sin(b), sc = Math.sin(c);
		final double ca = Math.cos(a), cb = Math.cos(b), cc = Math.cos(c);
		Vector x = new Vector(ca * cb, sa * cb, -sb);
		Vector y = new Vector(ca * sb * sc - sa * cc, sa * sb * sc + ca * cc, cb * sc);
		Vector z = new Vector(ca * sb * cc + sa * sc, sa * sb * cc - ca * sc, cb * cc);
		setMatrix(x, y, z);
	}

	/**
	 * Creates a new rotation matrix as rotation around a given axis
	 *
	 * @param axis  axis to rotate around
	 * @param angle angle to rotate around the given axis (rad)
	 */
	public Rotation(Vector axis, final double angle) {
		axis = axis.normalize();
		final double c = Math.cos(angle), s = Math.sin(angle), ax = axis.getX(), ay = axis.getY(), az = axis.getZ();
		Vector x = new Vector(ax * ax + (1 - ax * ax) * c, ax * ay * (1 - c) + az * s, ax * az * (1 - c) - ay * s);
		Vector y = new Vector(ax * ay * (1 - c) - az * s, ay * ay + (1 - ay * ay) * c, ay * az * (1 - c) + ax * s);
		Vector z = new Vector(ax * az * (1 - c) + ay * s, ay * az * (1 - c) - ax * s, az * az + (1 - az * az) * c);
		setMatrix(x, y, z);
	}

	private boolean near(final double a, final double b) {
		return Math.abs(a - b) < 0.001;
	}

	/**
	 * Retrieves the yaw angle
	 *
	 * @return yaw angle in rad
	 */
	public double getA() {
		final double b = getB();
		if (near(Math.abs(b), Math.PI / 2)) {
			return 0;
		}
		return Math.atan2(get(1, 0), get(0, 0));
	}

	/**
	 * Retrieves the pitch angle
	 *
	 * @return pitch angle in rad
	 */
	public double getB() {
		return Math.atan2(-get(2, 0), Math.sqrt(get(0, 0) * get(0, 0) + get(1, 0) * get(1, 0)));
	}

	/**
	 * Retrieves the roll angle
	 *
	 * @return roll angle in rad
	 */
	public double getC() {
		final double b = getB();
		if (near(b, Math.PI / 2)) {
			return Math.atan2(get(0, 1), get(1, 1));
		}
		if (near(b, -Math.PI / 2)) {
			return -Math.atan2(get(0, 1), get(1, 1));
		}
		return Math.atan2(get(2, 1), get(2, 2));
	}

	/**
	 * Gets the angle Phi, i.e. first rotation: around z axis
	 *
	 * @return Phi in rad
	 */
	public double getPhi() {
		return Math.atan2(get(1, 2), get(0, 1));
	}

	/**
	 * Gets the angle Theta, i.e. second rotation: around new y axis (after // Phi
	 * rotation)
	 *
	 * @return Theta in rad
	 */
	public double getTheta() {
		return Math.atan2(Math.sqrt(get(0, 1) * get(0, 1) + get(1, 2) * get(1, 2)), get(2, 2));
	}

	/**
	 * Gets the angle Psi, i.e. third rotation: around new z axis (after Theta
	 * rotation)
	 *
	 * @return Psi in rad
	 */
	public double getPsi() {
		return Math.atan2(get(2, 1), -get(2, 0));
	}

	/**
	 * Retrieves the x unit vector
	 *
	 * @return x unit vector
	 */
	public Vector getX() {
		return new Vector(get(0, 0), get(1, 0), get(2, 0));
	}

	/**
	 * Retrieves the y unit vector
	 *
	 * @return y unit vector
	 */
	public Vector getY() {
		return new Vector(get(0, 1), get(1, 1), get(2, 1));
	}

	/**
	 * Retrieves the z unit vector
	 *
	 * @return z unit vector
	 */
	public Vector getZ() {
		return new Vector(get(0, 2), get(1, 2), get(2, 2));
	}

	/**
	 * Retrieves the rotation axis
	 *
	 * @return vector describing the rotation axis
	 */
	public Vector getAxis() {
		double angle = getAngle();
		final double sin = Math.sin(angle);
		// see http://www.springerlink.com/content/p824162625k27p08
		if (near(angle, 0)) {
			// no rotation, give any axis
			return new Vector(1, 0, 0);
		} else if (near(angle, Math.PI) || near(angle, -Math.PI)) {
			// find an axis for 180 deg rotation
			double x = Math.sqrt((get(0, 0) + 1) / 2);
			double y = Math.sqrt((get(1, 1) + 1) / 2);
			double z = Math.sqrt((get(2, 2) + 1) / 2);
			if (get(0, 2) < 0) {
				x = -x;
			}
			if (get(2, 2) < 0) {
				y = -y;
			}
			if (x * y * get(0, 1) < 0) {
				x = -x;
			}
			return new Vector(x, y, z);
		} else {
			// find axis
			return new Vector((get(2, 1) - get(1, 2)) / (2 * sin), (get(0, 2) - get(2, 0)) / (2 * sin),
					(get(1, 0) - get(0, 1)) / (2 * sin));
		}
	}

	/**
	 * Retrieves the rotation angle around getAxis()
	 *
	 * @return rotation angle (rad)
	 */
	public double getAngle() {
		double cos = (get(0, 0) + get(1, 1) + get(2, 2) - 1) / 2;
		if (cos > 1) {
			cos = 1;
		}
		if (cos < -1) {
			cos = -1;
		}
		return Math.acos(cos);
	}

	/**
	 * Converts degrees into radians
	 *
	 * @param deg angle in degrees
	 * @return angle in radians
	 */
	public static double deg2rad(final double deg) {
		return deg * Math.PI / 180;
	}

	public static Rotation getIdentity() {
		return IDENTITY;
	}

	/**
	 * Converts radians into degrees
	 *
	 * @param rad angle in radians
	 * @return angle in degrees
	 */
	public static double rad2deg(final double rad) {
		return rad / Math.PI * 180;
	}

	public Rotation multiply(final Rotation other) {
		if (other == Rotation.IDENTITY) {
			return this;
		}
		if (this == Rotation.IDENTITY) {
			return other;
		}
		Matrix3x3 rot = super.multiply(other);
		return new Rotation(rot.get(0, 0), rot.get(0, 1), rot.get(0, 2), rot.get(1, 0), rot.get(1, 1), rot.get(1, 2),
				rot.get(2, 0), rot.get(2, 1), rot.get(2, 2));
	}

	@Override
	public Vector apply(Vector pos) {
		if (this == Rotation.IDENTITY) {
			return pos;
		}
		return super.apply(pos);
	}

	@Override
	public Rotation invert() {
		Matrix3x3 rot = transpose();
		return new Rotation(rot.get(0, 0), rot.get(0, 1), rot.get(0, 2), rot.get(1, 0), rot.get(1, 1), rot.get(1, 2),
				rot.get(2, 0), rot.get(2, 1), rot.get(2, 2));
	}

	@Override
	public String toString() {
		// display as yaw-pitch-roll
		return getAngle() < 1e-16 ? "[A 0\u00B0, B 0\u00B0, C 0\u00B0]"
				: String.format("[A %1.6f\u00B0, B %1.6f\u00B0, C %1.6f\u00B0]", rad2deg(getA()), rad2deg(getB()),
						rad2deg(getC()));
	}

	public boolean isEqualRotation(Rotation rotation) {
		if (rotation == this) {
			return true;
		}

		// TODO: is this check valid?
		return getX().isEqualVector(rotation.getX()) && getY().isEqualVector(rotation.getY())
				&& getZ().isEqualVector(rotation.getZ());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		return obj instanceof Rotation && super.equals(obj);
	}

	public boolean isIdentityRotation() {
		return isEqualRotation(IDENTITY);
	}

	public RealtimeRotation asRealtimeValue() {
		return RealtimeRotation.createFromConstant(this);
	}

}
