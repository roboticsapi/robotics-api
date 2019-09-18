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
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Scales a matrix.
 */
public class JMatrixScale extends JPrimitive {

	/** Input matrix */
	final JInPort<MutableMatrix> inMatrix = add("inMatrix", new JInPort<MutableMatrix>());

	/** Scalar */
	final JInPort<RPIdouble> inValue = add("inValue", new JInPort<RPIdouble>());

	/** Scaled matrix */
	final JOutPort<MutableMatrix> outMatrix = add("outMatrix", new JOutPort<MutableMatrix>());

	/** Rows count */
	final JParameter<RPIint> propRows = add("Rows", new JParameter<RPIint>(new RPIint("0")));

	/** Column count */
	final JParameter<RPIint> propCols = add("Cols", new JParameter<RPIint>(new RPIint("0")));

	/** Data */
	final JParameter<RPIdouble> propValue = add("Value", new JParameter<RPIdouble>(new RPIdouble("0")));

	MutableMatrix ret;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		ret = new MutableMatrix(propRows.get().get(), propCols.get().get());
	}

	@Override
	public void updateData() {
		for (int row = 0; row < ret.getRowDimension(); row++) {
			for (int col = 0; col < ret.getColumnDimension(); col++) {
				ret.set(row, col, inMatrix.get().get(row, col) * inValue.get().get());
			}
		}
		outMatrix.set(ret);
	}

}
