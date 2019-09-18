/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.javarcc.extension;

import org.roboticsapi.facet.javarcc.extension.JavaRccExtension;
import org.roboticsapi.facet.javarcc.extension.JavaRccExtensionPoint;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.framework.robot.javarcc.interfaces.JArmKinematicsInterface;
import org.roboticsapi.framework.robot.javarcc.primitives.JInvKin;
import org.roboticsapi.framework.robot.javarcc.primitives.JKin;

public class RobotJavaRccExtension extends JavaRccExtension {

	@Override
	public void extend(JavaRccExtensionPoint ep) {
		ep.registerPrimitive("ArmKinematics::InvKin", JInvKin.class);
		ep.registerPrimitive("ArmKinematics::Kin", JKin.class);

		ep.registerInterface("armkinematics",
				(device) -> device instanceof JArmKinematicsInterface ? new RpiParameters() : null);
	}
}
