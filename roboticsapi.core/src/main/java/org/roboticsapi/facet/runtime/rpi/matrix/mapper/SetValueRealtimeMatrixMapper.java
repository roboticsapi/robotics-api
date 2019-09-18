/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.matrix.mapper;

import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.world.realtimevalue.realtimematrix.SetValueRealtimeMatrix;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.matrix.RealtimeMatrixFragment;
import org.roboticsapi.facet.runtime.rpi.matrix.primitives.MatrixSet;

public class SetValueRealtimeMatrixMapper extends TypedRealtimeValueFragmentFactory<Matrix, SetValueRealtimeMatrix> {

	public SetValueRealtimeMatrixMapper() {
		super(SetValueRealtimeMatrix.class);
	}

	@Override
	protected RealtimeValueFragment<Matrix> createFragment(SetValueRealtimeMatrix value)
			throws MappingException, RpiException {
		MatrixSet matrixSet = new MatrixSet(value.getRowDimension(), value.getColumnDimension(), value.getRowIndex(),
				value.getColumIndex());
		RealtimeMatrixFragment ret = new RealtimeMatrixFragment(value, matrixSet.getOutMatrix());
		ret.addDependency(value.getMatrix(), "inMatrix", matrixSet.getInMatrix());
		ret.addDependency(value.getValue(), "inValue", matrixSet.getInValue());
		return ret;
	}
}
