/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.AddedDoubleSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleAdd;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class AddedDoubleSensorMapper implements SensorMapper<SoftRobotRuntime, Double, AddedDoubleSensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, AddedDoubleSensor sensor, SensorMappingPorts ports,
			SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("Add<DoubleSensor>");
		SensorMapperResult<Double> left = runtime.getMapperRegistry().mapSensor(runtime, sensor.getSensor1(), result,
				context);
		SensorMapperResult<Double> right = runtime.getMapperRegistry().mapSensor(runtime, sensor.getSensor2(), result,
				context);

		NetFragment val = new NetFragment("Add");
		DoubleAdd add = val.add(new DoubleAdd());

		val.connect(left.getSensorPort(), val.addInPort(left.getSensorPort().getType(), true, add.getInFirst()));
		val.connect(right.getSensorPort(), val.addInPort(right.getSensorPort().getType(), true, add.getInSecond()));

		result.add(val);
		DataflowOutPort resultPort = val.addOutPort(new DoubleDataflow(), false, add.getOutValue());
		return new DoubleSensorMapperResult(result, resultPort);
	}

}
