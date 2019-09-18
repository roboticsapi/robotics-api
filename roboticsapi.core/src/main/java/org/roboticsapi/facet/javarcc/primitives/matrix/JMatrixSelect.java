/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.matrix;

import org.roboticsapi.core.world.mutable.MutableMatrix;
import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIintArray;

/**
 * Selects rows and columns of a matrix.
 */
public class JMatrixSelect extends JPrimitive {

	/** Input matrix */
	final JInPort<MutableMatrix> inMatrix = add("inMatrix", new JInPort<MutableMatrix>());

	/** Output matrix with selected rows and columns */
	final JOutPort<MutableMatrix> outMatrix = add("outMatrix", new JOutPort<MutableMatrix>());

	/** Row dimension of output matrix */
	final JParameter<RPIint> propRowDimension = add("RowDimension", new JParameter<RPIint>(new RPIint("0")));

	/** Column dimension of output matrix */
	final JParameter<RPIint> propColumnDimension = add("ColumnDimension", new JParameter<RPIint>(new RPIint("0")));

	/** Array with indices of selected rows */
	final JParameter<RPIintArray> propSelectedRows = add("SelectedRows",
			new JParameter<RPIintArray>(new RPIintArray("[]")));

	/** Array with indices of selected columns */
	final JParameter<RPIintArray> propSelectedCols = add("SelectedCols",
			new JParameter<RPIintArray>(new RPIintArray("[]")));

	int[] rows, cols;
	MutableMatrix ret;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		rows = new int[propSelectedRows.get().getSize()];
		for (int i = 0; i < rows.length; i++)
			rows[i] = propSelectedRows.get().get(i).get();

		cols = new int[propSelectedCols.get().getSize()];
		for (int i = 0; i < cols.length; i++)
			cols[i] = propSelectedCols.get().get(i).get();

		ret = new MutableMatrix(rows.length, cols.length);
	}

	@Override
	public void updateData() {
		for (int row = 0; row < ret.getRowDimension(); row++)
			for (int col = 0; col < ret.getColumnDimension(); col++)
				ret.set(row, col, inMatrix.get().get(rows[row], cols[col]));
		outMatrix.set(ret);
	}

}
