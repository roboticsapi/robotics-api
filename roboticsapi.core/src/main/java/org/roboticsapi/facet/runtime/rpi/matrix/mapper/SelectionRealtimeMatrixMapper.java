/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.matrix.mapper;

import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.world.realtimevalue.realtimematrix.SelectionRealtimeMatrix;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.netcomm.WriteIntArrayToNet;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.matrix.RealtimeMatrixFragment;
import org.roboticsapi.facet.runtime.rpi.matrix.primitives.MatrixSelect;

public class SelectionRealtimeMatrixMapper extends TypedRealtimeValueFragmentFactory<Matrix, SelectionRealtimeMatrix> {

	public SelectionRealtimeMatrixMapper() {
		super(SelectionRealtimeMatrix.class);
	}

	@Override
	protected RealtimeValueFragment<Matrix> createFragment(SelectionRealtimeMatrix value)
			throws MappingException, RpiException {

		String selectedRows = WriteIntArrayToNet.toString(value.getSelectedRows());
		String selectedCols = WriteIntArrayToNet.toString(value.getSelectedColumns());

		MatrixSelect matrixSelect = new MatrixSelect(value.getRowDimension(), value.getColumnDimension(), selectedRows,
				selectedCols);
		RealtimeMatrixFragment ret = new RealtimeMatrixFragment(value, matrixSelect.getOutMatrix());
		ret.addDependency(value.getMatrix(), "inMatrix", matrixSelect.getInMatrix());
		return ret;
	}
}
