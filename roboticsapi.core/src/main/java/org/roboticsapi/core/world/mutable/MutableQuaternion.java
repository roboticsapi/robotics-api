/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.mutable;

public class MutableQuaternion {
	MutableVector xyz = new MutableVector();
	double w = 1;

	public MutableVector getVector() {
		return xyz;
	}

	public double getX() {
		return xyz.getX();
	}

	public void setX(double x) {
		xyz.setX(x);
	}

	public double getY() {
		return xyz.getY();
	}

	public void setY(double y) {
		xyz.setY(y);
	}

	public double getZ() {
		return xyz.getZ();
	}

	public void setZ(double z) {
		xyz.setZ(z);
	}

	public double getW() {
		return w;
	}

	public void setW(double w) {
		this.w = w;
	}

	public void set(double x, double y, double z, double w) {
		xyz.set(x, y, z);
		this.w = w;
	}

	@Override
	public String toString() {
		return "{X: " + getX() + ", Y: " + getY() + ", Z: " + getZ() + ", W: " + getW() + "}";
	}
}
