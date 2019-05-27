/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.mutable;

public class MutableVector {
	double[] vector = new double[3];

	public double getX() {
		return vector[0];
	}

	public void setX(double x) {
		vector[0] = x;
	}

	public double getY() {
		return vector[1];
	}

	public void setY(double y) {
		vector[1] = y;
	}

	public double getZ() {
		return vector[2];
	}

	public void setZ(double z) {
		vector[2] = z;
	}

	static void set(double x, double y, double z, double[] ret) {
		ret[0] = x;
		ret[1] = y;
		ret[2] = z;
	}

	public void set(double x, double y, double z) {
		set(x, y, z, vector);
	}

	public double getLength() {
		return length(vector[0], vector[1], vector[2]);
	}

	public static double length(double x, double y, double z) {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public void normalizeTo(MutableVector ret) {
		scaleTo(1 / getLength(), ret);
	}

	public void normalize() {
		normalizeTo(this);
	}

	public void scaleTo(double factor, MutableVector ret) {
		scale(vector, factor, ret.vector);
	}

	public void scale(double factor) {
		scaleTo(factor, this);
	}

	public void rotate(MutableRotation rotation) {
		rotateTo(rotation, this);
	}

	public void rotateTo(MutableRotation rotation, MutableVector ret) {
		rotate(vector, rotation.matrix, ret.vector);
	}

	public void unRotate(MutableRotation rotation) {
		unRotateTo(rotation, this);
	}

	public void unRotateTo(MutableRotation rotation, MutableVector ret) {
		ret.set(getX() * rotation.getQ00() + getY() * rotation.getQ10() + getZ() * rotation.getQ20(), //
				getX() * rotation.getQ01() + getY() * rotation.getQ11() + getZ() * rotation.getQ21(), //
				getX() * rotation.getQ02() + getY() * rotation.getQ12() + getZ() * rotation.getQ22());
	}

	public void add(MutableVector other) {
		addTo(other, this);
	}

	public void addTo(MutableVector other, MutableVector ret) {
		ret.set(getX() + other.getX(), getY() + other.getY(), getZ() + other.getZ());
	}

	public double dot(MutableVector other) {
		return getX() * other.getX() + getY() * other.getY() + getZ() * other.getZ();
	}

	public void cross(MutableVector other) {
		crossTo(other, this);
	}

	public void crossTo(MutableVector other, MutableVector ret) {
		double x = getY() * other.getZ() - getZ() * other.getY();
		double y = getZ() * other.getX() - getX() * other.getZ();
		double z = getX() * other.getY() - getY() * other.getX();
		ret.set(x, y, z);
	}

	public void addDelta(MutableVector vel, double dt) {
		addDeltaTo(vel, dt, this);
	}

	public void addDeltaTo(MutableVector vel, double dt, MutableVector ret) {
		ret.set(getX() + vel.getX() * dt, getY() + vel.getY() * dt, getZ() + vel.getZ() * dt);
	}

	public void getDeltaTo(MutableVector other, double dt, MutableVector ret) {
		ret.set((other.getX() - getX()) / dt, (other.getY() - getY()) / dt, (other.getZ() - getZ()) / dt);
	}

	public void copyTo(MutableVector ret) {
		ret.set(getX(), getY(), getZ());
	}

	public void transform(MutableTransformation transformation) {
		transformTo(transformation, this);
	}

	public void transformTo(MutableTransformation transformation, MutableVector ret) {
		ret.set(getX() * transformation.getRotation().getQ00() + getY() * transformation.getRotation().getQ01()
				+ getZ() * transformation.getRotation().getQ02() + transformation.getTranslation().getX(), //
				getX() * transformation.getRotation().getQ10() + getY() * transformation.getRotation().getQ11()
						+ getZ() * transformation.getRotation().getQ12() + transformation.getTranslation().getY(), //
				getX() * transformation.getRotation().getQ20() + getY() * transformation.getRotation().getQ21()
						+ getZ() * transformation.getRotation().getQ22() + transformation.getTranslation().getZ());
	}

	@Override
	public String toString() {
		return "{X: " + getX() + ", Y: " + getY() + ", Z: " + getZ() + "}";
	}

	static void rotate(double[] vector, double[][] rotation, double[] ret) {
		set(vector[0] * rotation[0][0] + vector[1] * rotation[0][1] + vector[2] * rotation[0][2], //
				vector[0] * rotation[1][0] + vector[1] * rotation[1][1] + vector[2] * rotation[1][2], //
				vector[0] * rotation[2][0] + vector[1] * rotation[2][1] + vector[2] * rotation[2][2], ret);

	}

	static void scale(double[] vector, double factor, double[] ret) {
		set(vector[0] * factor, vector[1] * factor, vector[2] * factor, ret);
	}

}
