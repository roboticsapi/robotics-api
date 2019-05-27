/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.CosineDoubleSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleCos;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class CosineDoubleSensorMapper implements SensorMapper<SoftRobotRuntime, Double, CosineDoubleSensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, CosineDoubleSensor sensor, SensorMappingPorts ports,
			SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("CosineDoubleSensor");
		SensorMapperResult<Double> mappedOther = runtime.getMapperRegistry().mapSensor(runtime, sensor.getInnerSensor(),
				fragment, context);

		DoubleCos cos = fragment.add(new DoubleCos());

		DataflowInPort cosIn = fragment.addInPort(new DoubleDataflow(), true, cos.getInValue());

		fragment.connect(mappedOther.getSensorPort(), cosIn);

		return new DoubleSensorMapperResult(fragment,
				fragment.addOutPort(new DoubleDataflow(), true, cos.getOutValue()));
	}

}
