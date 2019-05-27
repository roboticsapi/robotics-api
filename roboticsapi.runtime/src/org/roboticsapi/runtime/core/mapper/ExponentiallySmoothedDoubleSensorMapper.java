/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.ExponentiallySmootedDoubleSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.CycleTime;
import org.roboticsapi.runtime.core.primitives.DoubleAdd;
import org.roboticsapi.runtime.core.primitives.DoubleConditional;
import org.roboticsapi.runtime.core.primitives.DoubleDivide;
import org.roboticsapi.runtime.core.primitives.DoubleIsNull;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.core.primitives.DoublePower;
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
import org.roboticsapi.runtime.rpi.OutPort;

public class ExponentiallySmoothedDoubleSensorMapper
		implements SensorMapper<SoftRobotRuntime, Double, ExponentiallySmootedDoubleSensor> {

	@Override
	public SensorMapperResult<Double> map(SoftRobotRuntime runtime, ExponentiallySmootedDoubleSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment fragment = new NetFragment("ExponentiallySmootedDoubleSensor");

		SensorMapperResult<Double> mappedInnerSensor = runtime.getMapperRegistry().mapSensor(runtime,
				sensor.getOtherSensor(), fragment, context);

		// calculate alpha
		CycleTime cycleTime = fragment.add(new CycleTime());
		DoubleDivide cycleTimeDivHalfLife = fragment.add(new DoubleDivide(0d, -sensor.getHalfLife()));
		fragment.connect(cycleTime.getOutValue(), cycleTimeDivHalfLife.getInFirst());

		DoublePower power = fragment.add(new DoublePower(2d, 0d));
		fragment.connect(cycleTimeDivHalfLife.getOutValue(), power.getInSecond());

		DoubleMultiply negatePower = fragment.add(new DoubleMultiply(-1d, 0d));
		fragment.connect(power.getOutValue(), negatePower.getInSecond());

		DoubleAdd add = fragment.add(new DoubleAdd(1d, 0d));
		fragment.connect(negatePower.getOutValue(), add.getInSecond());

		OutPort alpha = add.getOutValue();
		OutPort oneMinusAlpha = power.getOutValue();

		// calculate exponential smoothing
		DoublePre pre = fragment.add(new DoublePre());

		DoubleIsNull nullChecker = fragment.add(new DoubleIsNull());
		fragment.connect(pre.getOutValue(), nullChecker.getInValue());

		DoubleConditional ifNull = fragment.add(new DoubleConditional());
		fragment.connect(nullChecker.getOutValue(), ifNull.getInCondition());
		fragment.connect(mappedInnerSensor.getSensorPort(), ifNull.getInTrue(), new DoubleDataflow());
		fragment.connect(pre.getOutValue(), ifNull.getInFalse());

		OutPort lastValue = ifNull.getOutValue();

		// smoothing
		DoubleMultiply firstTerm = fragment.add(new DoubleMultiply());
		fragment.connect(alpha, firstTerm.getInFirst());
		fragment.connect(mappedInnerSensor.getSensorPort(), firstTerm.getInSecond(), new DoubleDataflow());

		DoubleMultiply secondTerm = fragment.add(new DoubleMultiply());
		fragment.connect(oneMinusAlpha, secondTerm.getInFirst());
		fragment.connect(lastValue, secondTerm.getInSecond());

		DoubleAdd addTerms = fragment.add(new DoubleAdd());
		fragment.connect(firstTerm.getOutValue(), addTerms.getInFirst());
		fragment.connect(secondTerm.getOutValue(), addTerms.getInSecond());

		fragment.connect(addTerms.getOutValue(), pre.getInValue());

		DataflowOutPort sensorPort = fragment.addOutPort(new DoubleDataflow(), true, addTerms.getOutValue());

		return new DoubleSensorMapperResult(fragment, sensorPort);
	}
}
