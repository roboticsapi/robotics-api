/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.world.dataflow.TransformationArrayDataflow;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameArray;
import org.roboticsapi.runtime.world.primitives.FrameArraySet;
import org.roboticsapi.runtime.world.result.TransformationArraySensorMapperResult;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.sensor.MergedTransformationArraySensor;

public class MergedTransformationArraySensorMapper
		implements SensorMapper<AbstractMapperRuntime, Transformation[], MergedTransformationArraySensor> {

	@Override
	public SensorMapperResult<Transformation[]> map(AbstractMapperRuntime runtime,
			MergedTransformationArraySensor sensor, SensorMappingPorts ports, SensorMappingContext context)
			throws MappingException {
		NetFragment fragment = new NetFragment("MergedTransformationArraySensor");
		FrameArray array = fragment.add(new FrameArray(sensor.getSize()));
		OutPort out = array.getOutArray();

		for (int i = 0; i < sensor.getSize(); i++) {
			SensorMapperResult<Transformation> mappedOther = runtime.getMapperRegistry().mapSensor(runtime,
					sensor.getSensor(i), fragment, context);
			FrameArraySet set = fragment.add(new FrameArraySet(sensor.getSize(), i));
			fragment.connect(mappedOther.getSensorPort(), set.getInValue(), new TransformationDataflow());
			fragment.connect(out, set.getInArray());
			out = set.getOutArray();
		}
		return new TransformationArraySensorMapperResult(fragment,
				fragment.addOutPort(new TransformationArrayDataflow(sensor.getSize()), true, out));
	}
}
