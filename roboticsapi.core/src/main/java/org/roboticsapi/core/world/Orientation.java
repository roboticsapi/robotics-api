/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.util.HashCodeUtil;
import org.roboticsapi.core.world.realtimevalue.RealtimeOrientation;

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
		if (rotation.isEqualRotation(Rotation.getIdentity())) {
			if (getReferenceFrame() == null) {
				return "Orientation(local)";
			} else {
				return "Orientation(R: " + getReferenceFrame() + ")";
			}
		} else {
			if (getReferenceFrame() == null) {
				return "Orientation(local, " + rotation + ")";
			} else {
				return "Orientation(R: " + getReferenceFrame() + ", " + rotation + ")";
			}
		}
	}

	public boolean isEqualOrientation(Orientation other) {
		return getReferenceFrame() == other.getReferenceFrame() && getRotation().isEqualRotation(other.getRotation());
	}

	/**
	 * Converts this {@link Orientation} into a {@link Pose}.
	 *
	 * @return this orientation as a {@link Pose}.
	 * @throws TransformationException
	 *
	 * @see {@link Pose#getLocalOrientationAsPose(Orientation)}
	 */
	public Pose asPose() throws TransformationException {
		if (reference == null) {
			throw new TransformationException("Local orientation can only be converted in the context of a pose.");
		}
		return reference.asPose().plus(rotation);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Orientation && reference.equals(((Orientation) obj).reference)
				&& rotation.equals(((Orientation) obj).rotation);
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(HashCodeUtil.hash(HashCodeUtil.SEED, reference), rotation);
	}

	public RealtimeOrientation asRealtimeValue() {
		return RealtimeOrientation.createFromRotation(rotation.asRealtimeValue(), reference);
	}

	public Rotation getRotation(Frame newReference, FrameTopology topology) throws TransformationException {
		Transformation refChange = newReference.getTransformationTo(reference, topology);
		return refChange.multiply(new Transformation(getRotation(), Vector.ZERO)).getRotation();
	}

	public Orientation convert(Frame newReference, FrameTopology topology) throws TransformationException {
		return new Orientation(newReference, getRotation(newReference, topology));
	}

	public Orientation reinterpret(Frame newReference) {
		return new Orientation(newReference, getRotation());
	}

}
