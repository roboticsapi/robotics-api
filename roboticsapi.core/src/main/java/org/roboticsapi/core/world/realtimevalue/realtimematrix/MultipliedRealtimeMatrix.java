/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import org.roboticsapi.core.matrix.Matrix;

public class MultipliedRealtimeMatrix extends BinaryFunctionRealtimeMatrix<Matrix, Matrix> {

	public MultipliedRealtimeMatrix(RealtimeMatrix a, RealtimeMatrix b) {
		super(a, b, a.getRowDimension(), b.getColumnDimension());
	}

	public RealtimeMatrix getOperand1() {
		return (RealtimeMatrix) this.operand1;
	}

	public RealtimeMatrix getOperand2() {
		return (RealtimeMatrix) this.operand2;
	}

	@Override
	protected Matrix computeCheapValue(Matrix a, Matrix b) {
		return a.multiply(b);
	}

}
