/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.mapper.action;

import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.platform.action.DriveDirection;
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.primitives.TwistFromVelocities;
import org.roboticsapi.runtime.world.primitives.VectorFromXYZ;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianVelocityActionResult;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public class DriveDirectionMapper implements ActionMapper<SoftRobotRuntime, DriveDirection> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, DriveDirection action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {
		if (parameters.get(MotionCenterParameter.class) == null) {
			throw new MappingException("No motion center given.");
		}

		NetFragment fragment = new NetFragment("DriveDirection");

		SensorMappingContext context = new SensorMappingContext();
		SensorMapperResult<Double> xSensor = runtime.getMapperRegistry().mapSensor(runtime, action.getxVel(), fragment,
				context);
		SensorMapperResult<Double> ySensor = runtime.getMapperRegistry().mapSensor(runtime, action.getyVel(), fragment,
				context);
		SensorMapperResult<Double> aSensor = runtime.getMapperRegistry().mapSensor(runtime, action.getaVel(), fragment,
				context);

		NetFragment blocks = fragment.add(new NetFragment("Target velocity"));
		TwistFromVelocities twist = blocks.add(new TwistFromVelocities());
		VectorFromXYZ vel = blocks.add(new VectorFromXYZ(0d, 0d, 0d));
		VectorFromXYZ rot = blocks.add(new VectorFromXYZ(0d, 0d, 0d));
		twist.getInRotVel().connectTo(rot.getOutValue());
		twist.getInTransVel().connectTo(vel.getOutValue());

		blocks.connect(xSensor.getSensorPort(), blocks.addInPort(new DoubleDataflow(), false, vel.getInX()));
		blocks.connect(ySensor.getSensorPort(), blocks.addInPort(new DoubleDataflow(), false, vel.getInY()));
		blocks.connect(aSensor.getSensorPort(), blocks.addInPort(new DoubleDataflow(), false, rot.getInZ()));

		DataflowOutPort directionPort = blocks
				.addOutPort(new VelocityDataflow(parameters.get(MotionCenterParameter.class).getMotionCenter(),
						action.getReference(), parameters.get(MotionCenterParameter.class).getMotionCenter().getPoint(),
						action.getOrientation()), false, twist.getOutValue());

		CartesianVelocityActionResult result = new CartesianVelocityActionResult(directionPort);
		return new DriveVelocityMapperResult(action, fragment, result, ports.cancelPort);
	}
}
