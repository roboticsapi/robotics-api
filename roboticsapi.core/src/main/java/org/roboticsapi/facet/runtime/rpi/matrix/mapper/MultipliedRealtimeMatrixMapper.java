/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.matrix.mapper;

import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.world.realtimevalue.realtimematrix.MultipliedRealtimeMatrix;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.matrix.RealtimeMatrixFragment;
import org.roboticsapi.facet.runtime.rpi.matrix.primitives.MatrixMultiply;

public class MultipliedRealtimeMatrixMapper
		extends TypedRealtimeValueFragmentFactory<Matrix, MultipliedRealtimeMatrix> {

	public MultipliedRealtimeMatrixMapper() {
		super(MultipliedRealtimeMatrix.class);
	}

	@Override
	protected RealtimeValueFragment<Matrix> createFragment(MultipliedRealtimeMatrix value)
			throws MappingException, RpiException {

		MatrixMultiply matrixMultiply = new MatrixMultiply(value.getRowDimension(), value.getColumnDimension());
		RealtimeMatrixFragment ret = new RealtimeMatrixFragment(value, matrixMultiply.getOutMatrix());
		ret.addDependency(value.getOperand1(), "inFirst", matrixMultiply.getInMatrix1());
		ret.addDependency(value.getOperand2(), "inSecond", matrixMultiply.getInMatrix2());
		return ret;
	}
}
