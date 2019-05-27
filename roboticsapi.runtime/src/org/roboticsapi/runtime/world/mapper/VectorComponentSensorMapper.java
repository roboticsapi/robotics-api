/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.world.dataflow.VectorDataflow;
import org.roboticsapi.runtime.world.primitives.VectorToXYZ;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.VectorComponentSensor;
import org.roboticsapi.world.sensor.VectorComponentSensor.VectorComponent;

public class VectorComponentSensorMapper implements SensorMapper<AbstractMapperRuntime, Double, VectorComponentSensor> {

	@Override
	public SensorMapperResult<Double> map(AbstractMapperRuntime runtime, VectorComponentSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment fragment = new NetFragment("VectorComponentSensor");
		SensorMapperResult<Vector> mapSensor = runtime.getMapperRegistry().mapSensor(runtime, sensor.getSensor(),
				fragment, context);

		NetFragment snf = new NetFragment("Componentization");
		fragment.add(snf);

		DataflowOutPort componentOut;

		VectorComponent c = sensor.getComponent();

		VectorToXYZ pos = snf.add(new VectorToXYZ());

		DataflowInPort splitterIn = snf.addInPort(new VectorDataflow(), true, pos.getInValue());

		fragment.connect(mapSensor.getSensorPort(), splitterIn);

		OutPort out = c == VectorComponent.X ? pos.getOutX() : c == VectorComponent.Y ? pos.getOutY() : pos.getOutZ();

		componentOut = snf.addOutPort(new DoubleDataflow(), true, out);

		return new DoubleSensorMapperResult(fragment, componentOut);
	}

}
