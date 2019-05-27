/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.extension;

import org.roboticsapi.platform.action.DriveDirection;
import org.roboticsapi.platform.action.DriveTo;
import org.roboticsapi.platform.action.FollowFrame;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianGoalActionResult;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianGoalToCartesianPositionMapper;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianGoalToCartesianVelocityMapper;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianPositionActionResult;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianVelocityActionResult;
import org.roboticsapi.runtime.cartesianmotion.mapper.CartesianVelocityToCartesianPositionMapper;
import org.roboticsapi.runtime.extension.AbstractSoftRobotRoboticsBuilder;
import org.roboticsapi.runtime.mapping.MapperRegistry;
import org.roboticsapi.runtime.platform.CartesianCommandedPositionSensor;
import org.roboticsapi.runtime.platform.CartesianCommandedVelocitySensor;
import org.roboticsapi.runtime.platform.CartesianMeasuredPositionSensor;
import org.roboticsapi.runtime.platform.CartesianMeasuredVelocitySensor;
import org.roboticsapi.runtime.platform.PlatformWheelPositionDoubleSensor;
import org.roboticsapi.runtime.platform.PlatformWheelVelocityDoubleSensor;
import org.roboticsapi.runtime.platform.RobotBaseCommandedVelocitySensor;
import org.roboticsapi.runtime.platform.RobotBaseOdometrySensor;
import org.roboticsapi.runtime.platform.RobotBaseVelocitySensor;
import org.roboticsapi.runtime.platform.driver.SoftRobotPlatformCartesianPositionDriver;
import org.roboticsapi.runtime.platform.driver.SoftRobotRobotBaseDriver;
import org.roboticsapi.runtime.platform.driver.mapper.SoftRobotPlatformCartesianPositionMapper;
import org.roboticsapi.runtime.platform.driver.mapper.SoftRobotRobotBaseCartesianPositionMapper;
import org.roboticsapi.runtime.platform.driver.mapper.SoftRobotRobotBaseCartesianVelocityMapper;
import org.roboticsapi.runtime.platform.mapper.action.DriveDirectionMapper;
import org.roboticsapi.runtime.platform.mapper.action.DriveToMapper;
import org.roboticsapi.runtime.platform.mapper.action.FollowFrameMapper;
import org.roboticsapi.runtime.platform.mapper.sensor.CartesianCommandedPositionSensorMapper;
import org.roboticsapi.runtime.platform.mapper.sensor.CartesianCommandedVelocitySensorMapper;
import org.roboticsapi.runtime.platform.mapper.sensor.CartesianMeasuredPositionSensorMapper;
import org.roboticsapi.runtime.platform.mapper.sensor.CartesianMeasuredVelocitySensorMapper;
import org.roboticsapi.runtime.platform.mapper.sensor.PlatformWheelPositionDoubleSensorMapper;
import org.roboticsapi.runtime.platform.mapper.sensor.PlatformWheelVelocityDoubleSensorMapper;
import org.roboticsapi.runtime.platform.mapper.sensor.RobotBaseCommandedVelocitySensorMapper;
import org.roboticsapi.runtime.platform.mapper.sensor.RobotBaseOdometrySensorMapper;
import org.roboticsapi.runtime.platform.mapper.sensor.RobotBaseVelocitySensorMapper;

public class SoftRobotPlatformExtension extends AbstractSoftRobotRoboticsBuilder {

	public SoftRobotPlatformExtension() {
		super(SoftRobotRobotBaseDriver.class, SoftRobotPlatformCartesianPositionDriver.class);
	}

	@Override
	protected String[] getRuntimeExtensions() {
		return new String[] { "robotbase", "cartesianposition" };
	}

	@Override
	protected void onRuntimeAvailable(SoftRobotRuntime runtime) {

		MapperRegistry mapperregistry = runtime.getMapperRegistry();

		mapperregistry.registerActionMapper(SoftRobotRuntime.class, DriveTo.class, new DriveToMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, DriveDirection.class, new DriveDirectionMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, FollowFrame.class, new FollowFrameMapper());

		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, RobotBaseOdometrySensor.class,
				new RobotBaseOdometrySensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, RobotBaseVelocitySensor.class,
				new RobotBaseVelocitySensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, RobotBaseCommandedVelocitySensor.class,
				new RobotBaseCommandedVelocitySensorMapper());

		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, PlatformWheelPositionDoubleSensor.class,
				new PlatformWheelPositionDoubleSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, PlatformWheelVelocityDoubleSensor.class,
				new PlatformWheelVelocityDoubleSensorMapper());

		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, CartesianCommandedPositionSensor.class,
				new CartesianCommandedPositionSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, CartesianMeasuredPositionSensor.class,
				new CartesianMeasuredPositionSensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, CartesianCommandedVelocitySensor.class,
				new CartesianCommandedVelocitySensorMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, CartesianMeasuredVelocitySensor.class,
				new CartesianMeasuredVelocitySensorMapper());

		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotRobotBaseDriver.class,
				CartesianGoalActionResult.class, new CartesianGoalToCartesianVelocityMapper(0.05, 0.05, 0.01, 0.01));
		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotRobotBaseDriver.class,
				CartesianVelocityActionResult.class, new SoftRobotRobotBaseCartesianVelocityMapper());
		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotRobotBaseDriver.class,
				CartesianPositionActionResult.class, new SoftRobotRobotBaseCartesianPositionMapper());

		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class,
				SoftRobotPlatformCartesianPositionDriver.class, CartesianPositionActionResult.class,
				new SoftRobotPlatformCartesianPositionMapper());
		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class,
				SoftRobotPlatformCartesianPositionDriver.class, CartesianGoalActionResult.class,
				new CartesianGoalToCartesianPositionMapper(0.01, 0.01, 0.01, 0.01));
		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class,
				SoftRobotPlatformCartesianPositionDriver.class, CartesianVelocityActionResult.class,
				new CartesianVelocityToCartesianPositionMapper());
	}

	@Override
	protected void onRuntimeUnavailable(SoftRobotRuntime runtime) {

	}

}
