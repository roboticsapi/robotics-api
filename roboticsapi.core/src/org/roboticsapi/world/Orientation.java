/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

public class Orientation {
	private final Frame reference;
	private final Rotation rotation;

	public Orientation(Frame reference, Rotation rotation) {
		this.reference = reference;
		this.rotation = rotation;

	}

	public Frame getReferenceFrame() {
		return reference;
	}

	public Rotation getRotation() {
		return rotation;
	}

	@Override
	public String toString() {
		return "Orientation[R " + getReferenceFrame().toString() + ":A " + getRotation().getA() + ":B "
				+ getRotation().getB() + ":C " + getRotation().getC() + "]";
	}

	public boolean isEqualOrientation(Orientation other) {
		return getReferenceFrame().equals(other.getReferenceFrame())
				&& getRotation().isEqualRotation(other.getRotation());
	}
}
