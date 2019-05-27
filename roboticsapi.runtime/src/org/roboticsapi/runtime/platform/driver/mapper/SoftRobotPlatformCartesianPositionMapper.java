/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.driver.mapper;

import org.roboticsapi.cartesianmotion.FrameProjector;
import org.roboticsapi.cartesianmotion.parameter.FrameProjectorParameter;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.actuator.ActuatorNotOperationalException;
import org.roboticsapi.core.actuator.ConcurrentAccessException;
import org.roboticsapi.platform.IllegalGoalException;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianPositionActionResult;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowThroughOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BaseActuatorDriverMapperResult;
import org.roboticsapi.runtime.platform.driver.SoftRobotPlatformCartesianPositionDriver;
import org.roboticsapi.runtime.platform.primitives.CartesianPosition;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.WorldLinkBuilder;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.sensor.RelationSensor;

public class SoftRobotPlatformCartesianPositionMapper implements
		ActuatorDriverMapper<SoftRobotRuntime, SoftRobotPlatformCartesianPositionDriver, CartesianPositionActionResult> {

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime,
			final SoftRobotPlatformCartesianPositionDriver device, CartesianPositionActionResult actionResult,
			DeviceParameterBag parameters, DeviceMappingPorts ports) throws MappingException, RPIException {

		final NetFragment fragment = new NetFragment("Platform CartesianPosition");
		fragment.addLinkBuilder(new WorldLinkBuilder(runtime));

		// apply projector
		FrameProjectorParameter proj = parameters.get(FrameProjectorParameter.class);

		DataflowThroughOutPort position = fragment.addThroughPort(true,
				new RelationDataflow(device.getReferenceFrame(), device.getMovingFrame()));

		if (proj != null && proj.getProjector() != null) {
			try {
				DataflowThroughOutPort toProject = fragment.addThroughPort(true,
						new RelationDataflow(device.getReferenceFrame(), device.getMovingFrame()));
				FrameProjector projector = proj.getProjector();
				RelationSensor toProjectSensor = RelationDataflow.createRelationSensor(toProject, device.getRuntime());
				RelationSensor projected = projector.project(toProjectSensor, parameters);
				SensorMapperResult<Transformation> mappedSensor = runtime.getMapperRegistry()
						.mapSensor(device.getRuntime(), projected, fragment, new SensorMappingContext());
				fragment.connect(actionResult.getOutPort(), toProject.getInPort());
				fragment.connect(mappedSensor.getSensorPort(), position.getInPort());
			} catch (TransformationException e) {
				throw new MappingException(e);
			}
		} else {
			fragment.connect(actionResult.getOutPort(), position.getInPort());
		}

		CartesianPosition pos = fragment.add(new CartesianPosition(device.getDeviceName()));
		fragment.connect(position, pos.getInPosition(),
				new RelationDataflow(device.getReferenceFrame(), device.getMovingFrame()));

		fragment.addLinkBuilder(new WorldLinkBuilder(runtime));

		BaseActuatorDriverMapperResult ret = new BaseActuatorDriverMapperResult(device, fragment, true);
		ret.addActuatorExceptionPort(ConcurrentAccessException.class,
				fragment.addOutPort(new StateDataflow(), false, pos.getOutErrorConcurrentAccess()));
		ret.addActuatorExceptionPort(ActuatorNotOperationalException.class,
				fragment.addOutPort(new StateDataflow(), false, pos.getOutErrorDeviceFailed()));
		ret.addActuatorExceptionPort(IllegalGoalException.class,
				fragment.addOutPort(new StateDataflow(), false, pos.getOutErrorIllegalPosition()));

		return ret;
	}
}
