/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.runtime.mapping.MapperRegistry;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.sensor.VelocitySensor;

public class CartesianGoalActionResult extends ActionResult {

	public CartesianGoalActionResult(DataflowOutPort outPort) {
		super(outPort);
	}

	public DataflowOutPort getGoalVelocity(NetFragment f, MapperRegistry r, RoboticsRuntime rr)
			throws MappingException {
		DataflowType type = getOutPort().getType();
		if (!(type instanceof RelationDataflow)) {
			return null;
		} else {
			RelationDataflow reltype = (RelationDataflow) type;
			VelocitySensor velSensor = reltype.getFrom().getVelocitySensorOf(reltype.getTo());

			SensorMapperResult<Twist> mappedSensor = r.mapSensor(rr, velSensor, f, new SensorMappingContext());

			return mappedSensor.getSensorPort();
		}
	}
}
