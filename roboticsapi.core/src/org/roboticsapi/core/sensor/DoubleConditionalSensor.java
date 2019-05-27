/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Sensor;

public final class DoubleConditionalSensor extends DoubleSensor {

	private final Sensor<Boolean> condition;
	private final Sensor<Double> ifTrue;
	private final Sensor<Double> ifFalse;

	public DoubleConditionalSensor(Sensor<Boolean> condition, Sensor<Double> ifTrue, Sensor<Double> ifFalse) {
		super(selectRuntime(condition, ifTrue, ifFalse));
		addInnerSensors(condition, ifTrue, ifFalse);
		this.condition = condition;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;

	}

	public DoubleConditionalSensor(Sensor<Boolean> condition, Sensor<Double> ifTrue, double ifFalse) {
		this(condition, ifTrue, new ConstantDoubleSensor(ifFalse));
	}

	public DoubleConditionalSensor(Sensor<Boolean> condition, double ifTrue, Sensor<Double> ifFalse) {
		this(condition, new ConstantDoubleSensor(ifTrue), ifFalse);
	}

	public DoubleConditionalSensor(Sensor<Boolean> condition, double ifTrue, double ifFalse) {
		this(condition, ifTrue, new ConstantDoubleSensor(ifFalse));
	}

	public Sensor<Boolean> getCondition() {
		return condition;
	}

	public Sensor<Double> getIfFalse() {
		return ifFalse;
	}

	public Sensor<Double> getIfTrue() {
		return ifTrue;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && condition.equals(((DoubleConditionalSensor) obj).condition)
				&& ifTrue.equals(((DoubleConditionalSensor) obj).ifTrue)
				&& ifFalse.equals(((DoubleConditionalSensor) obj).ifFalse);
	}

	@Override
	public int hashCode() {
		return classHash(condition, ifTrue, ifFalse);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(condition, ifFalse, ifTrue);
	}

	@Override
	public String toString() {
		return "(" + condition + " ? " + ifTrue + " : " + ifFalse + ")";
	}
}
