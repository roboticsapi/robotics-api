/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.matrix.extension;

import org.roboticsapi.facet.runtime.rpi.extension.RpiExtension;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.matrix.mapper.AppliedMatrixRealtimeDoubleArrayMapper;
import org.roboticsapi.facet.runtime.rpi.matrix.mapper.ConstantRealtimeMatrixMapper;
import org.roboticsapi.facet.runtime.rpi.matrix.mapper.GetFromMatrixDoubleSensorMapper;
import org.roboticsapi.facet.runtime.rpi.matrix.mapper.GetFromMatrixRealtimeDoubleArrayMapper;
import org.roboticsapi.facet.runtime.rpi.matrix.mapper.MultipliedRealtimeMatrixMapper;
import org.roboticsapi.facet.runtime.rpi.matrix.mapper.ScaledRealtimeMatrixMapper;
import org.roboticsapi.facet.runtime.rpi.matrix.mapper.SelectionRealtimeMatrixMapper;
import org.roboticsapi.facet.runtime.rpi.matrix.mapper.SetArrayRealtimeMatrixMapper;
import org.roboticsapi.facet.runtime.rpi.matrix.mapper.SetBlockRealtimeMatrixMapper;
import org.roboticsapi.facet.runtime.rpi.matrix.mapper.SetValueRealtimeMatrixMapper;
import org.roboticsapi.facet.runtime.rpi.matrix.mapper.SolvedMatrixRealtimeDoubleArrayMapper;
import org.roboticsapi.facet.runtime.rpi.matrix.mapper.TransposedRealtimeMatrixMapper;

public class MatrixExtension extends RpiExtension {

	@Override
	protected void registerMappers(MapperRegistry mr) {
		mr.registerRealtimeValueMapper(new AppliedMatrixRealtimeDoubleArrayMapper());
		mr.registerRealtimeValueMapper(new ConstantRealtimeMatrixMapper());
		mr.registerRealtimeValueMapper(new GetFromMatrixRealtimeDoubleArrayMapper());
		mr.registerRealtimeValueMapper(new GetFromMatrixDoubleSensorMapper());
		mr.registerRealtimeValueMapper(new MultipliedRealtimeMatrixMapper());
		mr.registerRealtimeValueMapper(new ScaledRealtimeMatrixMapper());
		mr.registerRealtimeValueMapper(new SelectionRealtimeMatrixMapper());
		mr.registerRealtimeValueMapper(new SetArrayRealtimeMatrixMapper());
		mr.registerRealtimeValueMapper(new SetBlockRealtimeMatrixMapper());
		mr.registerRealtimeValueMapper(new SetValueRealtimeMatrixMapper());
		mr.registerRealtimeValueMapper(new SolvedMatrixRealtimeDoubleArrayMapper());
		mr.registerRealtimeValueMapper(new TransposedRealtimeMatrixMapper());
	}

	@Override
	protected void unregisterMappers(MapperRegistry mr) {
		// TODO Auto-generated method stub

	}

}
