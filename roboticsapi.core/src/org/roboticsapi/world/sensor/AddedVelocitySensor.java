/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.world.Twist;

public final class AddedVelocitySensor extends VelocitySensor {

	private final VelocitySensor sensor1;
	private final VelocitySensor sensor2;

	public AddedVelocitySensor(VelocitySensor sensor1, VelocitySensor sensor2) {
		super(selectRuntime(sensor1, sensor2), sensor2.getMovingFrame(), sensor1.getReferenceFrame(),
				sensor1.getPivotPoint(), sensor1.getOrientation());
		addInnerSensors(sensor1, sensor2);
		this.sensor1 = sensor1;
		this.sensor2 = sensor2;

		if (sensor1.getRuntime() != null && sensor2.getRuntime() != null
				&& !(sensor1.getRuntime().equals(sensor2.getRuntime()))) {
			throw new IllegalArgumentException("Runtimes of both sensors must match");
		}

		if (!sensor1.getPivotPoint().isEqualPoint(sensor2.getPivotPoint())) {
			throw new IllegalArgumentException("Pivot point of both sensors must match");
		}

		if (!sensor1.getOrientation().isEqualOrientation(sensor2.getOrientation())) {
			throw new IllegalArgumentException("Orientation of both sensors must match");
		}

		if (!(sensor1.getMovingFrame().equals(sensor2.getReferenceFrame()))) {
			throw new IllegalArgumentException("Sensor1's moving frame must be equal to sensor2's reference frame");
		}
	}

	public VelocitySensor getFirstSensor() {
		return sensor1;
	}

	public VelocitySensor getSecondSensor() {
		return sensor2;
	}

	@Override
	protected Twist calculateCheapValue() {
		Twist s1val = getFirstSensor().getCheapValue();
		Twist s2val = getSecondSensor().getCheapValue();
		return (s1val == null || s2val == null) ? null
				: new Twist(s1val.getTransVel().add(s2val.getTransVel()), s1val.getRotVel().add(s2val.getRotVel()));
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && sensor1.equals(((AddedVelocitySensor) obj).sensor1)
				&& sensor2.equals(((AddedVelocitySensor) obj).sensor2);
	}

	@Override
	public int hashCode() {
		return classHash(sensor1, sensor2);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(sensor1, sensor2);
	}

	@Override
	public String toString() {
		return "(" + sensor1 + " + " + sensor2 + ")";
	}
}
