/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.extension;

import org.roboticsapi.cartesianmotion.activity.CartesianGoalMotionInterface;
import org.roboticsapi.cartesianmotion.activity.CartesianGoalMotionInterfaceImpl;
import org.roboticsapi.cartesianmotion.activity.CartesianPathMotionInterface;
import org.roboticsapi.cartesianmotion.activity.CartesianPathMotionInterfaceImpl;
import org.roboticsapi.cartesianmotion.activity.CartesianVelocityMotionInterface;
import org.roboticsapi.cartesianmotion.activity.CartesianVelocityMotionInterfaceImpl;
import org.roboticsapi.cartesianmotion.activity.HoldMotionInterface;
import org.roboticsapi.cartesianmotion.activity.HoldMotionInterfaceImpl;
import org.roboticsapi.cartesianmotion.activity.HybridMoveInterface;
import org.roboticsapi.cartesianmotion.activity.HybridMoveInterfaceImpl;
import org.roboticsapi.cartesianmotion.activity.LinearMotionInterface;
import org.roboticsapi.cartesianmotion.activity.LinearMotionInterfaceImpl;
import org.roboticsapi.cartesianmotion.activity.SplineInterface;
import org.roboticsapi.cartesianmotion.activity.SplineInterfaceImpl;
import org.roboticsapi.cartesianmotion.device.CartesianActuator;
import org.roboticsapi.cartesianmotion.device.CartesianMotionDevice;
import org.roboticsapi.core.DerivedDeviceInterfaceFactory;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.SingleDeviceInterfaceFactory;
import org.roboticsapi.extension.AbstractRoboticsObjectBuilder;
import org.roboticsapi.extension.RoboticsObjectListener;

public class CartesianMotionExtension extends AbstractRoboticsObjectBuilder implements RoboticsObjectListener {

	public CartesianMotionExtension() {
		super(CartesianActuator.class);
	}

	@Override
	public void onAvailable(RoboticsObject object) {
		if (object instanceof CartesianMotionDevice) {
			final CartesianMotionDevice cmd = (CartesianMotionDevice) object;

			cmd.addInterfaceFactoryListener(new DerivedDeviceInterfaceFactory<CartesianPathMotionInterface>(
					LinearMotionInterface.class, SplineInterface.class) {
				@Override
				protected CartesianPathMotionInterface build() {
					return new CartesianPathMotionInterfaceImpl<CartesianMotionDevice>(cmd,
							use(LinearMotionInterface.class), use(SplineInterface.class));
				}
			});

			cmd.addInterfaceFactory(new SingleDeviceInterfaceFactory<CartesianGoalMotionInterface>() {
				@Override
				protected CartesianGoalMotionInterface build() {
					return new CartesianGoalMotionInterfaceImpl<CartesianMotionDevice>(cmd);
				}
			});

			cmd.addInterfaceFactory(new SingleDeviceInterfaceFactory<CartesianVelocityMotionInterface>() {
				@Override
				protected CartesianVelocityMotionInterface build() {
					return new CartesianVelocityMotionInterfaceImpl<CartesianMotionDevice>(cmd);
				}
			});

			cmd.addInterfaceFactory(new SingleDeviceInterfaceFactory<LinearMotionInterface>() {
				@Override
				protected LinearMotionInterface build() {
					return new LinearMotionInterfaceImpl<CartesianMotionDevice>(cmd);
				}
			});

			cmd.addInterfaceFactory(new SingleDeviceInterfaceFactory<SplineInterface>() {
				@Override
				protected SplineInterface build() {
					return new SplineInterfaceImpl<CartesianMotionDevice>(cmd);
				}
			});

			cmd.addInterfaceFactory(new SingleDeviceInterfaceFactory<HoldMotionInterface>() {
				@Override
				protected HoldMotionInterface build() {
					return new HoldMotionInterfaceImpl<CartesianMotionDevice>(cmd);
				}
			});

			cmd.addInterfaceFactory(new SingleDeviceInterfaceFactory<HybridMoveInterface>() {
				@Override
				protected HybridMoveInterface build() {
					return new HybridMoveInterfaceImpl(cmd);
				}
			});
		}
	}

	@Override
	public void onUnavailable(RoboticsObject object) {
		// TODO Auto-generated method stub

	}

}
