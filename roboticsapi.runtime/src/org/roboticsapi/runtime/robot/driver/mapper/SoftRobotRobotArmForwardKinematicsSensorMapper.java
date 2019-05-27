/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.driver.mapper;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.result.TransformationSensorMapperResult;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ComposedDataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.robot.driver.SoftRobotRobotArmForwardKinematicsSensor;
import org.roboticsapi.world.Transformation;

public class SoftRobotRobotArmForwardKinematicsSensorMapper
		implements SensorMapper<SoftRobotRuntime, Transformation, SoftRobotRobotArmForwardKinematicsSensor> {

	@Override
	public SensorMapperResult<Transformation> map(SoftRobotRuntime runtime,
			SoftRobotRobotArmForwardKinematicsSensor sensor, SensorMappingPorts ports, SensorMappingContext context)
			throws MappingException {

		NetFragment fragment = new NetFragment("SoftRobotRobotArmForwardKinematicsSensor");
		ComposedDataflowOutPort joints = new ComposedDataflowOutPort(true);
		for (int i = 0; i < sensor.getJointSensors().length; i++) {
			DoubleSensor joint = sensor.getJointSensors()[i];

			SensorMapperResult<Double> mappedJoint = runtime.getMapperRegistry().mapSensor(runtime, joint, fragment,
					context);
			joints.addDataflow(mappedJoint.getSensorPort());
		}

		DataflowOutPort jointsPort = fragment.reinterpret(joints,
				new JointsDataflow(sensor.getDriver().getJointCount(), sensor.getDriver()));

		RobotArmKinematicsFragment kinFragment = fragment.add(new RobotArmKinematicsFragment(sensor.getDriver()));
		fragment.connect(jointsPort, kinFragment.getInJoints());

		return new TransformationSensorMapperResult(fragment,
				fragment.reinterpret(kinFragment.getOutFrame(), new TransformationDataflow()));
	}
}
