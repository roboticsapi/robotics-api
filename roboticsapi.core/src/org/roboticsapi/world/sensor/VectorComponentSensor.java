/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Vector;

/**
 * The VectorComponentSensor extracts a certain component from a VectorSensor.
 */
public final class VectorComponentSensor extends DoubleSensor {

	/**
	 * Transformation Components.
	 */
	public enum VectorComponent {
		X, Y, Z
	}

	private final VectorComponent component;
	private final VectorSensor sensor;

	/**
	 * Instantiates a new TransformationComponentSensor.
	 *
	 * @param sensor    the TransformationSensor to extract a component from
	 * @param component the component to extract
	 */
	public VectorComponentSensor(VectorSensor sensor, VectorComponent component) {
		super(selectRuntime(sensor));
		addInnerSensors(sensor);
		this.sensor = sensor;
		this.component = component;
	}

	/**
	 * Gets the component.
	 *
	 * @return the component
	 */
	public VectorComponent getComponent() {
		return component;
	}

	/**
	 * Gets the original VectorSensor.
	 *
	 * @return the sensor
	 */
	public VectorSensor getSensor() {
		return sensor;
	}

	@Override
	protected Double calculateCheapValue() {
		Vector curr = getSensor().getCheapValue();
		if (curr == null) {
			return null;
		}
		if (component == VectorComponent.X) {
			return curr.getX();
		} else if (component == VectorComponent.Y) {
			return curr.getY();
		} else {
			return curr.getZ();
		}
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && sensor.equals(((VectorComponentSensor) obj).sensor)
				&& component == ((VectorComponentSensor) obj).component;
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
