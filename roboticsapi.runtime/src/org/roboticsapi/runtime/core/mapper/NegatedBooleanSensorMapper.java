/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.NegatedBooleanSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.BooleanNot;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BooleanSensorMapperResult;

public class NegatedBooleanSensorMapper implements SensorMapper<SoftRobotRuntime, Boolean, NegatedBooleanSensor> {

	@Override
	public SensorMapperResult<Boolean> map(SoftRobotRuntime runtime, NegatedBooleanSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("Negated Boolean");
		SensorMapperResult<Boolean> innerResult = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getOtherSensor(), result, context);
		NetFragment neg = new NetFragment("Negate");
		BooleanNot not = neg.add(new BooleanNot());
		neg.connect(innerResult.getSensorPort(),
				neg.addInPort(innerResult.getSensorPort().getType(), true, not.getInValue()));
		result.add(neg);
		DataflowOutPort resultPort = neg.addOutPort(new StateDataflow(), false, not.getOutValue());
		return new BooleanSensorMapperResult(result, resultPort);
	}
}
