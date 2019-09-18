/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.util.HashCodeUtil;
import org.roboticsapi.core.world.realtimevalue.RealtimePoint;

public class Point {
	private final Frame reference;
	private final Orientation orientation;
	private final Vector vector;

	public Point(Frame reference, Vector vector) {
		this(reference, reference == null ? null : reference.asOrientation(), vector);
	}

	public Point(Frame reference, Orientation orientation, Vector vector) {
		this.reference = reference;
		this.orientation = orientation;
		this.vector = vector;
	}

	public Frame getReferenceFrame() {
		return reference;
	}

	public Vector getVector() {
		return vector;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public String getName() {
		return "Point(" + (vector.getLength() == 0 ? getReferenceFrame()
				: "R: " + getReferenceFrame()
						+ (!getReferenceFrame().asOrientation().equals(orientation) ? ", O: " + orientation : "") + ", "
						+ vector)
				+ ")";
	}

	public boolean isEqualPoint(Point other) {
		return getReferenceFrame() == other.getReferenceFrame() && getVector().isEqualVector(other.getVector());
	}

	public Point plus(double x, double y, double z) {
		return plus(new Vector(x, y, z));
	}

	public Point plus(Vector vector) {
		return new Point(getReferenceFrame(), getOrientation(), getVector().add(vector));
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Point)) {
			return false;
		}
		Point point = (Point) obj;
		if (reference == null) {
			if (point.reference != null) {
				return false;
			}
		} else {
			if (!reference.equals(point.reference)) {
				return false;
			}
		}

		if (vector == null) {
			if (point.vector != null) {
				return false;
			}
		} else {
			if (!vector.equals(point.vector)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(HashCodeUtil.hash(HashCodeUtil.SEED, reference), vector);
	}

	@Override
	public String toString() {
		return getName();
	}

	public Pose asPose() {
		return new Pose(orientation, reference, new Transformation(Rotation.getIdentity(), vector));
	}

	public Vector getVectorForRepresentation(Frame newReference, Orientation newOrientation, FrameTopology topology)
			throws TransformationException {
		Vector vec = getVector();

		// convert to reference orientation
		if (orientation != reference.asOrientation()) {
			Transformation orientationChange = reference.asPose().getTransformationTo(getOrientation().asPose(),
					topology);
			vec = orientationChange.apply(vec);
		}

		// change reference
		if (reference != newReference) {
			Transformation relationSensor = newReference.getTransformationTo(getReferenceFrame(), topology);
			vec = relationSensor.apply(vec);
		}

		if (newOrientation != newReference.asOrientation()) {
			Transformation orientationChange = newOrientation.asPose().getTransformationTo(newReference.asPose(),
					topology);
			vec = orientationChange.apply(vec);
		}
		return vec;
	}

	public Point convertToRepresentation(Frame newReference, Orientation newOrientation, FrameTopology topology)
			throws TransformationException {
		return new Point(newReference, newOrientation,
				getVectorForRepresentation(newReference, newOrientation, topology));
	}

	public Point reinterpretToRepresentation(Frame newReference) {
		return new Point(newReference, newReference.asOrientation(), getVector());
	}

	public RealtimePoint asRealtimeValue() {
		return RealtimePoint.createFromConstant(this);
	}

}
