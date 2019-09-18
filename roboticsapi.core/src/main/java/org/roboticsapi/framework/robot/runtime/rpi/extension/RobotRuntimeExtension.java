/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.extension;

import org.roboticsapi.facet.runtime.rpi.extension.RpiExtension;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianGoalActionResult;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianGoalToCartesianVelocityMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianPositionActionResult;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianVelocityActionResult;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianVelocityGoalActionResult;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianVelocityGoalToCartesianVelocityMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianVelocityToCartesianPositionMapper;
import org.roboticsapi.framework.robot.action.JointSpaceConverter;
import org.roboticsapi.framework.robot.action.NullspacePTP;
import org.roboticsapi.framework.robot.action.NullspacePTPFromMotion;
import org.roboticsapi.framework.robot.action.SwitchToolAction;
import org.roboticsapi.framework.robot.runtime.rpi.driver.RobotArmGenericDriver;
import org.roboticsapi.framework.robot.runtime.rpi.mapper.CartesianGoalToJointGoalMapper;
import org.roboticsapi.framework.robot.runtime.rpi.mapper.CartesianPositionToJointPositionMapper;
import org.roboticsapi.framework.robot.runtime.rpi.mapper.JointSpaceConverterMapper;
import org.roboticsapi.framework.robot.runtime.rpi.mapper.NullspacePTPFromMotionMapper;
import org.roboticsapi.framework.robot.runtime.rpi.mapper.NullspacePTPMapper;
import org.roboticsapi.framework.robot.runtime.rpi.mapper.RobotArmForwardKinematicsSensorMapper;
import org.roboticsapi.framework.robot.runtime.rpi.mapper.RobotArmInverseKinematicsSensorMapper;
import org.roboticsapi.framework.robot.runtime.rpi.mapper.SwitchToolMapper;

public final class RobotRuntimeExtension extends RpiExtension {

	public RobotRuntimeExtension() {
		super(RobotArmGenericDriver.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void registerMappers(MapperRegistry mr) {
		mr.registerActuatorDriverMapper(RobotArmGenericDriver.class, CartesianGoalActionResult.class,
				new CartesianGoalToJointGoalMapper());
		mr.registerActuatorDriverMapper(RobotArmGenericDriver.class, CartesianPositionActionResult.class,
				new CartesianPositionToJointPositionMapper());
		mr.registerActuatorDriverMapper(RobotArmGenericDriver.class, CartesianGoalActionResult.class,
				new CartesianGoalToCartesianVelocityMapper(0.001, 0.001, 0.01, 0.01));
		mr.registerActuatorDriverMapper(RobotArmGenericDriver.class, CartesianVelocityGoalActionResult.class,
				new CartesianVelocityGoalToCartesianVelocityMapper());
		mr.registerActuatorDriverMapper(RobotArmGenericDriver.class, CartesianVelocityActionResult.class,
				new CartesianVelocityToCartesianPositionMapper());

		mr.registerActionMapper(NullspacePTP.class, new NullspacePTPMapper());
		mr.registerActionMapper(NullspacePTPFromMotion.class, new NullspacePTPFromMotionMapper());
		mr.registerActionMapper(JointSpaceConverter.class, new JointSpaceConverterMapper());
		mr.registerActionMapper(SwitchToolAction.class, new SwitchToolMapper());

		mr.registerRealtimeValueMapper(new RobotArmForwardKinematicsSensorMapper());
		mr.registerRealtimeValueMapper(new RobotArmInverseKinematicsSensorMapper());
	}

	@Override
	protected void unregisterMappers(MapperRegistry mr) {
		// TODO remove mappers???
	}
}
