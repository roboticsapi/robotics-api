/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;

/**
 * Abstract implementation for a {@link DoubleSensor} that is composed of two
 * other {@link DoubleSensor}s.
 */
public abstract class BinaryFunctionDoubleSensor extends DoubleSensor {

	/**
	 * The first sensor.
	 */
	private final Sensor<Double> sensor1;

	/**
	 * The second sensor.
	 */
	private final Sensor<Double> sensor2;

	/**
	 * Constructor.
	 *
	 * @param sensor1 the first sensor.
	 * @param sensor2 the second sensor.
	 * @throws IllegalArgumentException if the given sensor have different
	 *                                  {@link RoboticsRuntime}s.
	 */
	public BinaryFunctionDoubleSensor(Sensor<Double> sensor1, Sensor<Double> sensor2) {
		super(selectRuntime(sensor1, sensor2));
		addInnerSensors(sensor1, sensor2);

		this.sensor1 = sensor1;
		this.sensor2 = sensor2;
	}

	/**
	 * Returns the first sensor.
	 *
	 * @return the first sensor.
	 */
	public final Sensor<Double> getSensor1() {
		return sensor1;
	}

	/**
	 * Returns the second sensor.
	 *
	 * @return the second sensor.
	 */
	public final Sensor<Double> getSensor2() {
		return sensor2;
	}

	@Override
	protected final Double calculateCheapValue() {
		Double value1 = sensor1.getCheapValue();
		if (value1 == null) {
			return null;
		}

		Double value2 = sensor2.getCheapValue();
		if (value2 == null) {
			return null;
		}
		return computeCheapValue(value1, value2);
	}

	/**
	 * Computes the cheap value for the composed {@link DoubleSensor}.
	 *
	 * @param value1 the first sensor's cheap value
	 * @param value2 the second sensor's cheap value
	 * @return the computed cheap value.
	 */
	protected abstract Double computeCheapValue(double value1, double value2);

	@Override
	public final boolean equals(Object obj) {
		return classEqual(obj) && sensor1.equals(((BinaryFunctionDoubleSensor) obj).sensor1)
				&& sensor2.equals(((BinaryFunctionDoubleSensor) obj).sensor2);
	}

	@Override
	public final int hashCode() {
		return classHash(sensor1, sensor2);
	}

	@Override
	public final boolean isAvailable() {
		return areAvailable(sensor1, sensor2);
	}

}
