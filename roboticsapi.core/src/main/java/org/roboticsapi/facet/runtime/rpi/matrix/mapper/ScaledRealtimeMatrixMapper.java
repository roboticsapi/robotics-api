/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.matrix.mapper;

import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.world.realtimevalue.realtimematrix.ScaledRealtimeMatrix;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.matrix.RealtimeMatrixFragment;
import org.roboticsapi.facet.runtime.rpi.matrix.primitives.MatrixScale;

public class ScaledRealtimeMatrixMapper extends TypedRealtimeValueFragmentFactory<Matrix, ScaledRealtimeMatrix> {

	public ScaledRealtimeMatrixMapper() {
		super(ScaledRealtimeMatrix.class);
	}

	@Override
	protected RealtimeValueFragment<Matrix> createFragment(ScaledRealtimeMatrix value)
			throws MappingException, RpiException {
		MatrixScale matrixScale = new MatrixScale(value.getRowDimension(), value.getColumnDimension());
		RealtimeMatrixFragment ret = new RealtimeMatrixFragment(value, matrixScale.getOutMatrix());
		ret.addDependency(value.getMatrix(), "inMatrix", matrixScale.getInMatrix());
		ret.addDependency(value.getScalar(), "inScale", matrixScale.getInValue());
		return ret;
	}

}
