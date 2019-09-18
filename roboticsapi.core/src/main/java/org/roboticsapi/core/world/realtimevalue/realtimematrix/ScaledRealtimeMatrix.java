/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public class ScaledRealtimeMatrix extends BinaryFunctionRealtimeMatrix<Matrix, Double> {

	public ScaledRealtimeMatrix(RealtimeMatrix matrix, RealtimeDouble scalar) {
		super(matrix, scalar, matrix.getRowDimension(), matrix.getColumnDimension());
	}

	public RealtimeMatrix getMatrix() {
		return (RealtimeMatrix) this.operand1;
	}

	public RealtimeDouble getScalar() {
		return (RealtimeDouble) this.operand2;
	}

	@Override
	protected Matrix computeCheapValue(Matrix s, Double t) {
		return s.multiply(t);
	}

}
