/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;

public class SetArrayRealtimeMatrix extends BinaryFunctionRealtimeMatrix<Matrix, Double[]> {

	private final int rowIndex;
	private final int columIndex;
	private final int blockWidth;

	public SetArrayRealtimeMatrix(RealtimeMatrix matrix, RealtimeDoubleArray value, int rowIndex, int columIndex,
			int blockWidth) {
		super(matrix, value, matrix.getRowDimension(), matrix.getColumnDimension());
		this.rowIndex = rowIndex;
		this.columIndex = columIndex;
		this.blockWidth = blockWidth;
	}

	public RealtimeMatrix getMatrix() {
		return (RealtimeMatrix) this.operand1;
	}

	public RealtimeDoubleArray getValue() {
		return (RealtimeDoubleArray) this.operand2;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public int getColumIndex() {
		return columIndex;
	}

	public int getBlockWidth() {
		return blockWidth;
	}

	@Override
	protected Matrix computeCheapValue(Matrix s, Double[] t) {
		for (int i = 0; i < t.length; i++) {
			s = s.with(rowIndex + i / blockWidth, columIndex + i % blockWidth, t[i]);
		}
		return s;
	}

	@Override
	protected boolean equals2(BinaryFunctionRealtimeMatrix<?, ?> obj) {
		SetArrayRealtimeMatrix o = (SetArrayRealtimeMatrix) obj;

		return rowIndex == o.rowIndex && columIndex == o.columIndex && blockWidth == o.blockWidth;
	}

	@Override
	protected Object[] getMoreObjectsForHashCode() {
		return new Object[] { rowIndex, columIndex, blockWidth };
	}

}
