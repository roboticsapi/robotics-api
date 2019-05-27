/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.Atan2DoubleSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleAtan2;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class Atan2DoubleSensorMapper
		implements SensorMapper<org.roboticsapi.runtime.SoftRobotRuntime, Double, Atan2DoubleSensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, Atan2DoubleSensor sensor, SensorMappingPorts ports,
			SensorMappingContext context) throws MappingException {
		NetFragment result = new NetFragment("Atan2DoubleSensor");

		SensorMapperResult<Double> mappedY = runtime.getMapperRegistry().mapSensor(runtime, sensor.getY(), result,
				context);

		SensorMapperResult<Double> mappedX = runtime.getMapperRegistry().mapSensor(runtime, sensor.getX(), result,
				context);

		DoubleAtan2 atan2 = result.add(new DoubleAtan2());

		DataflowInPort inY = result.addInPort(new DoubleDataflow(), true, atan2.getInY());
		DataflowInPort inX = result.addInPort(new DoubleDataflow(), true, atan2.getInX());

		result.connect(mappedY.getSensorPort(), inY);
		result.connect(mappedX.getSensorPort(), inX);

		return new DoubleSensorMapperResult(result, result.addOutPort(new DoubleDataflow(), true, atan2.getOutValue()));
	}

}
