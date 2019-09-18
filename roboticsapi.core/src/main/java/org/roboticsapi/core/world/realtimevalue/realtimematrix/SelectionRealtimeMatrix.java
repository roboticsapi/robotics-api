/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import java.util.Arrays;

import org.roboticsapi.core.matrix.Matrix;

public class SelectionRealtimeMatrix extends UnaryFunctionRealtimeMatrix {

	private int[] selectedRows;
	private int[] selectedColumns;

	public SelectionRealtimeMatrix(RealtimeMatrix matrix, int[] selectedRows, int[] selectedColumns) {
		super(matrix, matrix.getRowDimension(), matrix.getColumnDimension());

		this.selectedRows = selectedRows;
		this.selectedColumns = selectedColumns;
	}

	public RealtimeMatrix getMatrix() {
		return inner;
	}

	public int[] getSelectedRows() {
		return selectedRows;
	}

	public int[] getSelectedColumns() {
		return selectedColumns;
	}

	@Override
	protected Matrix computeCheapValue(Matrix m) {
		return m.select(selectedRows, selectedColumns);
	}

	@Override
	protected boolean equals2(UnaryFunctionRealtimeMatrix obj) {
		SelectionRealtimeMatrix o = (SelectionRealtimeMatrix) obj;

		return Arrays.equals(selectedRows, o.selectedRows) && Arrays.equals(selectedColumns, o.selectedColumns);
	}

	@Override
	protected Object[] getMoreObjectsForHashCode() {
		return new Object[] { selectedRows, selectedColumns };
	}
}
