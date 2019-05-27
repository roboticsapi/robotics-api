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
 * Gives access to integer values of Sensors.
 */
public abstract class IntegerSensor extends Sensor<Integer> {

	/**
	 * Constructs a new {@link IntegerSensor}
	 * 
	 * @param runtime the underlying runtime if appropriate; <code>null</code>
	 *                otherwise.
	 */
	protected IntegerSensor(final RoboticsRuntime runtime) {
		super(runtime);
	}

	/**
	 * Returns a sensor which represents the negation of this sensor.
	 * 
	 * @return the negated sensor.
	 */
	public IntegerSensor negate() {
		return new NegatedIntegerSensor(this);
	}

	/**
	 * Returns a sensor which represents the addition of this sensor with the given
	 * sensor.
	 * 
	 * @param other the sensor to add.
	 * @return the resulting sensor
	 */
	public IntegerSensor add(IntegerSensor other) {
		return new AddedIntegerSensor(this, other);
	}

	/**
	 * Returns a sensor which represents the addition of this sensor with the given
	 * value.
	 * 
	 * @param other the value to add.
	 * @return the resulting sensor
	 */
	public IntegerSensor add(int other) {
		return add(new ConstantIntegerSensor(other));
	}

	/**
	 * Returns a sensor which represents the subtraction of this sensor with the
	 * given sensor.
	 * 
	 * @param other the sensor to subtract.
	 * @return the resulting sensor
	 */
	public IntegerSensor minus(IntegerSensor other) {
		return add(other.negate());
	}

	/**
	 * Returns a sensor which represents the subtraction of this sensor with the
	 * given value.
	 * 
	 * @param other the value to subtract.
	 * @return the resulting sensor
	 */
	public IntegerSensor minus(int other) {
		return add(-other);
	}

	/**
	 * Returns a sensor which represents the multiplication of this sensor with the
	 * given sensor.
	 * 
	 * @param other the sensor to multiply.
	 * @return the resulting sensor
	 */
	public IntegerSensor multiply(IntegerSensor other) {
		return new MultipliedIntegerSensor(this, other);
	}

	/**
	 * Returns a sensor which represents the multiplication of this sensor with the
	 * given value.
	 * 
	 * @param other the value to multiply.
	 * @return the resulting sensor
	 */
	public IntegerSensor multiply(int other) {
		return multiply(new ConstantIntegerSensor(other));
	}

	/**
	 * Returns a sensor which represents the square product of this sensor.
	 * 
	 * @return the resulting sensor
	 */
	public IntegerSensor square() {
		return multiply(this);
	}

	@Override
	protected Integer getDefaultValue() {
		return 0;
	}

	/**
	 * Returns an {@link IntegerSensor} holding a constant value.
	 * 
	 * @param value the constant value
	 * @return the according sensor.
	 */
	public static IntegerSensor fromValue(int value) {
		return new ConstantIntegerSensor(value);
	}
}
