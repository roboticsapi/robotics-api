/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.sensor;

import org.roboticsapi.core.Sensor;

/**
 * Abstract implementation for a {@link DoubleSensor} that is derived from
 * another {@link Sensor} of type <code>T</code>.
 */
public abstract class UnaryFunctionDoubleSensor<T> extends DoubleSensor {

	/**
	 * The inner sensor.
	 */
	private final Sensor<T> inner;

	/**
	 * Constructor.
	 *
	 * @param inner the inner sensor.
	 */
	public UnaryFunctionDoubleSensor(Sensor<T> inner) {
		super(selectRuntime(inner));
		addInnerSensors(inner);
		this.inner = inner;
	}

	/**
	 * Returns the inner sensor.
	 *
	 * @return the inner sensor.
	 */
	public final Sensor<T> getInnerSensor() {
		return inner;
	}

	@Override
	protected final Double calculateCheapValue() {
		T value = inner.getCheapValue();
		if (value == null) {
			return null;
		}

		return computeCheapValue(value);
	}

	/**
	 * Computes the cheap value for the derived {@link DoubleSensor}.
	 *
	 * @param value the inner sensor's cheap value which is not <code>null</code>.
	 * @return the computed cheap value.
	 */
	protected abstract Double computeCheapValue(T value);

	@Override
	public final boolean equals(Object obj) {
		return classEqual(obj) && inner.equals(((UnaryFunctionDoubleSensor<?>) obj).inner)
				&& equals2((UnaryFunctionDoubleSensor<?>) obj);
	}

	/**
	 * Override to refine whether some other sensor is "equal to" this one by
	 * comparing additional attributes.
	 *
	 * @return <code>true</code> if this object has the same additional attributes
	 *         as the given sensor; <code>false</code> otherwise.
	 */
	protected boolean equals2(UnaryFunctionDoubleSensor<?> obj) {
		return true;
	}

	@Override
	public final int hashCode() {
		int hashCode = classHash(inner);
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

	@Override
	public final boolean isAvailable() {
		return inner.isAvailable();
	}

}
