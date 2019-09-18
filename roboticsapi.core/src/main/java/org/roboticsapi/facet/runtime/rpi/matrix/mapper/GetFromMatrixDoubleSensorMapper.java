/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.matrix.mapper;

import org.roboticsapi.core.world.realtimevalue.realtimematrix.GetFromMatrixRealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.facet.runtime.rpi.matrix.primitives.MatrixGet;

public class GetFromMatrixDoubleSensorMapper
		extends TypedRealtimeValueFragmentFactory<Double, GetFromMatrixRealtimeDouble> {

	public GetFromMatrixDoubleSensorMapper() {
		super(GetFromMatrixRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(GetFromMatrixRealtimeDouble value)
			throws MappingException, RpiException {

		MatrixGet matrixGet = new MatrixGet(value.getRowIndex(), value.getColumnIndex());
		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value, matrixGet.getOutValue());
		ret.addDependency(value.getMatrix(), "inMatrix", matrixGet.getInMatrix());
		return ret;
	}
}
