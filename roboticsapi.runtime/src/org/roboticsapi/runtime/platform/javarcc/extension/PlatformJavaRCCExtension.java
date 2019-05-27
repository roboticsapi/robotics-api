/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.javarcc.extension;

import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtension;
import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtensionPoint;
import org.roboticsapi.runtime.platform.javarcc.devices.JMockDifferentialDriveDevice;
import org.roboticsapi.runtime.platform.javarcc.interfaces.JRobotBaseInterface;
import org.roboticsapi.runtime.platform.javarcc.primitives.JDifferentialDriveController;
import org.roboticsapi.runtime.platform.javarcc.primitives.JWheelMonitor;

public class PlatformJavaRCCExtension extends JavaRCCExtension {

	@Override
	public void extend(JavaRCCExtensionPoint ep) {
		ep.registerPrimitive("RobotBase::WheelMonitor", JWheelMonitor.class);
		ep.registerPrimitive("RobotBase::DifferentialDriveController", JDifferentialDriveController.class);

		ep.registerInterface("robotbase", JRobotBaseInterface.class);

		ep.registerDevice("platform::differentialdrive::mock",
				(p, d) -> new JMockDifferentialDriveDevice(Double.parseDouble(p.get("maxPosVel")),
						Double.parseDouble(p.get("maxRotVel")), Double.parseDouble(p.get("maxPosAcc")),
						Double.parseDouble(p.get("maxRotAcc"))));
	}

}
