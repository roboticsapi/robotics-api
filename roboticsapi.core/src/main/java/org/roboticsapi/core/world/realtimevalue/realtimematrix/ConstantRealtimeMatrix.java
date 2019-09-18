/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimematrix;

import org.roboticsapi.core.matrix.Matrix;

public class ConstantRealtimeMatrix extends RealtimeMatrix {

	private final Matrix constantValue;

	public ConstantRealtimeMatrix(Matrix constantValue) {
		super(null, constantValue.getRowDimension(), constantValue.getColumnDimension());
		this.constantValue = constantValue;
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	public Matrix getConstantValue() {
		return constantValue;
	}

	@Override
	protected Matrix calculateCheapValue() {
		return getConstantValue();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && constantValue.equals(((ConstantRealtimeMatrix) obj).constantValue);
	}

}
