/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.driver;

import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.robot.DHRobotArm;
import org.roboticsapi.runtime.driver.DeviceBasedLoadable;

public abstract class SoftRobotDHRobotArmDriver<T extends DHRobotArm> extends SoftRobotRobotArmDriver
		implements DeviceBasedLoadable<T> {

	@Override
	public boolean build(T robotArm) {
		Map<String, String> parameters = new HashMap<String, String>();

		collectMultiJointDeviceParameters(robotArm, parameters);
		collectDHParameters(robotArm, parameters);
		collectDriverSpecificParameters(parameters);

		return loadDeviceDriver(parameters);
	}

	@Override
	public void delete() {
		deleteDeviceDriver();
	}

	protected abstract void collectDriverSpecificParameters(Map<String, String> parameters);

	protected void collectDHParameters(T robotArm, Map<String, String> parameters) {
		StringBuilder dhD = new StringBuilder("{");
		StringBuilder dhA = new StringBuilder("{");
		StringBuilder dhTheta = new StringBuilder("{");
		StringBuilder dnAlpha = new StringBuilder("{");

		for (int i = 0; i < getJointCount(); i++) {
			dhD.append(robotArm.getD()[i]);
			dhA.append(robotArm.getA()[i]);
			dhTheta.append(robotArm.getTheta()[i]);
			dnAlpha.append(robotArm.getAlpha()[i]);

			if (i != getJointCount() - 1) {
				dhD.append(",");
				dhA.append(",");
				dhTheta.append(",");
				dnAlpha.append(",");
			} else {
				dhD.append("}");
				dhA.append("}");
				dhTheta.append("}");
				dnAlpha.append("}");
			}
		}
		parameters.put("dh_d", dhD.toString());
		parameters.put("dh_a", dhA.toString());
		parameters.put("dh_t", dhTheta.toString());
		parameters.put("dh_al", dnAlpha.toString());
	}
}
