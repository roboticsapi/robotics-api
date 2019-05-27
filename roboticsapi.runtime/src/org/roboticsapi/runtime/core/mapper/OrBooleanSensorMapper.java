/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.OrBooleanSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.BooleanOr;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BooleanSensorMapperResult;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class OrBooleanSensorMapper implements SensorMapper<SoftRobotRuntime, Boolean, OrBooleanSensor> {

	@Override
	public SensorMapperResult<Boolean> map(SoftRobotRuntime runtime, OrBooleanSensor sensor, SensorMappingPorts ports,
			SensorMappingContext context) throws MappingException {

		// List<SensorMapperResult<Boolean>> innerResults = new
		// Vector<SensorMapperResult<Boolean>>();

		NetFragment result = new NetFragment("Or on BooleanSensors");

		NetFragment neg = new NetFragment("Or concatenation");

		// neg.addInPort(innerResult.getSensorPort().getType(), true,
		// not.getInValue()).connectTo(innerResult.getSensorPort());

		boolean first = true;

		InPort currentIn = null;
		OutPort currentOut = null;

		for (BooleanSensor inner : sensor.getInnerSensors()) {
			SensorMapperResult<Boolean> mappedSensor = runtime.getMapperRegistry().mapSensor(runtime, inner, result,
					context);

			BooleanOr or = new BooleanOr(false, false);
			neg.add(or);

			if (first) {
				first = false;

				neg.connect(mappedSensor.getSensorPort(),
						neg.addInPort(mappedSensor.getSensorPort().getType(), true, or.getInFirst()));

				currentIn = or.getInSecond();
				currentOut = or.getOutValue();
			}

			else {
				neg.connect(mappedSensor.getSensorPort(),
						neg.addInPort(mappedSensor.getSensorPort().getType(), true, currentIn));

				try {
					or.getInFirst().connectTo(currentOut);
				} catch (RPIException e) {
					throw new MappingException(e);
				}
				currentIn = or.getInSecond();
				currentOut = or.getOutValue();

			}
		}

		result.add(neg);

		DataflowOutPort resultPort = neg.addOutPort(new StateDataflow(), false, currentOut);
		return new BooleanSensorMapperResult(result, resultPort);
	}

}
