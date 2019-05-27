/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.RotationDataflow;
import org.roboticsapi.runtime.world.dataflow.VectorDataflow;
import org.roboticsapi.runtime.world.primitives.RotationToAxisAngle;
import org.roboticsapi.runtime.world.result.VectorSensorMapperResult;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.AxisFromRotationSensor;

public class AxisFromRotationSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Vector, AxisFromRotationSensor> {

	@Override
	public SensorMapperResult<Vector> map(AbstractMapperRuntime runtime, AxisFromRotationSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("AxisFromRotationSensor");

		SensorMapperResult<Rotation> mappedRotationSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getRotationSensor(), result, context);

		RotationToAxisAngle converter = result.add(new RotationToAxisAngle());

		result.connect(mappedRotationSensor.getSensorPort(), converter.getInValue(), new RotationDataflow());

		return new VectorSensorMapperResult(result,
				result.addOutPort(new VectorDataflow(), true, converter.getOutAxis()));

	}
}
