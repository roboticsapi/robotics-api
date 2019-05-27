/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.SquareRootDoubleSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleSquareRoot;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class SquareRootDoubleSensorMapper implements SensorMapper<SoftRobotRuntime, Double, SquareRootDoubleSensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, SquareRootDoubleSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("SquareRootDoubleSensor");
		SensorMapperResult<Double> mappedOther = runtime.getMapperRegistry().mapSensor(runtime, sensor.getInnerSensor(),
				fragment, context);

		DoubleSquareRoot sqrt = fragment.add(new DoubleSquareRoot());

		DataflowInPort sqrtIn = fragment.addInPort(new DoubleDataflow(), true, sqrt.getInValue());

		fragment.connect(mappedOther.getSensorPort(), sqrtIn);

		return new DoubleSensorMapperResult(fragment,
				fragment.addOutPort(new DoubleDataflow(), true, sqrt.getOutValue()));
	}

}
