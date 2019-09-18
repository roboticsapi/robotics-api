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
import org.roboticsapi.core.world.Pose;
import org.roboticsapi.core.world.realtimevalue.RealtimePoseCoincidence;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionMapper;
import org.roboticsapi.facet.runtime.rpi.mapping.ActionResult;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.framework.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianPositionActionResult;
import org.roboticsapi.framework.multijoint.action.PTPFromMotion;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.JointPositionActionResult;
import org.roboticsapi.framework.multijoint.runtime.rpi.result.MultiJointActionResult;
import org.roboticsapi.framework.robot.action.NullspacePTPFromMotion;

public class NullspacePTPFromMotionMapper implements ActionMapper<NullspacePTPFromMotion> {

	@Override
	public ActionResult map(final NullspacePTPFromMotion action, DeviceParameterBag parameters, MapperRegistry registry,
			RealtimeBoolean cancel, RealtimeDouble override, RealtimeDouble time, Map<PlannedAction<?>, Plan> plans)
			throws MappingException, RpiException {

		ActionResult path = registry.mapAction(action.getPath(), parameters, cancel, override, time, plans);
		Plan pathPlan = plans.get(action.getPath());
		if (pathPlan == null) {
			throw new MappingException("Could not get plan for inner path motion.");
		}
		double duration = pathPlan.getTotalTime();

		PTPFromMotion ptp = new PTPFromMotion(action.getFrom(), action.getTo(), action.getRealStart(),
				action.getRealStartVel());
		ActionResult joints = registry.mapAction(ptp, parameters, cancel, override, time, plans);
		Plan firstPlan = plans.get(ptp);

		ptp = new PTPFromMotion(firstPlan.getTotalTime() / duration, action.getFrom(), action.getTo(),
				action.getRealStart(), action.getRealStartVel());
		joints = registry.mapAction(ptp, parameters, cancel, override, time, plans);

		Pose mc = parameters.get(MotionCenterParameter.class).getMotionCenter();
		try {
			RealtimeTransformation transformation = new RealtimePoseCoincidence(
					((CartesianPositionActionResult) path).getPosition(), mc.asRealtimeValue()).getTransformation(
							action.getRobotArm().getBase().asRealtimePose(),
							action.getRobotArm().getFlange().asRealtimePose());
			RealtimeDouble[] pos = Stream.of(((MultiJointActionResult) joints).getJointResults())
					.map(jr -> ((JointPositionActionResult) jr).getPosition())
					.toArray(size -> new RealtimeDouble[size]);
			MultiJointActionResult ret = new MultiJointActionResult(action, RealtimeBoolean.TRUE,
					Stream.of(action.getRobotArm().getInverseKinematicsSensor(transformation, pos, parameters))
							.map(jp -> new JointPositionActionResult(null, null, jp)).collect(Collectors.toList()));
			ret.addRealtimeValueSource(path);
			ret.addRealtimeValueSource(joints);
			return ret;
		} catch (RoboticsException e) {
			throw new MappingException(e);
		}
	}
}
