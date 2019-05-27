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

/**
 * Gives access to boolean values of Sensors.
 */
public abstract class BooleanSensor extends Sensor<Boolean> {
	/**
	 * @param runtime
	 */
	public BooleanSensor(final RoboticsRuntime runtime) {
		super(runtime);
	}

	/**
	 * Returns an IsTrue event for this sensor value.
	 * 
	 * @return SensorEvent with IsTrue semantics.
	 */
	public SensorState isTrue() {
		return new SensorState(this);
	}

	/**
	 * Returns an IsFalse event for this sensor value.
	 * 
	 * @return SensorEvent with IsFalse semantics.
	 */
	public SensorState isFalse() {
		return new SensorState(this.not());
	}

	@Override
	protected Boolean getDefaultValue() {
		return false;
	}

	public BooleanSensor not() {
		return new NegatedBooleanSensor(this);
	}

	public BooleanSensor or(BooleanSensor other, BooleanSensor... further) {
		BooleanSensor[] others = new BooleanSensor[further.length + 1];
		others[0] = other;
		for (int i = 1; i < further.length + 1; i++) {
			others[i] = further[i - 1];
		}

		return new OrBooleanSensor(this, others);
	}

	public BooleanSensor and(BooleanSensor other, BooleanSensor... further) {
		BooleanSensor[] negatedOthers = new BooleanSensor[further.length + 1];

		negatedOthers[0] = other.not();
		for (int i = 1; i < further.length + 1; i++) {
			negatedOthers[i] = further[i - 1].not();
		}

		return new NegatedBooleanSensor(new OrBooleanSensor(this.not(), negatedOthers));
	}

	public BooleanSensor xor(BooleanSensor otherSensor) {
		return (not().and(otherSensor)).or(and(otherSensor.not()));
	}

	public BooleanSensor xor(boolean value) {
		return xor(fromValue(value));
	}

	public static BooleanSensor fromValue(boolean value) {
		return new ConstantBooleanSensor(value);
	}

	public BooleanSensor fromHistory(DoubleSensor age, double maxAge) {
		return new BooleanAtTimeSensor(this, age, maxAge);
	}

	public BooleanSensor fromHistory(double age) {
		return new BooleanAtTimeSensor(this, DoubleSensor.fromValue(age), age);
	}

}
