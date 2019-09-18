/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue;

import java.util.Map;

import org.roboticsapi.core.Observer;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.Orientation;
import org.roboticsapi.core.world.Point;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public final class RealtimePoint extends RealtimeValue<Point> {

	@Override
	public final RealtimePoint substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimePoint) substitutionMap.get(this);
		}
		return new RealtimePoint(vector.substitute(substitutionMap), reference);
	}

	private final Frame reference;
	private final Orientation orientation;
	private final RealtimeVector vector;

	RealtimePoint(RealtimeVector vector, Frame reference) {
		this(vector, reference, reference == null ? null : reference.asOrientation());
	}

	RealtimePoint(RealtimeVector vector, Frame referenceFrame, Orientation orientation) {
		super(vector);
		this.vector = vector;
		this.reference = referenceFrame;
		this.orientation = orientation;
	}

	@Override
	public Observer<?> createObserver(final RealtimeValueListener<Point> listener, RealtimeBoolean condition,
			boolean async) {
		return vector.createObserver(new RealtimeValueListener<Vector>() {
			@Override
			public void onValueChanged(Vector newValue) {
				listener.onValueChanged(wrap(newValue));
			}
		}, condition, async);
	}

	@Override
	protected Point calculateCheapValue() {
		return wrap(vector.getCheapValue());
	}

	private Point wrap(Vector newValue) {
		if (newValue == null) {
			return null;
		} else {
			return new Point(getReference(), getOrientation(), newValue);
		}
	}

	public RealtimeDouble getX() {
		return getRealtimeVector().getX();
	}

	public RealtimeDouble getY() {
		return getRealtimeVector().getY();
	}

	public RealtimeDouble getZ() {
		return getRealtimeVector().getZ();
	}

	public RealtimeVector getVectorForRepresentation(Frame newReference, Orientation newOrientation,
			FrameTopology topology) throws TransformationException {
		RealtimeVector vec = getRealtimeVector();

		// convert to reference orientation
		if (orientation != reference.asOrientation()) {
			RealtimeTransformation orientationChange = reference.asRealtimePose()
					.getRealtimeTransformationTo(getOrientation().asPose().asRealtimeValue(), topology);
			vec = vec.transform(orientationChange);
		}

		// change reference
		if (reference != newReference) {
			RealtimeTransformation relationSensor = newReference.getRealtimeTransformationTo(getReference(), topology);
			vec = vec.transform(relationSensor);
		}

		if (newOrientation != newReference.asOrientation()) {
			RealtimeTransformation orientationChange = newOrientation.asPose().asRealtimeValue()
					.getRealtimeTransformationTo(newReference.asRealtimePose(), topology);
			vec = vec.transform(orientationChange);
		}
		return vec;
	}

	public RealtimePoint convertToRepresentation(Frame newReference, Orientation newOrientation, FrameTopology topology)
			throws TransformationException {
		return new RealtimePoint(getVectorForRepresentation(newReference, newOrientation, topology), newReference,
				newOrientation);
	}

	public RealtimePoint reinterpretToRepresentation(Frame newReference) {
		return new RealtimePoint(getRealtimeVector(), newReference, newReference.asOrientation());
	}

	public RealtimePoint reinterpretToRepresentation(Frame newReference, Orientation newOrientation) {
		return new RealtimePoint(getRealtimeVector(), newReference, newOrientation);
	}

	public static RealtimePoint createFromXYZ(Frame reference, RealtimeDouble xComponent, RealtimeDouble yComponent,
			RealtimeDouble zComponent) {
		return new RealtimePoint(RealtimeVector.createFromXYZ(xComponent, yComponent, zComponent), reference);
	}

	public static RealtimePoint createFromXYZ(Frame reference, Orientation orientation, RealtimeDouble xComponent,
			RealtimeDouble yComponent, RealtimeDouble zComponent) {
		return new RealtimePoint(RealtimeVector.createFromXYZ(xComponent, yComponent, zComponent), reference);
	}

	public static RealtimePoint createFromVector(Frame reference, Vector vector) {
		return new RealtimePoint(RealtimeVector.createFromConstant(vector), reference);
	}

	public static RealtimePoint createFromVector(Frame reference, Orientation orientation, Vector vector) {
		return new RealtimePoint(RealtimeVector.createFromConstant(vector), reference);
	}

	public static RealtimePoint createFromVector(Frame reference, RealtimeVector vector) {
		return new RealtimePoint(vector, reference);
	}

	public static RealtimePoint createFromVector(Frame reference, Orientation orientation, RealtimeVector vector) {
		return new RealtimePoint(vector, reference);
	}

	public Frame getReference() {
		return reference;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public RealtimeVector getRealtimeVector() {
		return vector;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && nullOrEqual(reference, ((RealtimePoint) obj).reference)
				&& nullOrEqual(orientation, ((RealtimePoint) obj).orientation);
	}

	@Override
	public int hashCode() {
		return hash(super.hashCode(), reference, orientation);
	}

	@Override
	public boolean isAvailable() {
		return vector.isAvailable();
	}

	@Override
	public String toString() {
		return "Point(R: " + reference + (!reference.asOrientation().equals(orientation) ? ", O: " + orientation : "")
				+ ", " + vector + ")";
	}

	@Override
	public boolean isConstant() {
		return vector.isConstant();
	}

	@Override
	public RealtimeBoolean isNull() {
		return vector.isNull();
	}

	@Override
	public RealtimePoint fromHistory(RealtimeDouble age, double maxAge) {
		return new RealtimePoint(vector.fromHistory(age, maxAge), reference);
	}

	public static RealtimePoint createFromConstant(Point point) {
		return new RealtimePoint(point.getVector().asRealtimeValue(), point.getReferenceFrame());
	}

	public RealtimePose asRealtimePose() {
		return RealtimePose.createFromTransformation(reference, RealtimeTransformation.createFromVector(vector));
	}

}
