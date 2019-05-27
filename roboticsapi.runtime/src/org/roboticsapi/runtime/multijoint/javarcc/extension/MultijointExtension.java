/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.javarcc.extension;

import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtension;
import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtensionPoint;
import org.roboticsapi.runtime.multijoint.javarcc.interfaces.JMultijointInterface;
import org.roboticsapi.runtime.multijoint.javarcc.primitives.JJointMonitor;
import org.roboticsapi.runtime.multijoint.javarcc.primitives.JJointPosition;
import org.roboticsapi.runtime.multijoint.javarcc.primitives.JToolParameters;

public class MultijointExtension extends JavaRCCExtension {

	@Override
	public void extend(JavaRCCExtensionPoint ep) {
		ep.registerPrimitive("RobotArm::JointMonitor", JJointMonitor.class);
		ep.registerPrimitive("RobotArm::JointPosition", JJointPosition.class);
		ep.registerPrimitive("RobotArm::ToolParameters", JToolParameters.class);

		ep.registerInterface("robotarm", JMultijointInterface.class);
	}
}
