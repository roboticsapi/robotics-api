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
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianPositionActionResult;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ComposedDataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.WrappedActuatorDriverMapperResult;
import org.roboticsapi.runtime.multijoint.mapper.JointPositionActionResult;
import org.roboticsapi.runtime.robot.NullspaceJointsDataflow;
import org.roboticsapi.runtime.robot.driver.SoftRobotRobotArmDriver;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.sensor.RelationSensor;

public class SoftRobotRobotArmDriverCartesianPositionMapper
		implements ActuatorDriverMapper<SoftRobotRuntime, SoftRobotRobotArmDriver, CartesianPositionActionResult> {

	@Override
	public ActuatorDriverMapperResult map(final SoftRobotRuntime runtime, final SoftRobotRobotArmDriver driver,
			CartesianPositionActionResult actionResult, final DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {

		FrameProjectorParameter proj = parameters.get(FrameProjectorParameter.class);

		// use old code path to work around bug 61 without FrameProjector
		if (proj == null || proj.getProjector() == null) {

			return runtime.getMapperRegistry().mapActuatorDriver(runtime, driver,
					new JointPositionActionResult(actionResult.getOutPort()), parameters, ports.cancel, ports.override);
		}

		NetFragment ret = new NetFragment("SoftRobotRobotArmDriverCartesianPosition");

		// apply projector if feasible
		// FIXME: this code path is affected by bug 61
		DataflowThroughOutPort position = ret.addThroughPort(true,
				new RelationDataflow(driver.getBase(), driver.getFlange()));
		DataflowOutPort deviceInput = position;
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

		if (actionResult.getOutPort().getType()
				.providesValueFor(new NullspaceJointsDataflow(driver.getJointCount(), driver))) {
			DataflowThroughOutPort hint = ret.addThroughPort(false,
					new NullspaceJointsDataflow(driver.getJointCount(), driver));
			ret.connect(actionResult.getOutPort(), hint.getInPort());
			deviceInput = ret.addOutPort(new ComposedDataflowOutPort(true, position, hint));
		}

		// map joint result
		ActuatorDriverMapperResult mappedJointPositionResult = runtime.getMapperRegistry().mapActuatorDriver(runtime,
				driver, new JointPositionActionResult(deviceInput), parameters, ports.cancel, ports.override);
		ret.add(mappedJointPositionResult.getNetFragment());

		return new WrappedActuatorDriverMapperResult(driver, ret, mappedJointPositionResult);
	}
}
