/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion;

import org.junit.Assert;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActivityHandle;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeVelocity;
import org.roboticsapi.framework.cartesianmotion.activity.CartesianVelocityMotionInterface;
import org.roboticsapi.framework.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.systemtest.DeviceInterfaceTest;
import org.roboticsapi.systemtest.DeviceInterfaceTestMethod;

public class CartesianVelocityMotionInterfaceTest implements DeviceInterfaceTest<CartesianVelocityMotionInterface> {

	@Override
	public Class<CartesianVelocityMotionInterface> getDeviceInterfaceType() {
		return CartesianVelocityMotionInterface.class;
	}

	@DeviceInterfaceTestMethod
	public void testMoveVelocityInZDirectionMovesOnlyInZDirection(CartesianVelocityMotionInterface inf)
			throws RoboticsException, InterruptedException {

		CartesianMotionDevice device = (CartesianMotionDevice) inf.getDevice();

		Activity act = inf.holdVelocity(RealtimeVelocity.createFromConstant(new Twist(0, 0, 1, 0, 0, 0),
				device.getReferenceFrame(), null, device.getReferenceFrame().asOrientation()));
		ActivityHandle ah = act.beginExecute();
		Thread.sleep(100);
		RealtimeTwist velocitySensor = device.getReferenceFrame().getCommandedRealtimeTwistOf(device.getMovingFrame());
		Twist currentValue = velocitySensor.getCurrentValue();
		try {
			Assert.assertTrue("Incorrect twist: " + currentValue,
					currentValue.isEqualTwist(new Twist(0, 0, 1, 0, 0, 0)));
		} finally {
			ah.cancelExecute();
			ah.endExecute();
		}
	}

}
