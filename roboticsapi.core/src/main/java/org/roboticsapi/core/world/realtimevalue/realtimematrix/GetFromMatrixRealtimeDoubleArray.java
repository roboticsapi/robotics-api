/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;

public class GetFromMatrixRealtimeDoubleArray extends RealtimeDoubleArray {

	private final RealtimeMatrix inner;

	private final int rowIndex;
	private final int colIndex;
	private final int blockWidth;
	private final int length;

	public GetFromMatrixRealtimeDoubleArray(RealtimeMatrix inner, int rowIndex, int columnIndex, int blockWidth,
			int length) {
		super(length, inner);
		this.inner = inner;
		this.rowIndex = rowIndex;
		this.colIndex = columnIndex;
		this.blockWidth = blockWidth;
		this.length = length;
	}

	public final RealtimeMatrix getMatrix() {
		return inner;
	}

	public final int getRowIndex() {
		return rowIndex;
	}

	public final int getColumnIndex() {
		return colIndex;
	}

	public final int getBlockWidth() {
		return blockWidth;
	}

	public final int getLength() {
		return length;
	}

	@Override
	public boolean isAvailable() {
		return inner.isAvailable();
	}

	@Override
	protected Double[] calculateCheapValue() {
		Matrix matrix = inner.getCheapValue();

		if (matrix == null) {
			return null;
		}
		return matrix.fillArray(rowIndex, colIndex, blockWidth, new Double[length]);
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && colIndex == ((GetFromMatrixRealtimeDoubleArray) obj).colIndex
				&& rowIndex == ((GetFromMatrixRealtimeDoubleArray) obj).rowIndex
				&& blockWidth == ((GetFromMatrixRealtimeDoubleArray) obj).blockWidth
				&& length == ((GetFromMatrixRealtimeDoubleArray) obj).length;
	}

}
