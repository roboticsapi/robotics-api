/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

public class Direction {

	private final Orientation orientation;
	private final Vector value;

	public Direction(Orientation orientation, Vector value) {
		this.orientation = orientation;
		this.value = value;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public Vector getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Direction[O " + getOrientation().toString() + ":X " + getValue().getX() + ":Y " + getValue().getY()
				+ ":Z " + getValue().getZ() + "]";
	}

	public boolean isEqualDirection(Direction other) {
		return getOrientation().isEqualOrientation(other.getOrientation())
				&& getValue().isEqualVector(other.getValue());
	}

	public Vector getVectorForRepresentation(Orientation newOrientation, FrameTopology topology)
			throws TransformationException {
		Transformation transformationSensor = newOrientation.asPose().getTransformationTo(getOrientation().asPose(),
				topology);
		return transformationSensor.getRotation().apply(value);
	}

	public Direction convertToRepresentation(Orientation newOrientation, FrameTopology topology)
			throws TransformationException {
		return new Direction(newOrientation, getVectorForRepresentation(newOrientation, topology));
	}

	public Direction reinterpretToRepresentation(Orientation newOrientation) {
		return new Direction(newOrientation, value);
	}

}
