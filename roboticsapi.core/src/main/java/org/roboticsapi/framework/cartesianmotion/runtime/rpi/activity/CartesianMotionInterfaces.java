/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.runtime.rpi.activity;

import org.roboticsapi.core.DeviceInterfaceFactoryCollector;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianGoalMotionInterface;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianPathMotionInterface;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianStopInterface;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianVelocityMotionInterface;
import org.roboticsapi.framework.cartesianmotion.activity.HoldMotionInterface;
import org.roboticsapi.framework.cartesianmotion.activity.LinearMotionInterface;
import org.roboticsapi.framework.cartesianmotion.activity.SplineInterface;
import org.roboticsapi.framework.cartesianmotion.device.CartesianActuatorDriver;

public class CartesianMotionInterfaces {

	public static void collectDeviceInterfaceFactories(CartesianActuatorDriver driver,
			DeviceInterfaceFactoryCollector collector) {

		collector.add(CartesianPathMotionInterface.class, () -> new CartesianPathMotionInterfaceImpl(driver));

		collector.add(CartesianGoalMotionInterface.class, () -> new CartesianGoalMotionInterfaceImpl(driver));

		collector.add(CartesianVelocityMotionInterface.class, () -> new CartesianVelocityMotionInterfaceImpl(driver));

		collector.add(LinearMotionInterface.class, () -> new LinearMotionInterfaceImpl(driver));

		collector.add(SplineInterface.class, () -> new SplineInterfaceImpl(driver));

		collector.add(HoldMotionInterface.class, () -> new HoldMotionInterfaceImpl(driver));

		collector.add(CartesianStopInterface.class, () -> new CartesianStopInterfaceImpl(driver));

	}
}
