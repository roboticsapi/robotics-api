/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.matrix.mapper;

import org.roboticsapi.core.world.realtimevalue.realtimematrix.GetFromMatrixRealtimeDoubleArray;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleArrayFragment;
import org.roboticsapi.facet.runtime.rpi.matrix.primitives.MatrixArrayGet;

public class GetFromMatrixRealtimeDoubleArrayMapper
		extends TypedRealtimeValueFragmentFactory<Double[], GetFromMatrixRealtimeDoubleArray> {

	public GetFromMatrixRealtimeDoubleArrayMapper() {
		super(GetFromMatrixRealtimeDoubleArray.class);
	}

	@Override
	protected RealtimeValueFragment<Double[]> createFragment(GetFromMatrixRealtimeDoubleArray value)
			throws MappingException, RpiException {
		MatrixArrayGet matrixGet = new MatrixArrayGet(value.getRowIndex(), value.getColumnIndex(),
				value.getBlockWidth(), value.getLength());
		RealtimeValueFragment<Double[]> ret = new RealtimeDoubleArrayFragment(value, matrixGet.getOutArray());
		ret.addDependency(value.getMatrix(), "inMatrix", matrixGet.getInMatrix());
		return ret;
	}
}
