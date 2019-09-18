/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.matrix.mapper;

import org.roboticsapi.core.world.realtimevalue.realtimematrix.AppliedMatrixRealtimeDoubleArray;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleArrayFragment;
import org.roboticsapi.facet.runtime.rpi.matrix.primitives.MatrixApply;

public class AppliedMatrixRealtimeDoubleArrayMapper
		extends TypedRealtimeValueFragmentFactory<Double[], AppliedMatrixRealtimeDoubleArray> {

	public AppliedMatrixRealtimeDoubleArrayMapper() {
		super(AppliedMatrixRealtimeDoubleArray.class);
	}

	@Override
	protected RealtimeValueFragment<Double[]> createFragment(AppliedMatrixRealtimeDoubleArray value)
			throws MappingException, RpiException {
		MatrixApply solve = new MatrixApply(value.getSize());
		RealtimeDoubleArrayFragment ret = new RealtimeDoubleArrayFragment(value, solve.getOutValue());
		ret.addDependency(value.getMatrix(), "inMatrix", solve.getInMatrix());
		ret.addDependency(value.getVector(), "inValue", solve.getInVector());
		return ret;
	}

}
