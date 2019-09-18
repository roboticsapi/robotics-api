/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform.runtime.rpi.extension;

import org.roboticsapi.facet.runtime.rpi.extension.RpiExtension;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianActuatorCartesianPositionMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianGoalActionResult;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianGoalToCartesianPositionMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianPositionActionResult;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianVelocityActionResult;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianVelocityGoalActionResult;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianVelocityGoalToCartesianVelocityMapper;
import org.roboticsapi.framework.cartesianmotion.runtime.rpi.mapper.CartesianVelocityToCartesianPositionMapper;
import org.roboticsapi.framework.platform.runtime.rpi.driver.DifferentialWheeledPlatformGenericDriver;
import org.roboticsapi.framework.platform.runtime.rpi.driver.DifferentialWheeledPlatformMockDriver;
import org.roboticsapi.framework.platform.runtime.rpi.driver.MecanumPlatformGenericDriver;
import org.roboticsapi.framework.platform.runtime.rpi.mapper.PlatformWheelPositionDoubleSensorMapper;
import org.roboticsapi.framework.platform.runtime.rpi.mapper.PlatformWheelVelocityDoubleSensorMapper;

public class PlatformRuntimeExtension extends RpiExtension {

	public PlatformRuntimeExtension() {
		super(MecanumPlatformGenericDriver.class, DifferentialWheeledPlatformGenericDriver.class,
				DifferentialWheeledPlatformMockDriver.class);
	}

	@Override
	protected void registerMappers(MapperRegistry mr) {

		mr.registerRealtimeValueMapper(new PlatformWheelPositionDoubleSensorMapper());
		mr.registerRealtimeValueMapper(new PlatformWheelVelocityDoubleSensorMapper());

		mr.registerActuatorDriverMapper(MecanumPlatformGenericDriver.class, CartesianPositionActionResult.class,
				new CartesianActuatorCartesianPositionMapper());
		mr.registerActuatorDriverMapper(MecanumPlatformGenericDriver.class, CartesianGoalActionResult.class,
				new CartesianGoalToCartesianPositionMapper(0.0001, 0.0001, 0.0001, 0.0001));
		mr.registerActuatorDriverMapper(MecanumPlatformGenericDriver.class, CartesianVelocityActionResult.class,
				new CartesianVelocityToCartesianPositionMapper());
		mr.registerActuatorDriverMapper(MecanumPlatformGenericDriver.class, CartesianVelocityGoalActionResult.class,
				new CartesianVelocityGoalToCartesianVelocityMapper());

		mr.registerActuatorDriverMapper(DifferentialWheeledPlatformGenericDriver.class,
				CartesianPositionActionResult.class, new CartesianActuatorCartesianPositionMapper());
		mr.registerActuatorDriverMapper(DifferentialWheeledPlatformGenericDriver.class, CartesianGoalActionResult.class,
				new CartesianGoalToCartesianPositionMapper(0.0001, 0.0001, 0.0001, 0.0001));
		mr.registerActuatorDriverMapper(DifferentialWheeledPlatformGenericDriver.class,
				CartesianVelocityActionResult.class, new CartesianVelocityToCartesianPositionMapper());
		mr.registerActuatorDriverMapper(DifferentialWheeledPlatformGenericDriver.class,
				CartesianVelocityGoalActionResult.class, new CartesianVelocityGoalToCartesianVelocityMapper());

	}

	@Override
	protected void unregisterMappers(MapperRegistry mr) {

	}

}
