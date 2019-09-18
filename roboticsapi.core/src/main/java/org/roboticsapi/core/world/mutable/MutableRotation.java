/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.mutable;

public class MutableRotation {
	double[][] matrix = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
	double[][] tmp = { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };
	double[] tmpvec = { 0, 0, 0 };

	public void setMatrix(double q00, double q01, double q02, double q10, double q11, double q12, double q20,
			double q21, double q22) {
		setMatrix(q00, q01, q02, q10, q11, q12, q20, q21, q22, matrix);
	}

	public double getQ00() {
		return matrix[0][0];
	}

	public double getQ01() {
		return matrix[0][1];
	}

	public double getQ02() {
		return matrix[0][2];
	}

	public double getQ10() {
		return matrix[1][0];
	}

	public double getQ11() {
		return matrix[1][1];
	}

	public double getQ12() {
		return matrix[1][2];
	}

	public double getQ20() {
		return matrix[2][0];
	}

	public double getQ21() {
		return matrix[2][1];
	}

	public double getQ22() {
		return matrix[2][2];
	}

	public void setQuaternion(double qx, double qy, double qz, double qw) {
		double angle = Math.acos(qw) * 2;
		double x, y, z;
		if (Math.abs(qw) > 0.99999) {
			x = y = z = 0;
		} else {
			double len = MutableVector.length(qx, qy, qz);
			x = qx / len;
			y = qy / len;
			z = qz / len;
		}
		setAxis(x * angle, y * angle, z * angle);
	}

	public void setQuaternion(MutableQuaternion q) {
		setQuaternion(q.getX(), q.getY(), q.getZ(), q.getW());
	}

	public void getQuaternionTo(MutableQuaternion ret) {
		double angle = getAngle();
		getUnitAxisTo(ret.getVector());
		double s = Math.sin(angle / 2);
		ret.getVector().scale(s);
		ret.setW(Math.cos(angle / 2));
	}

	static void setAxis(double x, double y, double z, double[][] ret) {
		double angle = MutableVector.length(x, y, z);
		double scale = angle == 0 ? 1 : 1 / angle;
		final double c = Math.cos(angle), s = Math.sin(angle), ax = x * scale, ay = y * scale, az = z * scale;
		setMatrix(ax * ax + (1 - ax * ax) * c, ax * ay * (1 - c) - az * s, ax * az * (1 - c) + ay * s, //
				ax * ay * (1 - c) + az * s, ay * ay + (1 - ay * ay) * c, ay * az * (1 - c) - ax * s, //
				ax * az * (1 - c) - ay * s, ay * az * (1 - c) + ax * s, az * az + (1 - az * az) * c, ret);
	}

	public void setAxis(double x, double y, double z) {
		setAxis(x, y, z, matrix);
	}

	public void getAxisTo(MutableVector a) {
		getAxisTo(matrix, a);
	}

	static void getAxisTo(double[][] rot, MutableVector a) {
		getUnitAxisTo(rot, a);
		a.scale(getAngle(rot));
	}

	public void setAxis(MutableVector a) {
		setAxis(a.getX(), a.getY(), a.getZ());
	}

	public void setEuler(double z, double y, double x) {
		final double sa = Math.sin(z), sb = Math.sin(y), sc = Math.sin(x);
		final double ca = Math.cos(z), cb = Math.cos(y), cc = Math.cos(x);
		setMatrix(ca * cb, ca * sb * sc - sa * cc, ca * sb * cc + sa * sc, sa * cb, sa * sb * sc + ca * cc,
				sa * sb * cc - ca * sc, -sb, cb * sc, cb * cc);
	}

	public void getEulerTo(MutableVector ret) {
		double a, b, c;
		b = Math.atan2(-getQ20(), Math.sqrt(getQ00() * getQ00() + getQ10() * getQ10()));

		if (MutableDouble.near(Math.abs(b), Math.PI / 2)) {
			a = 0;
		} else {
			a = Math.atan2(getQ10(), getQ00());
		}

		if (MutableDouble.near(b, Math.PI / 2)) {
			c = Math.atan2(getQ01(), getQ11());
		} else if (MutableDouble.near(b, -Math.PI / 2)) {
			c = -Math.atan2(getQ01(), getQ11());
		} else {
			c = Math.atan2(getQ21(), getQ22());
		}
		ret.set(c, b, a);
	}

	public void setEuler(MutableVector v) {
		setEuler(v.getZ(), v.getY(), v.getX());
	}

	static double getAngle(double[][] rot) {
		double cos = (rot[0][0] + rot[1][1] + rot[2][2] - 1) / 2;
		if (cos > 1) {
			cos = 1;
		}
		if (cos < -1) {
			cos = -1;
		}
		return Math.acos(cos);
	}

	public double getAngle() {
		return getAngle(matrix);
	}

