/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.VectorDataflow;
import org.roboticsapi.runtime.world.primitives.VectorFromXYZ;
import org.roboticsapi.runtime.world.result.VectorSensorMapperResult;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.VectorFromComponentsSensor;

public class VectorFromComponentsSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Vector, VectorFromComponentsSensor> {

	@Override
	public SensorMapperResult<Vector> map(AbstractMapperRuntime runtime, VectorFromComponentsSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment fragment = new NetFragment("VectorFromComponentsSensor");

		SensorMapperResult<Double> mappedX = runtime.getMapperRegistry().mapSensor(runtime, sensor.getXComponent(),
				fragment, context);

		SensorMapperResult<Double> mappedY = runtime.getMapperRegistry().mapSensor(runtime, sensor.getYComponent(),
				fragment, context);

		SensorMapperResult<Double> mappedZ = runtime.getMapperRegistry().mapSensor(runtime, sensor.getZComponent(),
				fragment, context);

		NetFragment combinationFragment = fragment.add(new NetFragment("Component combination"));

		VectorFromXYZ combiner = combinationFragment.add(new VectorFromXYZ());
		combinationFragment.connect(mappedX.getSensorPort(),
				combinationFragment.addInPort(new DoubleDataflow(), true, combiner.getInX()));
		combinationFragment.connect(mappedY.getSensorPort(),
				combinationFragment.addInPort(new DoubleDataflow(), true, combiner.getInY()));
		combinationFragment.connect(mappedZ.getSensorPort(),
				combinationFragment.addInPort(new DoubleDataflow(), true, combiner.getInZ()));

		DataflowOutPort sensorPort = fragment.addOutPort(new VectorDataflow(), true, combiner.getOutValue());

		return new VectorSensorMapperResult(fragment, sensorPort);
	}

}
