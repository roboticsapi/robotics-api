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
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.VectorDataflow;
import org.roboticsapi.runtime.world.primitives.VectorFromXYZ;
import org.roboticsapi.runtime.world.result.VectorSensorMapperResult;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.ConstantVectorSensor;

public class ConstantVectorSensorMapper implements SensorMapper<AbstractMapperRuntime, Vector, ConstantVectorSensor> {

	@Override
	public SensorMapperResult<Vector> map(AbstractMapperRuntime runtime, ConstantVectorSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("ConstantVectorSensor");

		Vector value = sensor.getConstantValue();
		VectorFromXYZ vector = fragment.add(new VectorFromXYZ(value.getX(), value.getY(), value.getZ()));
		DataflowOutPort outValue = fragment.addOutPort(new VectorDataflow(), true, vector.getOutValue());

		return new VectorSensorMapperResult(fragment, outValue);
	}

}
