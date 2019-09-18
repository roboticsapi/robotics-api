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
 * Gets a matrix block and returns it as an array.
 */
public class JMatrixArrayGet extends JPrimitive {

	/** Input matrix */
	final JInPort<MutableMatrix> inMatrix = add("inMatrix", new JInPort<MutableMatrix>());

	/** Output array */
	final JOutPort<RPIdoubleArray> outArray = add("outArray", new JOutPort<RPIdoubleArray>());

	/** Start row of block */
	final JParameter<RPIint> propStartRow = add("StartRow", new JParameter<RPIint>(new RPIint("0")));

	/** Start column of block */
	final JParameter<RPIint> propStartCol = add("StartCol", new JParameter<RPIint>(new RPIint("0")));

	/** Block width */
	final JParameter<RPIint> propBlockWidth = add("BlockWidth", new JParameter<RPIint>(new RPIint("1")));

	/** Data size */
	final JParameter<RPIint> propSize = add("Size", new JParameter<RPIint>(new RPIint("1")));

	private RPIdoubleArray array = null;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		array = new RPIdoubleArray(propSize.get().get());
	}

	@Override
	public void updateData() {
		MutableMatrix matrix = inMatrix.get();
		for (int i = 0; i < propSize.get().get(); i++) {
			array.get(i).set(matrix.get(propStartRow.get().get() + (i / propBlockWidth.get().get()),
					propStartCol.get().get() + (i % propBlockWidth.get().get())));
		}
		outArray.set(array);
	}

}
