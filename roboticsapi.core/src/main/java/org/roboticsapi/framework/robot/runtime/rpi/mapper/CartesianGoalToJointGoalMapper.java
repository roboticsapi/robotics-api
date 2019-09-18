/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.mapper;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.World;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.ActuatorDriverMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.NamedActuatorDriver;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianGoalActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointPositionGoalActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.MultiJointActionResult;
import org.roboticsapi.framework.robot.RobotArm;

public class CartesianGoalToJointGoalMapper<DD extends NamedActuatorDriver<? extends RobotArm>>
		implements ActuatorDriverMapper<DD, CartesianGoalActionResult> {

	@Override
	public RealtimeValueConsumerFragment map(DD actuatorDriver, CartesianGoalActionResult actionResult,
			DeviceParameterBag parameters, MapperRegistry registry, RealtimeBoolean cancel, RealtimeDouble override,
			RealtimeDouble time) throws MappingException, RpiException {
		FrameTopology topology = World.getCommandedTopology().forRuntime(actuatorDriver.getRuntime());

		try {
			RealtimeDouble[] jointGoal = actuatorDriver.getDevice().getInverseKinematicsSensor(
					actionResult.getGoal().getTransformationForRepresentation(
							actuatorDriver.getDevice().getReferenceFrame(), topology),
					actuatorDriver.getDevice().getJointSensors(), parameters);

			return registry.mapDriver(actuatorDriver, new MultiJointActionResult(null, null, Stream.of(jointGoal)
					.map(goal -> new JointPositionGoalActionResult(null, null, goal)).collect(Collectors.toList())),
					parameters, cancel, override, time);

		} catch (RoboticsException e) {
			throw new MappingException(e);
		}
	}
}
