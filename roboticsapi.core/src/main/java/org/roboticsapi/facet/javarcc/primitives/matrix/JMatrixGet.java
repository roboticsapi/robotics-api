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
 * Reads a matrix value.
 */
public class JMatrixGet extends JPrimitive {

	/** Input matrix */
	final JInPort<MutableMatrix> inMatrix = add("inMatrix", new JInPort<MutableMatrix>());

	/** Extracted value */
	final JOutPort<RPIdouble> outValue = add("outValue", new JOutPort<RPIdouble>());

	/** Row index */
	final JParameter<RPIint> propRow = add("Row", new JParameter<RPIint>(new RPIint("0")));

	/** Column index */
	final JParameter<RPIint> propCol = add("Col", new JParameter<RPIint>(new RPIint("0")));

	RPIdouble ret = new RPIdouble();

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		ret.set(inMatrix.get().get(propRow.get().get(), propCol.get().get()));
		outValue.set(ret);
	}

}
