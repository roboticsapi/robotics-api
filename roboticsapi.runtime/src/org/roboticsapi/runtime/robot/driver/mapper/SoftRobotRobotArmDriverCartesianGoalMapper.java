/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.driver.mapper;

import org.roboticsapi.cartesianmotion.FrameProjector;
import org.roboticsapi.cartesianmotion.parameter.FrameProjectorParameter;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianGoalActionResult;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.WrappedActuatorDriverMapperResult;
import org.roboticsapi.runtime.multijoint.mapper.JointGoalActionResult;
import org.roboticsapi.runtime.robot.driver.SoftRobotRobotArmDriver;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.sensor.RelationSensor;

public class SoftRobotRobotArmDriverCartesianGoalMapper
		implements ActuatorDriverMapper<SoftRobotRuntime, SoftRobotRobotArmDriver, CartesianGoalActionResult> {

	@Override
	public ActuatorDriverMapperResult map(final SoftRobotRuntime runtime, final SoftRobotRobotArmDriver driver,
			CartesianGoalActionResult actionResult, final DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {

		NetFragment ret = new NetFragment("SoftRobotRobotArmDriverCartesianGoal");

		// apply projector
		FrameProjectorParameter proj = parameters.get(FrameProjectorParameter.class);
		DataflowThroughOutPort position = ret.addThroughPort(true, actionResult.getOutPort().getType());
		if (proj != null && proj.getProjector() != null) {
			try {
				DataflowThroughOutPort toProject = ret.addThroughPort(true,
						new RelationDataflow(driver.getBase(), driver.getFlange()));
				FrameProjector projector = proj.getProjector();
				RelationSensor toProjectSensor = RelationDataflow.createRelationSensor(toProject, driver.getRuntime());
				RelationSensor projected = projector.project(toProjectSensor, parameters);
				SensorMapperResult<Transformation> mappedSensor = driver.getRuntime().getMapperRegistry()
						.mapSensor(driver.getRuntime(), projected, ret, new SensorMappingContext());
				ret.connect(actionResult.getOutPort(), toProject.getInPort());
				ret.connect(mappedSensor.getSensorPort(), position.getInPort());
			} catch (TransformationException e) {
				throw new MappingException(e);
			}
		} else {
			ret.connect(actionResult.getOutPort(), position.getInPort());
		}

		// map joint result
		ActuatorDriverMapperResult mappedJointPositionResult = runtime.getMapperRegistry().mapActuatorDriver(runtime,
				driver, new JointGoalActionResult(position), parameters, ports.cancel, ports.override);
		ret.add(mappedJointPositionResult.getNetFragment());

		return new WrappedActuatorDriverMapperResult(driver, ret, mappedJointPositionResult);
	}
}
