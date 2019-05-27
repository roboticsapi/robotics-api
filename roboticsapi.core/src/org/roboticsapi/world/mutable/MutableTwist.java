/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.mutable;

public class MutableTwist {
	MutableVector translation = new MutableVector();
	MutableVector rotation = new MutableVector();

	public MutableVector getTranslation() {
		return translation;
	}

	public MutableVector getRotation() {
		return rotation;
	}

	public void set(double tx, double ty, double tz, double rx, double ry, double rz) {
		translation.set(tx, ty, tz);
		rotation.set(rx, ry, rz);
	}

	public void getDeltaTo(MutableTwist other, double dt, MutableTwist ret) {
		getTranslation().getDeltaTo(other.getTranslation(), dt, ret.getTranslation());
		getRotation().getDeltaTo(other.getRotation(), dt, ret.getRotation());
	}

	public void addDelta(MutableTwist other, double dt) {
		addDeltaTo(other, dt, this);
	}

	public void addDeltaTo(MutableTwist other, double dt, MutableTwist ret) {
		getTranslation().addDeltaTo(other.getTranslation(), dt, ret.getTranslation());
		getRotation().addDeltaTo(other.getRotation(), dt, ret.getRotation());
	}

	@Override
	public String toString() {
		return "{" + translation + ", " + rotation + "}";
	}
}
