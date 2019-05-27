/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.javarcc.extension;

import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtension;
import org.roboticsapi.runtime.javarcc.extension.JavaRCCExtensionPoint;
import org.roboticsapi.runtime.robot.javarcc.interfaces.JArmKinematicsInterface;
import org.roboticsapi.runtime.robot.javarcc.primitives.JInvKin;
import org.roboticsapi.runtime.robot.javarcc.primitives.JKin;

public class RobotJavaRCCExtension extends JavaRCCExtension {

	@Override
	public void extend(JavaRCCExtensionPoint ep) {
		ep.registerPrimitive("ArmKinematics::InvKin", JInvKin.class);
		ep.registerPrimitive("ArmKinematics::Kin", JKin.class);

		ep.registerInterface("armkinematics", JArmKinematicsInterface.class);
	}
}
