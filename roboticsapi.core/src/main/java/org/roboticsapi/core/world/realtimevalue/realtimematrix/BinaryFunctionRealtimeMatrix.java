/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.matrix.Matrix;

/**
 * Abstract implementation for a {@link RealtimeMatrix} that is derived from two
 * other {@link RealtimeValue}s of type <code>S</code> and <code>T</code>
 * respectively.
 */
public abstract class BinaryFunctionRealtimeMatrix<S, T> extends RealtimeMatrix {

	/**
	 * The first inner sensor.
	 */
	protected final RealtimeValue<S> operand1;

	/**
	 * The second inner sensor.
	 */
	protected final RealtimeValue<T> operand2;

	/**
	 * Constructor.
	 *
	 * @param inner the inner sensor.
	 */
	public BinaryFunctionRealtimeMatrix(RealtimeValue<S> op1, RealtimeValue<T> op2, int rows, int cols) {
		super(rows, cols, op1, op2);
		this.operand1 = op1;
		this.operand2 = op2;
	}

	@Override
	protected final Matrix calculateCheapValue() {
		S s = operand1.getCheapValue();
		T t = operand2.getCheapValue();

		if (s == null || t == null) {
			return null;
		}

		return computeCheapValue(s, t);
	}

	protected abstract Matrix computeCheapValue(S s, T t);

	@Override
	public final boolean equals(Object obj) {
		return classEqual(obj) && operand1.equals(((BinaryFunctionRealtimeMatrix<?, ?>) obj).operand1)
				&& operand2.equals(((BinaryFunctionRealtimeMatrix<?, ?>) obj).operand2)
				&& equals2((BinaryFunctionRealtimeMatrix<?, ?>) obj);
	}

	/**
	 * Override to refine whether some other sensor is "equal to" this one by
	 * comparing additional attributes.
	 *
	 * @return <code>true</code> if this object has the same additional attributes
	 *         as the given sensor; <code>false</code> otherwise.
	 */
	protected boolean equals2(BinaryFunctionRealtimeMatrix<?, ?> obj) {
		return true;
	}

	@Override
	public final int hashCode() {
		int hashCode = classHash(operand1, operand2);
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
		return operand1.isAvailable() && operand2.isAvailable();
	}

}
