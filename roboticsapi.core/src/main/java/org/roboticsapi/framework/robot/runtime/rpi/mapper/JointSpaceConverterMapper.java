/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.mapper;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.action.Plan;
import org.roboticsapi.core.action.PlannedAction;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.World;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianGoalActionResult;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianPositionActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointPositionActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointPositionGoalActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.MultiJointActionResult;
import org.roboticsapi.framework.robot.RobotArm;
import org.roboticsapi.framework.robot.action.JointSpaceConverter;

@SuppressWarnings("rawtypes")
public class JointSpaceConverterMapper implements ActionMapper<JointSpaceConverter> {

	@Override
	public ActionResult map(final JointSpaceConverter action, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time, Map<PlannedAction<?>, Plan> plans)
			throws MappingException, RpiException {

		if (action.getDevice() instanceof RobotArm) {
			RobotArm robot = (RobotArm) action.getDevice();
			ActionResult innerResult = registry.mapAction(action.getInnerAction(), parameters, cancel, override, time,
					plans);
			RealtimeDouble[] joints;

			try {
				joints = robot.getInverseKinematicsSensor(((CartesianPositionActionResult) innerResult).getPosition()
						.getTransformationForRepresentation(robot.getBase(), World.getCommandedTopology()), parameters);
			} catch (RoboticsException e) {
				throw new MappingException(e);
			}
			if (innerResult instanceof CartesianPositionActionResult) {
				MultiJointActionResult ret = new MultiJointActionResult(action, RealtimeBoolean.TRUE, Stream.of(joints)
						.map(pos -> new JointPositionActionResult(null, null, pos)).collect(Collectors.toList()));
				ret.addRealtimeValueSource(innerResult);
				return ret;
			} else if (innerResult instanceof CartesianGoalActionResult) {
				MultiJointActionResult ret = new MultiJointActionResult(action, RealtimeBoolean.TRUE, Stream.of(joints)
						.map(pos -> new JointPositionGoalActionResult(null, null, pos)).collect(Collectors.toList()));
				ret.addRealtimeValueSource(innerResult);
				return ret;
			}
		}
		return null;
	}
}
