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

/**
 * Gets a matrix block and returns it as a matrix.
 */
public class JMatrixBlockGet extends JPrimitive {

	/** Input matrix */
	final JInPort<MutableMatrix> inMatrix = add("inMatrix", new JInPort<MutableMatrix>());

	/** Output matrix */
	final JOutPort<MutableMatrix> outMatrix = add("outMatrix", new JOutPort<MutableMatrix>());

	/** Start row of block */
	final JParameter<RPIint> propStartRow = add("StartRow", new JParameter<RPIint>(new RPIint("0")));

	/** Start column of block */
	final JParameter<RPIint> propStartCol = add("StartCol", new JParameter<RPIint>(new RPIint("0")));

	/** Block width (i.e. number of columns) */
	final JParameter<RPIint> propBlockWidth = add("BlockWidth", new JParameter<RPIint>(new RPIint("1")));

	/** Block height (i.e. number of rows) */
	final JParameter<RPIint> propBlockHeight = add("BlockHeight", new JParameter<RPIint>(new RPIint("1")));

	int[] rows, cols;
	MutableMatrix ret;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		rows = new int[propBlockHeight.get().get()];
		for (int i = 0; i < rows.length; i++)
			rows[i] = propStartRow.get().get() + i;
		cols = new int[propBlockWidth.get().get()];
		for (int i = 0; i < cols.length; i++)
			cols[i] = propStartCol.get().get() + i;
		ret = new MutableMatrix(rows.length, cols.length);
	}

	@Override
	public void updateData() {
		inMatrix.get().selectTo(rows, cols, ret);
		outMatrix.set(ret);
	}

}
