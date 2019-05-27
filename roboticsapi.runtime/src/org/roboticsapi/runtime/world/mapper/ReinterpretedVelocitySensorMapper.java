/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowThroughInPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.result.VelocitySensorMapperResult;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.ReinterpretedVelocitySensor;

public class ReinterpretedVelocitySensorMapper
		implements SensorMapper<AbstractMapperRuntime, Twist, ReinterpretedVelocitySensor> {

	@Override
	public SensorMapperResult<Twist> map(AbstractMapperRuntime runtime, ReinterpretedVelocitySensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment result = new NetFragment("ReinterpretedVelocitySensor");
		SensorMapperResult<Twist> innerResult = runtime.getMapperRegistry().mapSensor(runtime, sensor.getOtherSensor(),
				result, context);

		NetFragment interpret = new NetFragment("Output");
		DataflowThroughInPort in = new DataflowThroughInPort(new VelocityDataflow(
				sensor.getOtherSensor().getMovingFrame(), sensor.getOtherSensor().getReferenceFrame(),
				sensor.getOtherSensor().getPivotPoint(), sensor.getOtherSensor().getOrientation()));
		DataflowThroughOutPort out = new DataflowThroughOutPort(false, in, new VelocityDataflow(sensor.getMovingFrame(),
				sensor.getReferenceFrame(), sensor.getPivotPoint(), sensor.getOrientation()));
		interpret.addInPort(in);

		interpret.connect(innerResult.getSensorPort(), in);

		interpret.addOutPort(out);

		result.add(interpret);

		return new VelocitySensorMapperResult(result, out);
	}
}
