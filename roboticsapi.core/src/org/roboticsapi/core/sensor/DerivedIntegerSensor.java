/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Sensor;

/**
 * Abstract implementation for a {@link IntegerSensor} that is derived from
 * other {@link Sensor}s.
 */
public abstract class DerivedIntegerSensor extends IntegerSensor {

	private final Sensor<?>[] innerSensors;

	/**
	 * Constructs a new {@link DerivedIntegerSensor}.
	 *
	 * @param sensors the inner sensors.
	 */
	public DerivedIntegerSensor(Sensor<?>... sensors) {
		super(selectRuntime(sensors));
		innerSensors = sensors;
		addInnerSensors(sensors);

	}

	@Override
	public final boolean isAvailable() {
		return areAvailable(innerSensors);
	}

	@Override
	protected final Integer calculateCheapValue() {
		Object[] values = new Object[innerSensors.length];

		for (int i = 0; i < innerSensors.length; i++) {
			values[i] = innerSensors[i].getCheapValue();

			if (values[i] == null) {
				return null;
			}
		}
		return computeCheapValue(values);
	}

	/**
	 * Computes the cheap value for the derived {@link IntegerSensor}.
	 *
	 * @param values the inner sensor's cheap values
	 * @return the computed cheap value.
	 */
	protected abstract Integer computeCheapValue(Object[] values);

	@Override
	public final boolean equals(Object obj) {
		if (!classEqual(obj)) {
			return false;
		}
		DerivedIntegerSensor other = (DerivedIntegerSensor) obj;

		for (int i = 0; i < innerSensors.length; i++) {
			if (!innerSensors[i].equals(other.innerSensors[i])) {
				return false;
			}
		}
		return equals2(other);
	}

	/**
	 * Override to refine whether some other sensor is "equal to" this one by
	 * comparing additional attributes.
	 *
	 * @return <code>true</code> if this object has the same additional attributes
	 *         as the given sensor; <code>false</code> otherwise.
	 */
	protected boolean equals2(DerivedIntegerSensor obj) {
		return true;
	}

	@Override
	public final int hashCode() {
		int hashCode = classHash((Object[]) innerSensors);
		return hash(hashCode, getMoreObjectsForHashCode());
	}

	/**
	 * Override to supply more objects for calculation the hash code.
	 *
	 * @return more objects for the hash code.
	 */
	protected Object[] getMoreObjectsForHashCode() {
		return new Object[0];
	}

	protected final Sensor<?> getInnerSensor(int index) {
		return innerSensors[index];
	}

}