	static void getUnitAxisTo(double[][] rot, MutableVector ret) {
		double angle = getAngle(rot);
		final double sin = Math.sin(angle);
		// see http://www.springerlink.com/content/p824162625k27p08
		if (MutableDouble.near(angle, 0)) {
			// no rotation, give any axis
			ret.set(1, 0, 0);
		} else if (MutableDouble.near(angle, Math.PI) || MutableDouble.near(angle, -Math.PI)) {
			// find an axis for 180 deg rotation
			double x = Math.sqrt((rot[0][0] + 1) / 2);
			double y = Math.sqrt((rot[1][1] + 1) / 2);
			double z = Math.sqrt((rot[2][2] + 1) / 2);
			ret.set(x, y, z);
			ret.normalize();
		} else {
			// find axis
			ret.set((rot[2][1] - rot[1][2]) / (2 * sin), (rot[0][2] - rot[2][0]) / (2 * sin),
					(rot[1][0] - rot[0][2]) / (2 * sin));
			ret.normalize();
		}

	}

	public void getUnitAxisTo(MutableVector ret) {
		getUnitAxisTo(matrix, ret);
	}

	public void multiplyTo(MutableRotation other, MutableRotation ret) {
		multiply(matrix, other.matrix, ret.matrix);
	}

	static void multiply(double[][] first, double[][] second, double[][] ret) {
		setMatrix(first[0][0] * second[0][0] + first[0][1] * second[1][0] + first[0][2] * second[2][0], //
				first[0][0] * second[0][1] + first[0][1] * second[1][1] + first[0][2] * second[2][1], //
				first[0][0] * second[0][2] + first[0][1] * second[1][2] + first[0][2] * second[2][2], //
				first[1][0] * second[0][0] + first[1][1] * second[1][0] + first[1][2] * second[2][0], //
				first[1][0] * second[0][1] + first[1][1] * second[1][1] + first[1][2] * second[2][1], //
				first[1][0] * second[0][2] + first[1][1] * second[1][2] + first[1][2] * second[2][2], //
				first[2][0] * second[0][0] + first[2][1] * second[1][0] + first[2][2] * second[2][0], //
				first[2][0] * second[0][1] + first[2][1] * second[1][1] + first[2][2] * second[2][1], //
				first[2][0] * second[0][2] + first[2][1] * second[1][2] + first[2][2] * second[2][2], ret);
	}

	public void multiply(MutableRotation other) {
		multiplyTo(other, this);
	}

	public void invert() {
		invertTo(this);
	}

	public void invertTo(MutableRotation ret) {
		invert(matrix, ret.matrix);
	}

	static void setMatrix(double q00, double q01, double q02, double q10, double q11, double q12, double q20,
			double q21, double q22, double[][] ret) {
		ret[0][0] = q00;
		ret[0][1] = q01;
		ret[0][2] = q02;
		ret[1][0] = q10;
		ret[1][1] = q11;
		ret[1][2] = q12;
		ret[2][0] = q20;
		ret[2][1] = q21;
		ret[2][2] = q22;
	}

	static void invert(double[][] rot, double[][] ret) {
		setMatrix(rot[0][0], rot[1][0], rot[2][0], //
				rot[0][1], rot[1][1], rot[2][1], //
				rot[0][2], rot[1][2], rot[2][2], ret);
	}

	public void addDelta(MutableVector rot, double dt) {
		addDeltaTo(rot, dt, this);
	}

	public void addDeltaTo(MutableVector rot, double dt, MutableRotation ret) {
		invert(matrix, tmp);
		MutableVector.rotate(rot.vector, tmp, tmpvec);
		MutableVector.scale(tmpvec, dt, tmpvec);
		setAxis(tmpvec[0], tmpvec[1], tmpvec[2], tmp);
		multiply(matrix, tmp, ret.matrix);
	}

	public void getDeltaTo(MutableRotation other, double dt, MutableVector ret) {
		invert(matrix, tmp);
		multiply(tmp, other.matrix, tmp);
		getAxisTo(tmp, ret);
		ret.scale(1 / dt);
		ret.rotate(this);
	}

	public void copyTo(MutableRotation ret) {
		ret.setMatrix(getQ00(), getQ01(), getQ02(), //
				getQ10(), getQ11(), getQ12(), //
				getQ20(), getQ21(), getQ22());
	}

	public double getA() {
		double a, b = getB();

		if (MutableDouble.near(Math.abs(b), Math.PI / 2)) {
			a = 0;
		} else {
			a = Math.atan2(getQ10(), getQ00());
		}
		return a;
	}

	public double getB() {
		double b = Math.atan2(-getQ20(), Math.sqrt(getQ00() * getQ00() + getQ10() * getQ10()));
		return b;
	}

	public double getC() {
		double b = getB(), c;

		if (MutableDouble.near(b, Math.PI / 2)) {
			c = Math.atan2(getQ01(), getQ11());
		} else if (MutableDouble.near(b, -Math.PI / 2)) {
			c = -Math.atan2(getQ01(), getQ11());
		} else {
			c = Math.atan2(getQ21(), getQ22());
		}
		return c;
	}

	public double get(int row, int col) {
		return matrix[row][col];
	}

	public void set(int row, int col, double value) {
		matrix[row][col] = value;
	}

	@Override
	public String toString() {
		return "{A: " + getA() + ", B: " + getB() + ", C: " + getC() + "}";
	}

}
