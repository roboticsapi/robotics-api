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
 * Transposes a matrix.
 */
public class JMatrixTranspose extends JPrimitive {

	/** Input matrix */
	final JInPort<MutableMatrix> inMatrix = add("inMatrix", new JInPort<MutableMatrix>());

	/** Transposed matrix */
	final JOutPort<MutableMatrix> outMatrix = add("outMatrix", new JOutPort<MutableMatrix>());

	/** Number of rows */
	final JParameter<RPIint> propRows = add("Rows", new JParameter<RPIint>(new RPIint("0")));

	/** Number of columns */
	final JParameter<RPIint> propCols = add("Cols", new JParameter<RPIint>(new RPIint("0")));

	MutableMatrix ret;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		ret = new MutableMatrix(propRows.get().get(), propCols.get().get());
	}

	@Override
	public void updateData() {
		inMatrix.get().transposeTo(ret);
		outMatrix.set(ret);
	}

}
