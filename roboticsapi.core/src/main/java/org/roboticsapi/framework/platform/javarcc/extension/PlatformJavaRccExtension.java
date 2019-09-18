/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform.javarcc.extension;

import org.roboticsapi.facet.javarcc.extension.JavaRccExtension;
import org.roboticsapi.facet.javarcc.extension.JavaRccExtensionPoint;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.framework.platform.javarcc.devices.JMockDifferentialDriveDevice;
import org.roboticsapi.framework.platform.javarcc.interfaces.JRobotBaseInterface;
import org.roboticsapi.framework.platform.javarcc.primitives.JDifferentialDriveController;
import org.roboticsapi.framework.platform.javarcc.primitives.JWheelMonitor;

public class PlatformJavaRccExtension extends JavaRccExtension {

	@Override
	public void extend(JavaRccExtensionPoint ep) {
		ep.registerPrimitive("RobotBase::WheelMonitor", JWheelMonitor.class);
		ep.registerPrimitive("RobotBase::DifferentialDriveController", JDifferentialDriveController.class);

		ep.registerInterface("robotbase",
				(device) -> device instanceof JRobotBaseInterface ? new RpiParameters() : null);

		ep.registerDevice("platform::differentialdrive::mock",
				(p, d) -> new JMockDifferentialDriveDevice(p.get(RPIdouble.class, "maxPosVel").get(),
						p.get(RPIdouble.class, "maxRotVel").get(), p.get(RPIdouble.class, "maxPosAcc").get(),
						p.get(RPIdouble.class, "maxRotAcc").get()));
	}

}
