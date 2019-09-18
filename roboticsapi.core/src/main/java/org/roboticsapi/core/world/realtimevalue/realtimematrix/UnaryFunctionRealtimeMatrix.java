/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import org.roboticsapi.core.matrix.Matrix;

/**
 * Abstract implementation for a {@link RealtimeMatrix} that is derived from
 * another {@link RealtimeMatrix}.
 */
public abstract class UnaryFunctionRealtimeMatrix extends RealtimeMatrix {

	/**
	 * The inner sensor.
	 */
	protected final RealtimeMatrix inner;

	/**
	 * Constructor.
	 *
	 * @param inner the inner sensor.
	 */
	public UnaryFunctionRealtimeMatrix(RealtimeMatrix inner, int rows, int cols) {
		super(rows, cols, inner);

		this.inner = inner;
	}

	@Override
	protected final Matrix calculateCheapValue() {
		Matrix m = inner.getCheapValue();

		if (m == null) {
			return null;
		}
		return computeCheapValue(m);
	}

	protected abstract Matrix computeCheapValue(Matrix m);

	@Override
	public final boolean equals(Object obj) {
		return classEqual(obj) && inner.equals(((UnaryFunctionRealtimeMatrix) obj).inner)
				&& equals2((UnaryFunctionRealtimeMatrix) obj);
	}

	/**
	 * Override to refine whether some other sensor is "equal to" this one by
	 * comparing additional attributes.
	 *
	 * @return <code>true</code> if this object has the same additional attributes
	 *         as the given sensor; <code>false</code> otherwise.
	 */
	protected boolean equals2(UnaryFunctionRealtimeMatrix obj) {
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
