/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.matrix;

import org.roboticsapi.core.world.mutable.MutableMatrix;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;

/**
 * Creates an matrix.
 */
public class JMatrixCreate extends JPrimitive {

	/** Value */
	final JOutPort<MutableMatrix> outMatrix = add("outMatrix", new JOutPort<MutableMatrix>());

	/** Number of rows */
	final JParameter<RPIint> propRows = add("Rows", new JParameter<RPIint>(new RPIint("1")));

	/** Number of cols */
	final JParameter<RPIint> propCols = add("Cols", new JParameter<RPIint>(new RPIint("1")));

	private MutableMatrix matrix;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		matrix = new MutableMatrix(propRows.get().get(), propCols.get().get());
	}

	@Override
	public void updateData() {
		outMatrix.set(matrix);
	}

}
