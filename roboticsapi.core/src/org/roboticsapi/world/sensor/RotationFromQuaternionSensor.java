/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Quaternion;
import org.roboticsapi.world.Rotation;

public final class RotationFromQuaternionSensor extends RotationSensor {

	private final DoubleSensor xComponent;
	private final DoubleSensor yComponent;
	private final DoubleSensor zComponent;
	private final DoubleSensor wComponent;

	public RotationFromQuaternionSensor(DoubleSensor xComponent, DoubleSensor yComponent, DoubleSensor zComponent,
			DoubleSensor wComponent) {
		super(selectRuntime(xComponent, yComponent, zComponent, wComponent));
		addInnerSensors(xComponent, yComponent, zComponent, wComponent);

		this.xComponent = xComponent;
		this.yComponent = yComponent;
		this.zComponent = zComponent;
		this.wComponent = wComponent;
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

	public DoubleSensor getWComponent() {
		return wComponent;
	}

	@Override
	public DoubleSensor getQuaternionWSensor() {
		return wComponent;
	}

	@Override
	public DoubleSensor getQuaternionXSensor() {
		return xComponent;
	}

	@Override
	public DoubleSensor getQuaternionYSensor() {
		return yComponent;
	}

	@Override
	public DoubleSensor getQuaternionZSensor() {
		return zComponent;
	}

	@Override
	protected Rotation calculateCheapValue() {
		Double x = getXComponent().getCheapValue();
		Double y = getYComponent().getCheapValue();
		Double z = getZComponent().getCheapValue();
		Double w = getWComponent().getCheapValue();
		return (x == null || y == null || z == null || w == null) ? null : new Rotation(new Quaternion(x, y, z, w));
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && xComponent.equals(((RotationFromQuaternionSensor) obj).xComponent)
				&& yComponent.equals(((RotationFromQuaternionSensor) obj).yComponent)
				&& zComponent.equals(((RotationFromQuaternionSensor) obj).zComponent)
				&& wComponent.equals(((RotationFromQuaternionSensor) obj).wComponent);
	}

	@Override
	public int hashCode() {
		return classHash(xComponent, yComponent, zComponent, wComponent);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(xComponent, yComponent, zComponent, wComponent);
	}

	@Override
	public String toString() {
		return "rotation(X:" + xComponent + ", Y:" + yComponent + ", Z:" + zComponent + ", W:" + wComponent + ")";
	}
}
