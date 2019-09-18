/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

public class Quaternion {
	private final double x, y, z, w;

	public Quaternion(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double getW() {
		return w;
	}

	public Vector getAxis() {
		if (Math.abs(w) > 0.99999) {
			return new Vector();
		} else {
			return new Vector(x, y, z).normalize();
		}
	}

	public double getAngle() {
		return Math.acos(w) * 2;
	}
}
