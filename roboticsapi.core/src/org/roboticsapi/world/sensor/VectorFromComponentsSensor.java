/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Vector;

public final class VectorFromComponentsSensor extends VectorSensor {

	private final DoubleSensor xComponent;
	private final DoubleSensor yComponent;
	private final DoubleSensor zComponent;

	public VectorFromComponentsSensor(DoubleSensor xComponent, DoubleSensor yComponent, DoubleSensor zComponent) {
		super(selectRuntime(xComponent, yComponent, zComponent));
		addInnerSensors(xComponent, yComponent, zComponent);

		this.xComponent = xComponent;
		this.yComponent = yComponent;
		this.zComponent = zComponent;
	}

	public DoubleSensor getXComponent() {
		return xComponent;
	}

	public DoubleSensor getYComponent() {
		return yComponent;
	}

	public DoubleSensor getZComponent() {
		return zComponent;
	}

	@Override
	public DoubleSensor getXSensor() {
		return getXComponent();
	}

	@Override
	public DoubleSensor getYSensor() {
		return getYComponent();
	}

	@Override
	public DoubleSensor getZSensor() {
		return getZComponent();
	}

	@Override
	protected Vector calculateCheapValue() {
		Double x = getXSensor().getCheapValue();
		Double y = getYSensor().getCheapValue();
		Double z = getZSensor().getCheapValue();

		if (x == null || y == null || z == null) {
			return null;
		}
		return new Vector(x, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && xComponent.equals(((VectorFromComponentsSensor) obj).xComponent)
				&& yComponent.equals(((VectorFromComponentsSensor) obj).yComponent)
				&& zComponent.equals(((VectorFromComponentsSensor) obj).zComponent);
	}

	@Override
	public int hashCode() {
		return classHash(xComponent, yComponent, zComponent);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(xComponent, yComponent, zComponent);
	}

	@Override
	public String toString() {
		return "vector(" + xComponent + ", " + yComponent + ", " + zComponent + ")";
	}
}
