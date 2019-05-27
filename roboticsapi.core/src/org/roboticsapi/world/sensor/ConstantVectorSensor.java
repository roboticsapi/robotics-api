/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.sensor.ConstantDoubleSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Vector;

public final class ConstantVectorSensor extends VectorSensor {

	private final Vector value;

	public ConstantVectorSensor(Vector value) {
		super(null);
		this.value = value;
	}

	@Override
	protected Vector calculateCheapValue() {
		return getConstantValue();
	}

	public Vector getConstantValue() {
		return value;
	}

	@Override
	public DoubleSensor getXSensor() {
		return new ConstantDoubleSensor(getConstantValue().getX());
	}

	@Override
	public DoubleSensor getYSensor() {
		return new ConstantDoubleSensor(getConstantValue().getY());
	}

	@Override
	public DoubleSensor getZSensor() {
		return new ConstantDoubleSensor(getConstantValue().getZ());
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && value.equals(((ConstantVectorSensor) obj).value);
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
		return "vector(" + value.getX() + ", " + value.getY() + ", " + value.getZ() + ")";
	}
}
