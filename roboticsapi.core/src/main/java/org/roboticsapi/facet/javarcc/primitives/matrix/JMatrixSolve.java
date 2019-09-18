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
 * Solves a linear equation system with the given matrix and result
 */
public class JMatrixSolve extends JPrimitive {

	/** Matrix */
	final JInPort<MutableMatrix> inMatrix = add("inMatrix", new JInPort<MutableMatrix>());

	/** Vector */
	final JInPort<RPIdoubleArray> inResult = add("inResult", new JInPort<RPIdoubleArray>());

	/** Result (Matrix * Result = Vector) */
	final JOutPort<RPIdoubleArray> outValue = add("outValue", new JOutPort<RPIdoubleArray>());

	/** Size of the resulting vector */
	final JParameter<RPIint> propSize = add("Size", new JParameter<RPIint>(new RPIint("0")));

	private RPIdoubleArray result;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		result = new RPIdoubleArray(propSize.get().get());
	}

	@Override
	public void updateData() {
		double[] value = new double[inResult.get().getSize()];
		for (int i = 0; i < value.length; i++)
			value[i] = inResult.get().get(i).get();
		double[] solved = new double[propSize.get().get()];
		if (inMatrix.get().solveTo(value, solved)) {
			for (int i = 0; i < solved.length; i++)
				result.get(i).set(solved[i]);
			outValue.set(result);
		} else {
			outValue.set(null);
		}
	}

}
