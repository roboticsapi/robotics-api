/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.primitives.TwistFromVelocities;
import org.roboticsapi.runtime.world.result.VelocitySensorMapperResult;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.ConstantVelocitySensor;

public class ConstantVelocitySensorMapper
		implements SensorMapper<AbstractMapperRuntime, Twist, ConstantVelocitySensor> {

	@Override
	public SensorMapperResult<Twist> map(AbstractMapperRuntime runtime, ConstantVelocitySensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment fragment = new NetFragment("ConstantVelocitySensor");

		Twist c = sensor.getConstantValue();
		TwistFromVelocities twist = fragment.add(new TwistFromVelocities(c.getTransVel().getX(), c.getTransVel().getY(),
				c.getTransVel().getZ(), c.getRotVel().getX(), c.getRotVel().getY(), c.getRotVel().getZ()));

		DataflowOutPort twistOut = fragment.addOutPort(new VelocityDataflow(sensor.getMovingFrame(),
				sensor.getReferenceFrame(), sensor.getPivotPoint(), sensor.getOrientation()), true,
				twist.getOutValue());
		return new VelocitySensorMapperResult(fragment, twistOut);
	}

}
