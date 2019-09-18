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
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdoubleArray;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Set a matrix block using an array.
 */
public class JMatrixArraySet extends JPrimitive {

	/** Input array */
	final JInPort<RPIdoubleArray> inArray = add("inArray", new JInPort<RPIdoubleArray>());

	/** Input matrix */
	final JInPort<MutableMatrix> inMatrix = add("inMatrix", new JInPort<MutableMatrix>());

	/** Modified matrix */
	final JOutPort<MutableMatrix> outMatrix = add("outMatrix", new JOutPort<MutableMatrix>());

	/** Number of rows */
	final JParameter<RPIint> propRows = add("Rows", new JParameter<RPIint>(new RPIint("0")));

	/** Number of columns */
	final JParameter<RPIint> propCols = add("Cols", new JParameter<RPIint>(new RPIint("0")));

	/** Start row of block */
	final JParameter<RPIint> propStartRow = add("StartRow", new JParameter<RPIint>(new RPIint("0")));

	/** Start col of block */
	final JParameter<RPIint> propStartCol = add("StartCol", new JParameter<RPIint>(new RPIint("0")));

	/** Block width */
	final JParameter<RPIint> propBlockWidth = add("BlockWidth", new JParameter<RPIint>(new RPIint("1")));

	/** Data */
	final JParameter<RPIdoubleArray> propArray = add("Array", new JParameter<RPIdoubleArray>(new RPIdoubleArray("[]")));

	MutableMatrix ret;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		ret = new MutableMatrix(propRows.get().get(), propCols.get().get());
	}

	@Override
	public void updateData() {
		RPIdoubleArray array = inArray.get(propArray);
		inMatrix.get().copyTo(ret);
		for (int i = 0; i < array.getSize(); i++) {
			ret.set(propStartCol.get().get() + (i / propBlockWidth.get().get()),
					propStartCol.get().get() + (i % propBlockWidth.get().get()), array.get(i).get());
		}
		outMatrix.set(ret);
	}

}
