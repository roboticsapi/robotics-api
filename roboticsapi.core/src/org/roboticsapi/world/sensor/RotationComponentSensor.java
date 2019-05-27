/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Rotation;

/**
 * The TransformationComponentSensor extracts a certain component from a
 * TransformationSensor.
 */
public final class RotationComponentSensor extends DoubleSensor {

	/**
	 * Transformation Components.
	 */
	public enum RotationComponent {
		A, B, C, QuaternionX, QuaternionY, QuaternionZ, QuaternionW
	}

	private final RotationComponent component;
	private final RotationSensor sensor;

	/**
	 * Instantiates a new TransformationComponentSensor.
	 * 
	 * @param sensor    the TransformationSensor to extract a component from
	 * @param component the component to extract
	 */
	public RotationComponentSensor(RotationSensor sensor, RotationComponent component) {
		super(selectRuntime(sensor));
		addInnerSensors(sensor);
		this.sensor = sensor;

		if (component == null) {
			throw new IllegalArgumentException("Argument component must not be null.");
		}

		this.component = component;
	}

	/**
	 * Gets the component.
	 * 
	 * @return the component
	 */
	public RotationComponent getComponent() {
		return component;
	}

	/**
	 * Gets the original VectorSensor.
	 * 
	 * @return the sensor
	 */
	public RotationSensor getSensor() {
		return sensor;
	}

	@Override
	protected Double calculateCheapValue() {
		Rotation curr = getSensor().getCheapValue();
		if (curr == null) {
			return null;
		}

		switch (component) {
		case A:
			return curr.getA();
		case B:
			return curr.getB();
		case C:
			return curr.getC();
		case QuaternionX:
			return curr.getQuaternion().getX();
		case QuaternionY:
			return curr.getQuaternion().getY();
		case QuaternionZ:
			return curr.getQuaternion().getZ();
		case QuaternionW:
			return curr.getQuaternion().getW();
		default:
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && component == ((RotationComponentSensor) obj).component
				&& sensor.equals(((RotationComponentSensor) obj).sensor);
	}

	@Override
	public int hashCode() {
		return classHash(sensor, component);
	}

	@Override
	public boolean isAvailable() {
		return sensor.isAvailable();
	}

	@Override
	public String toString() {
		return sensor + "." + component;
	}
}
