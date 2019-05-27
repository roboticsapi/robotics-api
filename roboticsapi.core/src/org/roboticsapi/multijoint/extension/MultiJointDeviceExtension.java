/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.SingleDeviceInterfaceFactory;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.multijoint.MultiJointDevice;
import org.roboticsapi.multijoint.activity.JointGoalMotionInterfaceImpl;
import org.roboticsapi.multijoint.activity.JointHoldMotionInterfaceImpl;
import org.roboticsapi.multijoint.activity.JointJoggingInterfaceImpl;
import org.roboticsapi.multijoint.activity.JointPtpInterfaceImpl;
import org.roboticsapi.multijoint.activity.SimulatedJointMotionInterfaceImpl;

public class MultiJointDeviceExtension implements RoboticsObjectListener {

	@Override
	public void onAvailable(final RoboticsObject object) {
		if (object instanceof MultiJointDevice) {
			final MultiJointDevice mjd = (MultiJointDevice) object;

			mjd.addInterfaceFactory(new SingleDeviceInterfaceFactory<JointGoalMotionInterfaceImpl>() {
				@Override
				protected JointGoalMotionInterfaceImpl build() {
					return new JointGoalMotionInterfaceImpl(mjd);
				}
			});

			mjd.addInterfaceFactory(new SingleDeviceInterfaceFactory<JointHoldMotionInterfaceImpl>() {
				@Override
				protected JointHoldMotionInterfaceImpl build() {
					return new JointHoldMotionInterfaceImpl(mjd);
				}
			});

			mjd.addInterfaceFactory(new SingleDeviceInterfaceFactory<JointPtpInterfaceImpl<MultiJointDevice>>() {
				@Override
				protected JointPtpInterfaceImpl<MultiJointDevice> build() {
					return new JointPtpInterfaceImpl<MultiJointDevice>(mjd);
				}
			});

			mjd.addInterfaceFactory(new SingleDeviceInterfaceFactory<JointJoggingInterfaceImpl>() {
				@Override
				protected JointJoggingInterfaceImpl build() {
					return new JointJoggingInterfaceImpl(mjd);
				}
			});

			mjd.addInterfaceFactory(
					new SingleDeviceInterfaceFactory<SimulatedJointMotionInterfaceImpl<MultiJointDevice>>() {
						@Override
						protected SimulatedJointMotionInterfaceImpl<MultiJointDevice> build() {
							return new SimulatedJointMotionInterfaceImpl<MultiJointDevice>(mjd);
						}
					});
		}

	}

	@Override
	public void onUnavailable(RoboticsObject object) {
		// TODO Auto-generated method stub

	}

}
