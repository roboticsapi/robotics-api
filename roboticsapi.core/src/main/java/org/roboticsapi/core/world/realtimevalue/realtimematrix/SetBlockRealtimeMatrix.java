/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import org.roboticsapi.core.matrix.Matrix;

public class SetBlockRealtimeMatrix extends BinaryFunctionRealtimeMatrix<Matrix, Matrix> {

	private final int rowIndex;
	private final int columIndex;

	public SetBlockRealtimeMatrix(RealtimeMatrix matrix, RealtimeMatrix block, int rowIndex, int columIndex) {
		super(matrix, block, matrix.getRowDimension(), matrix.getColumnDimension());
		this.rowIndex = rowIndex;
		this.columIndex = columIndex;
	}

	public RealtimeMatrix getMatrix() {
		return (RealtimeMatrix) this.operand1;
	}

	public RealtimeMatrix getBlock() {
		return (RealtimeMatrix) this.operand2;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public int getColumIndex() {
		return columIndex;
	}

	@Override
	protected Matrix computeCheapValue(Matrix m, Matrix b) {
		return m.withBlock(rowIndex, columIndex, b);
	}

	@Override
	protected boolean equals2(BinaryFunctionRealtimeMatrix<?, ?> obj) {
		SetBlockRealtimeMatrix o = (SetBlockRealtimeMatrix) obj;

		return rowIndex == o.rowIndex && columIndex == o.columIndex;
	}

	@Override
	protected Object[] getMoreObjectsForHashCode() {
		return new Object[] { rowIndex, columIndex };
	}

}
