/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Rotation;

public final class OrientationSensor extends Sensor<Orientation> {

	private final Frame reference;
	private final RotationSensor rotationSensor;

	public OrientationSensor(RotationSensor rotationSensor, Frame reference) {
		super(selectRuntime(rotationSensor));
		addInnerSensors(rotationSensor);
		this.rotationSensor = rotationSensor;
		this.reference = reference;
	}

	@Override
	protected Orientation getDefaultValue() {
		return new Orientation(getReference(), getRotationSensor().getDefaultValue());
	}

	@Override
	protected Orientation calculateCheapValue() {
		Rotation cheapValue = getRotationSensor().getCheapValue();
		return cheapValue == null ? null : new Orientation(getReference(), cheapValue);
	}

	public Frame getReference() {
		return reference;
	}

	public DoubleSensor getASensor() {
		return getRotationSensor().getASensor();
	}

	public DoubleSensor getBSensor() {
		return getRotationSensor().getBSensor();
	}

	public DoubleSensor getCSensor() {
		return getRotationSensor().getCSensor();
	}

	public OrientationSensor changeReference(Frame newReference) {
		RelationSensor relationSensor = newReference.getRelationSensor(getReference());

		return new OrientationSensor(getRotationSensor().multiply(relationSensor.getTransformationSensor()),
				newReference);
	}

	public OrientationSensor reinterpret(Frame newReference) {
		return new OrientationSensor(getRotationSensor(), newReference);
	}

	public static OrientationSensor fromABC(Frame reference, DoubleSensor aComponent, DoubleSensor bComponent,
			DoubleSensor cComponent) {
		return new OrientationSensor(RotationSensor.fromABC(aComponent, bComponent, cComponent), reference);
	}

	public static OrientationSensor fromConstant(Frame reference, Rotation rotation) {
		return new OrientationSensor(RotationSensor.fromConstant(rotation), reference);
	}

	public static OrientationSensor fromConstant(Orientation orientation) {
		return fromConstant(orientation.getReferenceFrame(), orientation.getRotation());
	}

	public RotationSensor getRotationSensor() {
		return rotationSensor;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && reference.equals(((OrientationSensor) obj).reference)
				&& rotationSensor.equals(((OrientationSensor) obj).rotationSensor);
	}

	@Override
	public int hashCode() {
		return classHash(reference, rotationSensor);
	}

	@Override
	public boolean isAvailable() {
		return rotationSensor.isAvailable();
	}

	@Override
	public String toString() {
		return "orientation(" + reference + ", " + rotationSensor + ")";
	}

}
