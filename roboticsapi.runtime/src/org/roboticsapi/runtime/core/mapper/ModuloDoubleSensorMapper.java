/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.ModuloDoubleSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleModulo;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class ModuloDoubleSensorMapper implements SensorMapper<SoftRobotRuntime, Double, ModuloDoubleSensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, ModuloDoubleSensor sensor, SensorMappingPorts ports,
			SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("Modulo<DoubleSensor>");
		SensorMapperResult<Double> dividend = runtime.getMapperRegistry().mapSensor(runtime, sensor.getDividend(),
				result, context);
		SensorMapperResult<Double> divisor = runtime.getMapperRegistry().mapSensor(runtime, sensor.getDivisor(), result,
				context);

		NetFragment val = new NetFragment("Modulo");
		DoubleModulo div = val.add(new DoubleModulo());

		val.connect(dividend.getSensorPort(), div.getInFirst(), new DoubleDataflow());
		val.connect(divisor.getSensorPort(), div.getInSecond(), new DoubleDataflow());

		result.add(val);
		DataflowOutPort resultPort = val.addOutPort(new DoubleDataflow(), false, div.getOutValue());
		return new DoubleSensorMapperResult(result, resultPort);
	}

}
