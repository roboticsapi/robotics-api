/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimedouble;

import org.roboticsapi.core.RealtimeValue;

/**
 * Abstract implementation for a {@link RealtimeDouble} that is derived from
 * another {@link RealtimeValue} of type <code>T</code>.
 */
public abstract class UnaryFunctionRealtimeDouble<T> extends RealtimeDouble {

	/**
	 * The inner RealtimeValue.
	 */
	private final RealtimeValue<T> inner;

	/**
	 * Constructor.
	 *
	 * @param inner the inner RealtimeValue.
	 */
	public UnaryFunctionRealtimeDouble(RealtimeValue<T> inner) {
		super(inner);
		this.inner = inner;
	}

	/**
	 * Returns the inner RealtimeValue.
	 *
	 * @return the inner RealtimeValue.
	 */
	public final RealtimeValue<T> getInnerValue() {
		return inner;
	}

	@Override
	public final Double calculateCheapValue() {
		T value = inner.getCheapValue();
		if (value == null) {
			return null;
		}

		return computeCheapValue(value);
	}

	/**
	 * Computes the cheap value for the derived {@link RealtimeDouble}.
	 *
	 * @param value the inner sensor's cheap value which is not <code>null</code>.
	 * @return the computed cheap value.
	 */
	protected abstract Double computeCheapValue(T value);

	@Override
	public final boolean equals(Object obj) {
		return classEqual(obj) && inner.equals(((UnaryFunctionRealtimeDouble<?>) obj).inner)
				&& equals2((UnaryFunctionRealtimeDouble<?>) obj);
	}

	/**
	 * Override to refine whether some other sensor is "equal to" this one by
	 * comparing additional attributes.
	 *
	 * @return <code>true</code> if this object has the same additional attributes
	 *         as the given sensor; <code>false</code> otherwise.
	 */
	protected boolean equals2(UnaryFunctionRealtimeDouble<?> obj) {
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
