/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.realtimevalue.realtimedouble.UnaryFunctionRealtimeDouble;

public class GetFromMatrixRealtimeDouble extends UnaryFunctionRealtimeDouble<Matrix> {

	private final int rowIndex;
	private final int colIndex;

	public GetFromMatrixRealtimeDouble(RealtimeMatrix inner, int rowIndex, int columnIndex) {
		super(inner);
		this.rowIndex = rowIndex;
		this.colIndex = columnIndex;
	}

	public final RealtimeMatrix getMatrix() {
		return (RealtimeMatrix) getInnerValue();
	}

	public final int getRowIndex() {
		return rowIndex;
	}

	public final int getColumnIndex() {
		return colIndex;
	}

	@Override
	protected Double computeCheapValue(Matrix value) {
		return value.get(rowIndex, colIndex);
	}

	@Override
	protected boolean equals2(UnaryFunctionRealtimeDouble<?> obj) {
		return rowIndex == ((GetFromMatrixRealtimeDouble) obj).rowIndex
				&& colIndex == ((GetFromMatrixRealtimeDouble) obj).colIndex;
	}

}
