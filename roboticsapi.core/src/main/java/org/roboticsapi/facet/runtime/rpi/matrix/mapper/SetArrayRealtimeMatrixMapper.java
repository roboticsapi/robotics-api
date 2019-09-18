/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.matrix.mapper;

import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.world.realtimevalue.realtimematrix.SetArrayRealtimeMatrix;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.matrix.RealtimeMatrixFragment;
import org.roboticsapi.facet.runtime.rpi.matrix.primitives.MatrixArraySet;

public class SetArrayRealtimeMatrixMapper extends TypedRealtimeValueFragmentFactory<Matrix, SetArrayRealtimeMatrix> {

	public SetArrayRealtimeMatrixMapper() {
		super(SetArrayRealtimeMatrix.class);
	}

	@Override
	protected RealtimeValueFragment<Matrix> createFragment(SetArrayRealtimeMatrix value)
			throws MappingException, RpiException {
		MatrixArraySet matrixSet = new MatrixArraySet(value.getRowDimension(), value.getColumnDimension(),
				value.getRowIndex(), value.getColumIndex(), value.getBlockWidth());
		RealtimeMatrixFragment ret = new RealtimeMatrixFragment(value, matrixSet.getOutMatrix());
		ret.addDependency(value.getMatrix(), "inMatrix", matrixSet.getInMatrix());
		ret.addDependency(value.getValue(), "inValue", matrixSet.getInArray());
		return ret;
	}
}
