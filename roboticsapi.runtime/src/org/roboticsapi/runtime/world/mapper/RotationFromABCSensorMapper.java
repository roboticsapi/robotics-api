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
import org.roboticsapi.runtime.world.dataflow.RotationDataflow;
import org.roboticsapi.runtime.world.primitives.RotationFromABC;
import org.roboticsapi.runtime.world.result.RotationSensorMapperResult;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.sensor.RotationFromABCSensor;

public class RotationFromABCSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Rotation, RotationFromABCSensor> {

	@Override
	public SensorMapperResult<Rotation> map(AbstractMapperRuntime runtime, RotationFromABCSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("RotationFromABCSensor");
		SensorMapperResult<Double> mappedA = runtime.getMapperRegistry().mapSensor(runtime, sensor.getAComponent(),
				fragment, context);

		SensorMapperResult<Double> mappedB = runtime.getMapperRegistry().mapSensor(runtime, sensor.getBComponent(),
				fragment, context);

		SensorMapperResult<Double> mappedC = runtime.getMapperRegistry().mapSensor(runtime, sensor.getCComponent(),
				fragment, context);

		NetFragment combinationFragment = fragment.add(new NetFragment("Component fusion"));

		RotationFromABC combiner = combinationFragment.add(new RotationFromABC());
		combinationFragment.connect(mappedA.getSensorPort(),
				combinationFragment.addInPort(new DoubleDataflow(), true, combiner.getInA()));
		combinationFragment.connect(mappedB.getSensorPort(),
				combinationFragment.addInPort(new DoubleDataflow(), true, combiner.getInB()));
		combinationFragment.connect(mappedC.getSensorPort(),
				combinationFragment.addInPort(new DoubleDataflow(), true, combiner.getInC()));

		DataflowOutPort sensorPort = fragment.addOutPort(new RotationDataflow(), true, combiner.getOutValue());

		return new RotationSensorMapperResult(fragment, sensorPort);
	}

}
