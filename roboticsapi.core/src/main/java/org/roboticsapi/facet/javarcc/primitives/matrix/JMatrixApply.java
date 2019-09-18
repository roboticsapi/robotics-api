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
 * Transforms a vector by a matrix
 */
public class JMatrixApply extends JPrimitive {

	/** Matrix */
	final JInPort<MutableMatrix> inMatrix = add("inMatrix", new JInPort<MutableMatrix>());

	/** Vector */
	final JInPort<RPIdoubleArray> inVector = add("inVector", new JInPort<RPIdoubleArray>());

	/** Result (Matrix * Vector) */
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
		MutableMatrix matrix = inMatrix.get();
		double[] vec = new double[inVector.get().getSize()];
		double[] applied = new double[propSize.get().get()];
		for (int i = 0; i < vec.length; i++)
			vec[i] = inVector.get().get(i).get();
		matrix.applyTo(vec, applied);
		for (int i = 0; i < applied.length; i++)
			result.get(i).set(applied[i]);
		outValue.set(result);
	}

}
