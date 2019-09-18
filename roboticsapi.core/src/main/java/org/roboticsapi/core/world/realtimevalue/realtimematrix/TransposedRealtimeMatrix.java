/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import org.roboticsapi.core.matrix.Matrix;

public class TransposedRealtimeMatrix extends UnaryFunctionRealtimeMatrix {

	public TransposedRealtimeMatrix(RealtimeMatrix matrix) {
		super(matrix, matrix.getColumnDimension(), matrix.getRowDimension());
	}

	public RealtimeMatrix getMatrix() {
		return inner;
	}

	@Override
	protected Matrix computeCheapValue(Matrix m) {
		return m.transpose();
	}
}
