/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.sensor;

import org.roboticsapi.core.Sensor;
import org.roboticsapi.world.Transformation;

public final class TransformationConditionalSensor extends TransformationSensor {

	private final Sensor<Boolean> condition;
	private final Sensor<Transformation> ifTrue;
	private final Sensor<Transformation> ifFalse;

	public TransformationConditionalSensor(Sensor<Boolean> condition, Sensor<Transformation> ifTrue,
			Sensor<Transformation> ifFalse) {
		super(selectRuntime(condition, ifTrue, ifFalse));
		addInnerSensors(condition, ifTrue, ifFalse);
		this.condition = condition;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;

	}

	public TransformationConditionalSensor(Sensor<Boolean> condition, Sensor<Transformation> ifTrue,
			Transformation ifFalse) {
		this(condition, ifTrue, new ConstantTransformationSensor(ifFalse));
	}

	public TransformationConditionalSensor(Sensor<Boolean> condition, Transformation ifTrue,
			Sensor<Transformation> ifFalse) {
		this(condition, new ConstantTransformationSensor(ifTrue), ifFalse);
	}

	public TransformationConditionalSensor(Sensor<Boolean> condition, Transformation ifTrue, Transformation ifFalse) {
		this(condition, ifTrue, new ConstantTransformationSensor(ifFalse));
	}

	public Sensor<Boolean> getCondition() {
		return condition;
	}

	public Sensor<Transformation> getIfFalse() {
		return ifFalse;
	}

	public Sensor<Transformation> getIfTrue() {
		return ifTrue;
	}

	@Override
	protected Transformation calculateCheapValue() {
		Boolean booleanValue = condition.getCheapValue();

		if (booleanValue == null) {
			return null;
		}

		if (booleanValue) {
			return ifTrue.getCheapValue();
		} else {
			return ifFalse.getCheapValue();
		}
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && condition.equals(((TransformationConditionalSensor) obj).condition)
				&& ifTrue.equals(((TransformationConditionalSensor) obj).ifTrue)
				&& ifFalse.equals(((TransformationConditionalSensor) obj).ifFalse);
	}

	@Override
	public int hashCode() {
		return classHash(condition, ifTrue, ifFalse);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(condition, ifTrue, ifFalse);
	}

	@Override
	public String toString() {
		return "(" + condition + " ? " + ifTrue + " : " + ifFalse + ")";
	}
}
