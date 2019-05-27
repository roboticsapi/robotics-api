/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.Sensor;
import org.roboticsapi.world.Direction;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Vector;

public class DirectionSensor extends Sensor<Direction> {

	private final Orientation orientation;
	private final VectorSensor vectorSensor;

	public DirectionSensor(VectorSensor vectorSensor, Orientation orientation) {
		super(selectRuntime(vectorSensor));
		addInnerSensors(vectorSensor);
		this.vectorSensor = vectorSensor;
		this.orientation = orientation;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public VectorSensor getVectorSensor() {
		return vectorSensor;
	}

	@Override
	protected Direction getDefaultValue() {
		return new Direction(getOrientation(), getVectorSensor().getDefaultValue());
	}

	@Override
	protected Direction calculateCheapValue() {
		Vector cheapValue = getVectorSensor().getCheapValue();
		return cheapValue == null ? null : new Direction(getOrientation(), cheapValue);
	}

	public DirectionSensor changeOrientation(Orientation newOrientation) {
		RelationSensor transformationSensor = newOrientation.getReferenceFrame().plus(newOrientation.getRotation())
				.getRelationSensor(getOrientation().getReferenceFrame().plus(getOrientation().getRotation()));

		return new DirectionSensor(
				getVectorSensor().transform(transformationSensor.getTransformationSensor().getRotationSensor()),
				newOrientation);
	}

	public DirectionSensor reinterpret(Orientation newOrientation) {
		return new DirectionSensor(getVectorSensor(), newOrientation);
	}

	public static DirectionSensor fromConstant(Direction direction) {
		return new DirectionSensor(VectorSensor.fromConstant(direction.getValue()), direction.getOrientation());
	}

	@Override
	public boolean isAvailable() {
		return vectorSensor.isAvailable();
	}

	@Override
	public String toString() {
		return "direction(" + orientation + ", " + vectorSensor + ")";
	}
}
