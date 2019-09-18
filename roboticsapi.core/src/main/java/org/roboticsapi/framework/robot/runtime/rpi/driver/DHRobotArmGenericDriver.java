/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.driver;

import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdoubleArray;
import org.roboticsapi.framework.robot.DHRobotArm;

public abstract class DHRobotArmGenericDriver<T extends DHRobotArm> extends RobotArmGenericDriver<T> {

	@Override
	protected RpiParameters getRpiDeviceParameters() {
		DHRobotArm robotArm = getDevice();
		RPIdoubleArray dhD = new RPIdoubleArray(robotArm.getJointCount());
		RPIdoubleArray dhA = new RPIdoubleArray(robotArm.getJointCount());
		RPIdoubleArray dhTheta = new RPIdoubleArray(robotArm.getJointCount());
		RPIdoubleArray dhAlpha = new RPIdoubleArray(robotArm.getJointCount());

		for (int i = 0; i < getJointCount(); i++) {
			dhD.set(i, new RPIdouble(robotArm.getD()[i]));
			dhA.set(i, new RPIdouble(robotArm.getA()[i]));
			dhTheta.set(i, new RPIdouble(robotArm.getTheta()[i]));
			dhAlpha.set(i, new RPIdouble(robotArm.getAlpha()[i]));
		}
		return super.getRpiDeviceParameters().with("dh_d", dhD).with("dh_a", dhA).with("dh_t", dhTheta).with("dh_al",
				dhAlpha);
	}

}
