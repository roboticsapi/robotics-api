/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

/**
 *
 */
package org.roboticsapi.core.realtimevalue.realtimeinteger;

import org.roboticsapi.core.RealtimeValue;

/**
 * Abstract implementation for a {@link RealtimeInteger} that is derived from
 * other {@link RealtimeValue}s.
 */
public abstract class DerivedRealtimeInteger extends RealtimeInteger {

	private final RealtimeValue<?>[] innerValues;

	/**
	 * Constructs a new {@link DerivedRealtimeInteger}.
	 *
	 * @param values the inner RealtimeValues.
	 */
	public DerivedRealtimeInteger(RealtimeValue<?>... values) {
		super(values);
		innerValues = values;
	}

	@Override
	public final boolean isAvailable() {
		return areAvailable(innerValues);
	}

	@Override
	protected final Integer calculateCheapValue() {
		Object[] values = new Object[innerValues.length];

		for (int i = 0; i < innerValues.length; i++) {
			values[i] = innerValues[i].getCheapValue();

			if (values[i] == null) {
				return null;
			}
		}
		return computeCheapValue(values);
	}

	/**
	 * Computes the cheap value for the derived {@link RealtimeInteger}.
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
		DerivedRealtimeInteger other = (DerivedRealtimeInteger) obj;

		for (int i = 0; i < innerValues.length; i++) {
			if (!innerValues[i].equals(other.innerValues[i])) {
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
	protected boolean equals2(DerivedRealtimeInteger obj) {
		return true;
	}

	@Override
	public final int hashCode() {
		int hashCode = classHash((Object[]) innerValues);
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

	protected final RealtimeValue<?> getInner(int index) {
		return innerValues[index];
	}

}
