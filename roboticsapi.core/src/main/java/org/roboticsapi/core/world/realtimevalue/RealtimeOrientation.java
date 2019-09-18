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
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public final class RealtimeOrientation extends RealtimeValue<Orientation> {

	@Override
	public final RealtimeOrientation substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeOrientation) substitutionMap.get(this);
		}
		return new RealtimeOrientation(rotation.substitute(substitutionMap), reference);
	}

	private final Frame reference;
	private final RealtimeRotation rotation;

	RealtimeOrientation(RealtimeRotation rotation, Frame reference) {
		super(rotation);
		this.rotation = rotation;
		this.reference = reference;
	}

	@Override
	protected Orientation calculateCheapValue() {
		return wrap(rotation.getCheapValue());
	}

	private Orientation wrap(Rotation newValue) {
		if (newValue == null) {
			return null;
		} else {
			return new Orientation(getReference(), newValue);
		}
	}

	@Override
	public Observer<?> createObserver(final RealtimeValueListener<Orientation> listener, RealtimeBoolean condition,
			boolean async) {
		return rotation.createObserver(new RealtimeValueListener<Rotation>() {
			@Override
			public void onValueChanged(Rotation newValue) {
				listener.onValueChanged(wrap(newValue));
			}
		}, condition, async);
	}

	public Frame getReference() {
		return reference;
	}

	public RealtimeDouble getA() {
		return getRotation().getA();
	}

	public RealtimeDouble getB() {
		return getRotation().getB();
	}

	public RealtimeDouble getC() {
		return getRotation().getC();
	}

	@Override
	public boolean isConstant() {
		return rotation.isConstant();
	}

	public RealtimeOrientation changeReference(Frame newReference) {
		RealtimeTransformation refChange = newReference.getRealtimeTransformationTo(reference,
				World.getCommandedTopology());
		return new RealtimeOrientation(
				refChange.multiply(RealtimeTransformation.createFromRotation(getRotation())).getRotation(),
				newReference);
	}

	public RealtimeRotation getRotationForRepresentation(Frame newReference, FrameTopology topology) {
		RealtimeTransformation refChange = newReference.getRealtimeTransformationTo(reference, topology);
		return refChange.multiply(RealtimeTransformation.createFromRotation(getRotation())).getRotation();
	}

	public RealtimeOrientation convertToRepresentation(Frame newReference, FrameTopology topology) {
		return new RealtimeOrientation(getRotationForRepresentation(newReference, topology), newReference);
	}

	public RealtimeOrientation reinterpretToRepresentation(Frame newReference) {
		return new RealtimeOrientation(getRotation(), newReference);
	}

	public static RealtimeOrientation createFromABC(Frame reference, RealtimeDouble aComponent,
			RealtimeDouble bComponent, RealtimeDouble cComponent) {
		return new RealtimeOrientation(RealtimeRotation.createFromABC(aComponent, bComponent, cComponent), reference);
	}

	public static RealtimeOrientation createFromConstant(Frame reference, Rotation rotation) {
		return new RealtimeOrientation(RealtimeRotation.createFromConstant(rotation), reference);
	}

	public static RealtimeOrientation createFromConstant(Orientation orientation) {
		return createFromConstant(orientation.getReferenceFrame(), orientation.getRotation());
	}

	public RealtimeRotation getRotation() {
		return rotation;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && nullOrEqual(reference, ((RealtimeOrientation) obj).reference);
	}

	@Override
	public int hashCode() {
		return hash(super.hashCode(), reference);
	}

	@Override
	public boolean isAvailable() {
		return rotation.isAvailable();
	}

	@Override
	public String toString() {
		return "orientation(" + reference + ", " + rotation + ")";
	}

	@Override
	public RealtimeBoolean isNull() {
		return rotation.isNull();
	}

	@Override
	public RealtimeOrientation fromHistory(RealtimeDouble age, double maxAge) {
		return new RealtimeOrientation(rotation.fromHistory(age, maxAge), reference);
	}

	public static RealtimeOrientation createFromRotation(RealtimeRotation rotation, Frame reference) {
		return new RealtimeOrientation(rotation, reference);
	}

	public RealtimePose asRealtimePose() {
		return RealtimePose.createFromTransformation(reference.asOrientation(), reference,
				RealtimeTransformation.createFromVectorRotation(RealtimeVector.ZERO, rotation));
	}

}
