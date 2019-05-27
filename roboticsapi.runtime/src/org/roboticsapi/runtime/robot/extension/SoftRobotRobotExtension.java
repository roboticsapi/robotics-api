/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.robot.RobotArm;
import org.roboticsapi.robot.action.JointSpaceConverter;
import org.roboticsapi.robot.action.NullspacePTP;
import org.roboticsapi.robot.action.NullspacePTPFromMotion;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianGoalActionResult;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianGoalToCartesianVelocityMapper;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianPositionActionResult;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianVelocityActionResult;
import org.roboticsapi.runtime.extension.AbstractSoftRobotRoboticsBuilder;
import org.roboticsapi.runtime.mapping.MapperRegistry;
import org.roboticsapi.runtime.multijoint.mapper.JointGoalActionResult;
import org.roboticsapi.runtime.multijoint.mapper.JointPositionActionResult;
import org.roboticsapi.runtime.robot.driver.SoftRobotRobotArmDriver;
import org.roboticsapi.runtime.robot.driver.SoftRobotRobotArmForwardKinematicsSensor;
import org.roboticsapi.runtime.robot.driver.SoftRobotRobotArmInverseKinematicsSensor;
import org.roboticsapi.runtime.robot.driver.mapper.SoftRobotRobotArmDriverCartesianGoalMapper;
import org.roboticsapi.runtime.robot.driver.mapper.SoftRobotRobotArmDriverCartesianPositionMapper;
import org.roboticsapi.runtime.robot.driver.mapper.SoftRobotRobotArmDriverCartesianVelocityMapper;
import org.roboticsapi.runtime.robot.driver.mapper.SoftRobotRobotArmDriverJointGoalMapper;
import org.roboticsapi.runtime.robot.driver.mapper.SoftRobotRobotArmDriverJointPositionMapper;
import org.roboticsapi.runtime.robot.driver.mapper.SoftRobotRobotArmForwardKinematicsSensorMapper;
import org.roboticsapi.runtime.robot.driver.mapper.SoftRobotRobotArmInverseKinematicsSensorMapper;
import org.roboticsapi.runtime.robot.mapper.JointSpaceConverterMapper;
import org.roboticsapi.runtime.robot.mapper.NullspacePTPFromMotionMapper;
import org.roboticsapi.runtime.robot.mapper.NullspacePTPMapper;

public final class SoftRobotRobotExtension extends AbstractSoftRobotRoboticsBuilder {

	public SoftRobotRobotExtension() {
		super(SoftRobotRobotArmDriver.class);
	}

	@Override
	protected String[] getRuntimeExtensions() {
		return new String[] { "robotarm", "armkinematics" };
	}

	@Override
	protected void onRoboticsObjectAvailable(RoboticsObject object) {
		super.onRoboticsObjectAvailable(object);

		if (object instanceof RobotArm) {
			// TODO: add DeviceInterfaces
		}
	}

	@Override
	protected void onRuntimeAvailable(SoftRobotRuntime runtime) {
		MapperRegistry mapperregistry = runtime.getMapperRegistry();

		mapperregistry.registerActionMapper(SoftRobotRuntime.class, NullspacePTP.class, new NullspacePTPMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, NullspacePTPFromMotion.class,
				new NullspacePTPFromMotionMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, JointSpaceConverter.class,
				new JointSpaceConverterMapper());

		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotRobotArmDriver.class,
				JointPositionActionResult.class,
				new SoftRobotRobotArmDriverJointPositionMapper<SoftRobotRobotArmDriver>());
		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotRobotArmDriver.class,
				JointGoalActionResult.class, new SoftRobotRobotArmDriverJointGoalMapper<SoftRobotRobotArmDriver>());
		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotRobotArmDriver.class,
				CartesianGoalActionResult.class, new SoftRobotRobotArmDriverCartesianGoalMapper());
		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotRobotArmDriver.class,
				CartesianPositionActionResult.class, new SoftRobotRobotArmDriverCartesianPositionMapper());
		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotRobotArmDriver.class,
				CartesianVelocityActionResult.class, new SoftRobotRobotArmDriverCartesianVelocityMapper());
		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotRobotArmDriver.class,
				CartesianGoalActionResult.class, new CartesianGoalToCartesianVelocityMapper(0.001, 0.001, 0.01, 0.01));

		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, SoftRobotRobotArmForwardKinematicsSensor.class,
				new SoftRobotRobotArmForwardKinematicsSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, SoftRobotRobotArmInverseKinematicsSensor.class,
				new SoftRobotRobotArmInverseKinematicsSensorMapper());
	}

	@Override
	protected void onRuntimeUnavailable(SoftRobotRuntime runtime) {
		// TODO remove mappers???
	}
}
