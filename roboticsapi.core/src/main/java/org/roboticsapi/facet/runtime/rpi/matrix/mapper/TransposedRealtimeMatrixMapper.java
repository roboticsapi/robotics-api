/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.matrix.mapper;

import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.world.realtimevalue.realtimematrix.TransposedRealtimeMatrix;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.matrix.RealtimeMatrixFragment;
import org.roboticsapi.facet.runtime.rpi.matrix.primitives.MatrixTranspose;

public class TransposedRealtimeMatrixMapper
		extends TypedRealtimeValueFragmentFactory<Matrix, TransposedRealtimeMatrix> {

	public TransposedRealtimeMatrixMapper() {
		super(TransposedRealtimeMatrix.class);
	}

	@Override
	protected RealtimeValueFragment<Matrix> createFragment(TransposedRealtimeMatrix value)
			throws MappingException, RpiException {
		MatrixTranspose matrixTranspose = new MatrixTranspose(value.getRowDimension(), value.getColumnDimension());
		RealtimeMatrixFragment ret = new RealtimeMatrixFragment(value, matrixTranspose.getOutMatrix());
		ret.addDependency(value.getMatrix(), "inMatrix", matrixTranspose.getInMatrix());
		return ret;
	}
}
