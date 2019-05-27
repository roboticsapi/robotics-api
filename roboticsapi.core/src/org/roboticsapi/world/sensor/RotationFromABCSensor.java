/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Rotation;

public final class RotationFromABCSensor extends RotationSensor {

	private final DoubleSensor aComponent;
	private final DoubleSensor bComponent;
	private final DoubleSensor cComponent;

	public RotationFromABCSensor(DoubleSensor aComponent, DoubleSensor bComponent, DoubleSensor cComponent) {
		super(selectRuntime(aComponent, bComponent, cComponent));
		addInnerSensors(aComponent, bComponent, cComponent);

		this.aComponent = aComponent;
		this.bComponent = bComponent;
		this.cComponent = cComponent;
	}

	public DoubleSensor getAComponent() {
		return aComponent;
	}

	public DoubleSensor getBComponent() {
		return bComponent;
	}

	public DoubleSensor getCComponent() {
		return cComponent;
	}

	@Override
	public DoubleSensor getASensor() {
		return getAComponent();
	}

	@Override
	public DoubleSensor getBSensor() {
		return getBComponent();
	}

	@Override
	public DoubleSensor getCSensor() {
		return getCComponent();
	}

	@Override
	protected Rotation calculateCheapValue() {
		Double a = getAComponent().getCheapValue();
		Double b = getBComponent().getCheapValue();
		Double c = getCComponent().getCheapValue();

		return (a == null || b == null || c == null) ? null : new Rotation(a, b, c);
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && aComponent.equals(((RotationFromABCSensor) obj).aComponent)
				&& bComponent.equals(((RotationFromABCSensor) obj).bComponent)
				&& cComponent.equals(((RotationFromABCSensor) obj).cComponent);
	}

	@Override
	public int hashCode() {
		return classHash(aComponent, bComponent, cComponent);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(aComponent, bComponent, cComponent);
	}

	@Override
	public String toString() {
		return "rotation(A:" + aComponent + ", B:" + bComponent + ", C:" + cComponent + ")";
	}
}
