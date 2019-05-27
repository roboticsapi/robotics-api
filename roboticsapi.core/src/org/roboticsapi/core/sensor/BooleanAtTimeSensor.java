/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

public final class BooleanAtTimeSensor extends BooleanSensor {

	private final BooleanSensor sensor;
	private final DoubleSensor age;
	private final double maxAge;

	public BooleanAtTimeSensor(BooleanSensor sensor, DoubleSensor age, double maxAge) {
		super(selectRuntime(sensor, age));
		addInnerSensors(sensor, age);
		this.sensor = sensor;
		this.age = age;
		this.maxAge = maxAge;
	}

	public BooleanSensor getOtherSensor() {
		return sensor;
	}

	public DoubleSensor getAge() {
		return age;
	}

	public double getMaxAge() {
		return maxAge;
	}

	/**
	 * Compares this sensor with the given object.
	 * 
	 * @param obj The object to compare with.
	 * 
	 * @return true if {@code obj} has the same class, the equal
	 *         {@code sensor:BooleanSensor}, the equal {@code age:DoubleSensor} and
	 *         the equal {@code maxAge}. false else.
	 */
	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && sensor.equals(((BooleanAtTimeSensor) obj).sensor)
				&& age.equals(((BooleanAtTimeSensor) obj).age) && maxAge == ((BooleanAtTimeSensor) obj).maxAge;
	}

	@Override
	public int hashCode() {
		return classHash(sensor, age, maxAge);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(sensor, age);
	}

	@Override
	public String toString() {
		return "atTime(" + sensor + ", " + age + ")";
	}
}
