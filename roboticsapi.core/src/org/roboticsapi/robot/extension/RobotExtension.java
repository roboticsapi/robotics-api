/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.extension;

import org.roboticsapi.cartesianmotion.activity.HoldMotionInterfaceImpl;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.SingleDeviceInterfaceFactory;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.robot.RobotArm;
import org.roboticsapi.robot.activity.GoalMotionInterfaceImpl;
import org.roboticsapi.robot.activity.JoggingInterfaceImpl;
import org.roboticsapi.robot.activity.MotionInterfaceImpl;
import org.roboticsapi.robot.activity.SimulatedRobotArmMotionInterfaceImpl;

public class RobotExtension implements RoboticsObjectListener {

	@Override
	public void onAvailable(RoboticsObject object) {
		if (object instanceof RobotArm) {
			final RobotArm robot = (RobotArm) object;

			robot.addInterfaceFactory(new SingleDeviceInterfaceFactory<MotionInterfaceImpl<RobotArm>>() {
				@Override
				protected MotionInterfaceImpl<RobotArm> build() {
					return new MotionInterfaceImpl<RobotArm>(robot);
				}
			});

			robot.addInterfaceFactory(new SingleDeviceInterfaceFactory<JoggingInterfaceImpl>() {
				@Override
				protected JoggingInterfaceImpl build() {
					return new JoggingInterfaceImpl(robot);
				}
			});

			robot.addInterfaceFactory(new SingleDeviceInterfaceFactory<GoalMotionInterfaceImpl>() {
				@Override
				protected GoalMotionInterfaceImpl build() {
					return new GoalMotionInterfaceImpl(robot);
				}
			});

			robot.addInterfaceFactory(new SingleDeviceInterfaceFactory<HoldMotionInterfaceImpl<RobotArm>>() {
				@Override
				protected HoldMotionInterfaceImpl<RobotArm> build() {
					return new HoldMotionInterfaceImpl<RobotArm>(robot);
				}
			});

			robot.addInterfaceFactory(
					new SingleDeviceInterfaceFactory<SimulatedRobotArmMotionInterfaceImpl<RobotArm>>() {
						@Override
						protected SimulatedRobotArmMotionInterfaceImpl<RobotArm> build() {
							return new SimulatedRobotArmMotionInterfaceImpl<RobotArm>(robot);
						}
					});
		}

	}

	@Override
	public void onUnavailable(RoboticsObject object) {
		// TODO Auto-generated method stub

	}

}
