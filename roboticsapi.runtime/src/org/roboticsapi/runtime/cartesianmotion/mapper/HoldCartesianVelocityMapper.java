/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper;

import org.roboticsapi.cartesianmotion.action.HoldCartesianVelocity;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.PlannedActionMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.primitives.TwistFromVelocities;
import org.roboticsapi.runtime.world.primitives.TwistToVelocities;
import org.roboticsapi.runtime.world.primitives.VectorScale;
import org.roboticsapi.world.Twist;

public class HoldCartesianVelocityMapper implements ActionMapper<SoftRobotRuntime, HoldCartesianVelocity> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, HoldCartesianVelocity action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {

		NetFragment result = new NetFragment("CartesianJogging Action");
		// map sensor
		SensorMapperResult<Twist> twist = runtime.getMapperRegistry().mapSensor(runtime, action.getVelocity(), result,
				null);

		// find out override
		DataflowOutPort overridePort = ports.overridePort;

		// scale sensor value
		TwistToVelocities split = result.add(new TwistToVelocities());
		DataflowOutPort vel = twist.getSensorPort();
		result.connect(vel, split.getInValue(), vel.getType());
		VectorScale trans = result.add(new VectorScale());
		result.connect(split.getOutTransVel(), trans.getInValue());
		result.connect(overridePort, trans.getInFactor(), new DoubleDataflow());
		VectorScale rot = result.add(new VectorScale());
		result.connect(split.getOutRotVel(), rot.getInValue());
		result.connect(overridePort, rot.getInFactor(), new DoubleDataflow());
		TwistFromVelocities join = result.add(new TwistFromVelocities());
		result.connect(trans.getOutValue(), join.getInTransVel());
		result.connect(rot.getOutValue(), join.getInRotVel());

		// create result
		DataflowOutPort resultPort = result.addOutPort(vel.getType(), false, join.getOutValue());

		return new PlannedActionMapperResult(action, result, new CartesianPositionActionResult(resultPort),
				ports.cancelPort, null);
	}
}
