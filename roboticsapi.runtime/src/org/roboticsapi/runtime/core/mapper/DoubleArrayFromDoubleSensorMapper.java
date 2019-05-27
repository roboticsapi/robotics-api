/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.sensor.DoubleArrayFromDoubleSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleArray;
import org.roboticsapi.runtime.core.primitives.DoubleArraySet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DoubleArrayDataflow;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleArraySensorMapperResult;
import org.roboticsapi.runtime.rpi.OutPort;

public class DoubleArrayFromDoubleSensorMapper
		implements SensorMapper<SoftRobotRuntime, Double[], DoubleArrayFromDoubleSensor> {

	@Override
	public SensorMapperResult<Double[]> map(SoftRobotRuntime runtime, DoubleArrayFromDoubleSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("DoubleFromDoubleArraySensor");
		DoubleArray doubleArray = fragment.add(new DoubleArray(sensor.getSize()));
		OutPort out = doubleArray.getOutArray();
		for (int i = 0; i < sensor.getSize(); i++) {
			SensorMapperResult<Double> mappedOther = runtime.getMapperRegistry().mapSensor(runtime, sensor.getSensor(i),
					fragment, context);
			DoubleArraySet set = fragment.add(new DoubleArraySet(sensor.getSize(), i));
			fragment.connect(mappedOther.getSensorPort(), set.getInValue(), new DoubleDataflow());
			fragment.connect(out, set.getInArray());
			out = set.getOutArray();
		}
		return new DoubleArraySensorMapperResult(fragment,
				fragment.addOutPort(new DoubleArrayDataflow(sensor.getSize()), true, out));
	}
}
