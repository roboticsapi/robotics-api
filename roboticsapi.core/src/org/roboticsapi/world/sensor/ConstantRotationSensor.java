/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.sensor.ConstantDoubleSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Rotation;

public final class ConstantRotationSensor extends RotationSensor {

	private final Rotation value;

	public ConstantRotationSensor(Rotation value) {
		super(null);
		this.value = value;
	}

	@Override
	protected Rotation calculateCheapValue() {
		return getConstantValue();
	}

	public Rotation getConstantValue() {
		return value;
	}

	@Override
	public DoubleSensor getASensor() {
		return new ConstantDoubleSensor(getConstantValue().getA());
	}

	@Override
	public DoubleSensor getBSensor() {
		return new ConstantDoubleSensor(getConstantValue().getB());
	}

	@Override
	public DoubleSensor getCSensor() {
		return new ConstantDoubleSensor(getConstantValue().getC());
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && value.equals(((ConstantRotationSensor) obj).value);
	}

	@Override
	public int hashCode() {
		return classHash(value);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String toString() {
		return "rotation(A:" + value.getA() + ", B:" + value.getB() + ", C:" + value.getC() + ")";
	}
}
