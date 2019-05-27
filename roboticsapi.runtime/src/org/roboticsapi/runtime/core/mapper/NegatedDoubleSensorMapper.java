/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.NegatedDoubleSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class NegatedDoubleSensorMapper implements SensorMapper<SoftRobotRuntime, Double, NegatedDoubleSensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, NegatedDoubleSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("Minus<DoubleSensor>");

		SensorMapperResult<Double> mappedSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getInnerSensor(), fragment, context);

		NetFragment minus = new NetFragment("Minus");
		DoubleMultiply mult = new DoubleMultiply();
		mult.setFirst(-1.0);
		minus.add(mult);
		minus.connect(mappedSensor.getSensorPort(), minus.addInPort(new DoubleDataflow(), true, mult.getInSecond()));
		fragment.add(minus);
		DataflowOutPort result = minus.addOutPort(new DoubleDataflow(), false, mult.getOutValue());

		return new DoubleSensorMapperResult(fragment, result);
	}

}
