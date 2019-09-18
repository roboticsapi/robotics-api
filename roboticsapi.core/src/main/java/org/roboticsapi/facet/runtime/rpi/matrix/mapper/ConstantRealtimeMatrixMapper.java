/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.matrix.mapper;

import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.world.realtimevalue.realtimematrix.ConstantRealtimeMatrix;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.netcomm.WriteDoubleArrayToNet;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.matrix.RealtimeMatrixFragment;
import org.roboticsapi.facet.runtime.rpi.matrix.primitives.MatrixArraySet;
import org.roboticsapi.facet.runtime.rpi.matrix.primitives.MatrixCreate;

public class ConstantRealtimeMatrixMapper extends TypedRealtimeValueFragmentFactory<Matrix, ConstantRealtimeMatrix> {

	public ConstantRealtimeMatrixMapper() {
		super(ConstantRealtimeMatrix.class);
	}

	@Override
	protected RealtimeValueFragment<Matrix> createFragment(ConstantRealtimeMatrix value)
			throws MappingException, RpiException {
		RealtimeMatrixFragment fragment = new RealtimeMatrixFragment(value);

		Matrix matrix = value.getConstantValue();
		double[] values = matrix.asArray();

		MatrixCreate matrixCreate = fragment
				.add(new MatrixCreate(matrix.getRowDimension(), matrix.getColumnDimension()));

		MatrixArraySet matrixSet = fragment.add(new MatrixArraySet(matrix.getRowDimension(),
				matrix.getColumnDimension(), 0, 0, matrix.getColumnDimension()));
		matrixSet.setArray(WriteDoubleArrayToNet.toString(values));

		fragment.defineResult(matrixSet.getOutMatrix());
		fragment.connect(matrixCreate.getOutMatrix(), matrixSet.getInMatrix());
		return fragment;

	}
}
