/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class SetValueRealtimeMatrix extends BinaryFunctionRealtimeMatrix<Matrix, Double> {

	private final int rowIndex;
	private final int columIndex;

	public SetValueRealtimeMatrix(RealtimeMatrix matrix, RealtimeDouble value, int rowIndex, int columIndex) {
		super(matrix, value, matrix.getRowDimension(), matrix.getColumnDimension());
		this.rowIndex = rowIndex;
		this.columIndex = columIndex;
	}

	public RealtimeMatrix getMatrix() {
		return (RealtimeMatrix) this.operand1;
	}

	public RealtimeDouble getValue() {
		return (RealtimeDouble) this.operand2;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public int getColumIndex() {
		return columIndex;
	}

	@Override
	protected Matrix computeCheapValue(Matrix m, Double v) {
		return m.with(rowIndex, columIndex, v);
	}

	@Override
	protected boolean equals2(BinaryFunctionRealtimeMatrix<?, ?> obj) {
		SetValueRealtimeMatrix o = (SetValueRealtimeMatrix) obj;

		return rowIndex == o.rowIndex && columIndex == o.columIndex;
	}

	@Override
	protected Object[] getMoreObjectsForHashCode() {
		return new Object[] { rowIndex, columIndex };
	}

}
