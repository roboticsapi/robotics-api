/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorState;
import org.roboticsapi.core.State;
import org.roboticsapi.core.state.AndState;

/**
 * Gives access to double values of Sensors.
 */
public abstract class DoubleSensor extends Sensor<Double> {
	/**
	 * @param runtime
	 */
	public DoubleSensor(final RoboticsRuntime runtime) {
		super(runtime);
	}

	/**
	 * Returns an event with IsGreater semantics for the sensor.
	 *
	 * @param value The value to compare the sensor value with.
	 * @return IsGreater event.
	 */
	public SensorState isGreater(final double value) {
		return new DoubleIsGreaterSensor(this, value).isTrue();
	}

	/**
	 * Returns an event with IsGreater semantics for the sensor.
	 *
	 * @param value The sensor value to compare this sensor value with.
	 * @return IsGreater event.
	 */
	public SensorState isGreater(DoubleSensor value) {
		return new DoubleIsGreaterSensor(this, value).isTrue();
	}

	/**
	 * Returns an event with IsLess semantics for the sensor.
	 *
	 * @param value The value to compare the sensor value with.
	 * @return IsLess event.
	 */
	public SensorState isLess(final double value) {
		return new DoubleIsGreaterSensor(value, this).isTrue();
	}

	/**
	 * Returns an event with IsLess semantics for the sensor.
	 *
	 * @param value The sensor value to compare this sensor value with.
	 * @return IsLess event.
	 */
	public SensorState isLess(DoubleSensor value) {
		return new DoubleIsGreaterSensor(value, this).isTrue();
	}

	public BooleanSensor greater(DoubleSensor value) {
		return new DoubleIsGreaterSensor(this, value);
	}

	public BooleanSensor greater(double value) {
		return greater(DoubleSensor.fromValue(value));
	}

	public BooleanSensor less(DoubleSensor value) {
		return new DoubleIsGreaterSensor(value, this);
	}

	public BooleanSensor less(double value) {
		return less(DoubleSensor.fromValue(value));
	}

	public BooleanSensor equals(DoubleSensor value, double epsilon) {
		return add(epsilon).greater(value).and(add(-epsilon).less(value));
	}

	public BooleanSensor equals(double value, double epsilon) {
		return equals(DoubleSensor.fromValue(value), epsilon);
	}

	public State isEqual(final double value, final double epsilon) {
		final AndState ae = new AndState();
		ae.addState(isGreater(value - epsilon));
		ae.addState(isLess(value + epsilon));
		return ae;
	}

	public DoubleSensor negate() {
		return new NegatedDoubleSensor(this);
	}

	public DoubleSensor add(DoubleSensor other) {
		return new AddedDoubleSensor(this, other);
	}

	public DoubleSensor add(double other) {
		return add(new ConstantDoubleSensor(other));
	}

	public DoubleSensor minus(DoubleSensor other) {
		return add(other.negate());
	}

	public DoubleSensor minus(double other) {
		return add(-other);
	}

	public DoubleSensor multiply(DoubleSensor other) {
		return new MultipliedDoubleSensor(this, other);
	}

	public DoubleSensor multiply(double other) {
		return multiply(new ConstantDoubleSensor(other));
	}

	public DoubleSensor square() {
		return multiply(this);
	}

	public DoubleSensor divide(double other) {
		return divide(new ConstantDoubleSensor(other));
	}

	public DoubleSensor divide(DoubleSensor other) {
		return new DividedDoubleSensor(this, other);
	}

	public DoubleSensor modulo(DoubleSensor other) {
		return new ModuloDoubleSensor(this, other);
	}

	public DoubleSensor modulo(double other) {
		return modulo(new ConstantDoubleSensor(other));
	}

	public DoubleSensor sqrt() {
		return new SquareRootDoubleSensor(this);
	}

	public DoubleSensor abs() {
		return new AbsDoubleSensor(this);
	}

	public DoubleSensor limit(double minValue, double maxValue) {
		return DoubleSensor.conditional(less(minValue), DoubleSensor.fromValue(minValue),
				DoubleSensor.conditional(greater(maxValue), DoubleSensor.fromValue(maxValue), this));
	}

	public DoubleSensor exponentialSmoothing(double halfLife) {
		return new ExponentiallySmootedDoubleSensor(this, halfLife);
	}

	public DoubleSensor slidingAverage(double duration) {
		return new SlidingAverageDoubleSensor(this, duration);
	}

	public DoubleSensor power(DoubleSensor exponent) {
		return new PowerDoubleSensor(this, exponent);
	}

	public DoubleSensor power(double exponent) {
		return this.power(fromValue(exponent));
	}

	public DoubleSensor acos() {
		return new ArccosineDoubleSensor(this);
	}

	public DoubleSensor asin() {
		return new ArcsineDoubleSensor(this);
	}

	public DoubleSensor cos() {
		return new CosineDoubleSensor(this);
	}

	public DoubleSensor sin() {
		return new SineDoubleSensor(this);
	}

	@Override
	protected Double getDefaultValue() {
		return 0d;
	}

	public static DoubleSensor fromValue(double value) {
		return new ConstantDoubleSensor(value);
	}

	public static DoubleSensor[] fromValues(double[] values) {
		DoubleSensor[] sensors = new DoubleSensor[values.length];
		for (int i = 0; i < values.length; i++) {
			sensors[i] = fromValue(values[i]);
		}
		return sensors;
	}

	public static DoubleSensor atan2(DoubleSensor y, DoubleSensor x) {
		return new Atan2DoubleSensor(y, x);
	}

	public static DoubleSensor average(DoubleSensor sensor, DoubleSensor... sensors) {
		for (int i = 0; i < sensors.length; i++) {
			sensor = sensor.add(sensors[i]);
		}
		return sensor.divide(sensors.length + 1);
	}

	public static DoubleSensor conditional(BooleanSensor condition, DoubleSensor ifTrue, DoubleSensor ifFalse) {
		return new DoubleConditionalSensor(condition, ifTrue, ifFalse);
	}

	public static DoubleSensor min(DoubleSensor... sensors) {
		if (sensors == null || sensors.length < 1) {
			return null;
		}
		return min(sensors[0], 1, sensors);
	}

	private static DoubleSensor min(DoubleSensor sensor, int offset, DoubleSensor... sensors) {
		for (int i = offset; i < sensors.length; i++) {
			sensor = conditional(sensor.less(sensors[i]), sensor, sensors[i]);
		}
		return sensor;
	}

	public static DoubleSensor max(DoubleSensor... sensors) {
		if (sensors == null || sensors.length < 1) {
			return null;
		}
		return max(sensors[0], 1, sensors);
	}

	private static DoubleSensor max(DoubleSensor sensor, int offset, DoubleSensor... sensors) {
		for (int i = offset; i < sensors.length; i++) {
			sensor = conditional(sensor.greater(sensors[i]), sensor, sensors[i]);
		}
		return sensor;
	}

}
