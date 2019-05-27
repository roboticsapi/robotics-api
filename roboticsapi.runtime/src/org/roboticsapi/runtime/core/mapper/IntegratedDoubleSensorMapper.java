/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.IntegratedDoubleSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.CycleTime;
import org.roboticsapi.runtime.core.primitives.DoubleAdd;
import org.roboticsapi.runtime.core.primitives.DoubleConditional;
import org.roboticsapi.runtime.core.primitives.DoubleIsNull;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.core.primitives.DoublePre;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class IntegratedDoubleSensorMapper implements SensorMapper<SoftRobotRuntime, Double, IntegratedDoubleSensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, IntegratedDoubleSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("Integrate<DoubleSensor>");
		SensorMapperResult<Double> initial = runtime.getMapperRegistry().mapSensor(runtime, sensor.getSensor1(), result,
				context);
		SensorMapperResult<Double> increment = runtime.getMapperRegistry().mapSensor(runtime, sensor.getSensor2(),
				result, context);

		NetFragment val = result.add(new NetFragment("Integrate"));

		DoublePre pre = val.add(new DoublePre());
		DoubleIsNull isNull = val.add(new DoubleIsNull());
		DoubleConditional conditional = val.add(new DoubleConditional());
		CycleTime cycleTime = val.add(new CycleTime());
		DoubleMultiply scale = val.add(new DoubleMultiply());
		DoubleAdd sum = val.add(new DoubleAdd());

		val.connect(sum.getOutValue(), pre.getInValue());
		val.connect(pre.getOutValue(), isNull.getInValue());
		val.connect(isNull.getOutValue(), conditional.getInCondition());
		val.connect(initial.getSensorPort(), conditional.getInTrue(), new DoubleDataflow());
		val.connect(pre.getOutValue(), conditional.getInFalse());
		val.connect(cycleTime.getOutValue(), scale.getInFirst());
		val.connect(increment.getSensorPort(), scale.getInSecond(), new DoubleDataflow());
		val.connect(scale.getOutValue(), sum.getInSecond());
		val.connect(conditional.getOutValue(), sum.getInFirst());

		DataflowOutPort resultPort = val.addOutPort(new DoubleDataflow(), false, sum.getOutValue());
		return new DoubleSensorMapperResult(result, resultPort);
	}

}
