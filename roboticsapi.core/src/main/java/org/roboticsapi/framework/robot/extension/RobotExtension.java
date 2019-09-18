/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.extension;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.framework.robot.RobotArm;

public class RobotExtension implements RoboticsObjectListener {

	@Override
	public void onAvailable(RoboticsObject object) {
		if (object instanceof ActuatorDriver) {
			final ActuatorDriver driver = (ActuatorDriver) object;
			if (driver.getDevice() instanceof RobotArm) {
				final RobotArm robot = (RobotArm) driver.getDevice();
			}
		}

	}

	@Override
	public void onUnavailable(RoboticsObject object) {
		// TODO Auto-generated method stub

	}

}
