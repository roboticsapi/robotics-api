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
import org.roboticsapi.core.world.Direction;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.Orientation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public final class RealtimeDirection extends RealtimeValue<Direction> {

	@Override
	public final RealtimeDirection substitute(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		if (substitutionMap.containsKey(this)) {
			return (RealtimeDirection) substitutionMap.get(this);
		}
		return new RealtimeDirection(vector.substitute(substitutionMap), getOrientation());
	}

	private final Orientation orientation;
	private final RealtimeVector vector;

	public RealtimeDirection(RealtimeVector vector, Orientation orientation) {
		super(vector);
		this.vector = vector;
		this.orientation = orientation;
	}

	@Override
	public Observer<?> createObserver(final RealtimeValueListener<Direction> listener, RealtimeBoolean condition,
			boolean async) {
		return vector.createObserver(new RealtimeValueListener<Vector>() {
			@Override
			public void onValueChanged(Vector newValue) {
				listener.onValueChanged(wrap(newValue));
			}

		}, condition, async);
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && nullOrEqual(orientation, ((RealtimeDirection) obj).orientation);
	}

	@Override
	public int hashCode() {
		return hash(super.hashCode(), orientation);
	}

	private Direction wrap(Vector newValue) {
		if (newValue == null) {
			return null;
		} else {
			return new Direction(getOrientation(), newValue);
		}
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public RealtimeVector getVector() {
		return vector;
	}

	@Override
	protected Direction calculateCheapValue() {
		return wrap(vector.getCheapValue());
	}

	@Override
	public boolean isConstant() {
		return vector.isConstant();
	}

	public RealtimeVector getVectorForRepresentation(Orientation newOrientation, FrameTopology topology)
			throws TransformationException {
		RealtimeTransformation transformationSensor = newOrientation.asPose().asRealtimeValue()
				.getRealtimeTransformationTo(getOrientation().asPose().asRealtimeValue(), topology);
		return getVector().transform(transformationSensor.getRotation());
	}

	public RealtimeDirection convertToRepresentation(Orientation newOrientation, FrameTopology topology)
			throws TransformationException {
		return new RealtimeDirection(getVectorForRepresentation(newOrientation, topology), newOrientation);
	}

	public RealtimeDirection reinterpretToRepresentation(Orientation newOrientation) {
		return new RealtimeDirection(getVector(), newOrientation);
	}

	public static RealtimeDirection createFromConstant(Direction direction) {
		return new RealtimeDirection(RealtimeVector.createFromConstant(direction.getValue()),
				direction.getOrientation());
	}

	public static RealtimeDirection createFromVector(RealtimeVector vector, Orientation orientation) {
		return new RealtimeDirection(vector, orientation);
	}

	@Override
	public boolean isAvailable() {
		return vector.isAvailable();
	}

	@Override
	public String toString() {
		return "direction(" + orientation + ", " + vector + ")";
	}

	@Override
	public RealtimeBoolean isNull() {
		return vector.isNull();
	}

	@Override
	public RealtimeDirection fromHistory(RealtimeDouble age, double maxAge) {
		return new RealtimeDirection(vector.fromHistory(age, maxAge), orientation);
	}
}
