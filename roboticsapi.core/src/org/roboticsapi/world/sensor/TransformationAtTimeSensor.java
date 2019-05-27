/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.sensor.DoubleSensor;

public final class TransformationAtTimeSensor extends TransformationSensor {

	private final TransformationSensor sensor;
	private final DoubleSensor age;
	private final double maxAge;

	public TransformationAtTimeSensor(TransformationSensor sensor, DoubleSensor age, double maxAge) {
		super(selectRuntime(sensor, age));
		addInnerSensors(sensor, age);
		this.sensor = sensor;
		this.age = age;
		this.maxAge = maxAge;
	}

	public TransformationSensor getOtherSensor() {
		return sensor;
	}

	public DoubleSensor getAge() {
		return age;
	}

	public double getMaxAge() {
		return maxAge;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && sensor.equals(((TransformationAtTimeSensor) obj).sensor)
				&& age.equals(((TransformationAtTimeSensor) obj).age)
				&& maxAge == ((TransformationAtTimeSensor) obj).maxAge;
	}

	@Override
	public int hashCode() {
		return classHash(sensor, age, maxAge);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(age, sensor);
	}

	@Override
	public String toString() {
		return "atTime(" + sensor + ", " + age + ")";
	}
}
