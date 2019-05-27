/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.mutable;

public class MutableTransformation {
	MutableRotation rotation = new MutableRotation();
	MutableVector translation = new MutableVector();

	public MutableTransformation() {
	}

	public MutableTransformation(double x, double y, double z, double a, double b, double c) {
		this();
		setVectorEuler(x, y, z, a, b, c);
	}

	public MutableRotation getRotation() {
		return rotation;
	}

	public MutableVector getTranslation() {
		return translation;
	}

	public void setMatrixVector(double q00, double q01, double q02, double q10, double q11, double q12, double q20,
			double q21, double q22, double x, double y, double z) {
		rotation.setMatrix(q00, q01, q02, q10, q11, q12, q20, q21, q22);
		translation.set(x, y, z);
	}

	public void setVectorEuler(double x, double y, double z, double a, double b, double c) {
		rotation.setEuler(a, b, c);
		translation.set(x, y, z);
	}

	public void copyTo(MutableTransformation ret) {
		getRotation().copyTo(ret.getRotation());
		getTranslation().copyTo(ret.getTranslation());
	}

	public void addDelta(MutableTwist vel, double dt) {
		addDeltaTo(vel, dt, this);
	}

	public void addDeltaTo(MutableTwist vel, double dt, MutableTransformation ret) {
		getTranslation().addDeltaTo(vel.getTranslation(), dt, ret.getTranslation());
		getRotation().addDeltaTo(vel.getRotation(), dt, ret.getRotation());
	}

	public void getDeltaTo(MutableTransformation other, double dt, MutableTwist ret) {
		getTranslation().getDeltaTo(other.getTranslation(), dt, ret.getTranslation());
		getRotation().getDeltaTo(other.getRotation(), dt, ret.getRotation());
	}

	public void invert() {
		invertTo(this);
	}

	public void invertTo(MutableTransformation ret) {
		getRotation().invertTo(ret.getRotation());
		getTranslation().rotateTo(ret.getRotation(), ret.getTranslation());
		ret.getTranslation().scale(-1);
	}

	public void multiply(MutableTransformation other) {
		multiplyTo(other, this);
	}

	public void multiplyTo(MutableTransformation other, MutableTransformation ret) {
		double x = getTranslation().getX(), y = getTranslation().getY(), z = getTranslation().getZ();
		other.getTranslation().rotateTo(getRotation(), ret.getTranslation());
		ret.getTranslation().set(ret.getTranslation().getX() + x, ret.getTranslation().getY() + y,
				ret.getTranslation().getZ() + z);
		getRotation().multiplyTo(other.getRotation(), ret.getRotation());
	}

	@Override
	public String toString() {
		return "{" + translation + ", " + rotation + "}";
	}
}
