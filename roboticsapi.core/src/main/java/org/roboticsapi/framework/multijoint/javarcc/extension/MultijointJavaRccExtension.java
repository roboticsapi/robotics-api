/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.javarcc.extension;

import org.roboticsapi.facet.javarcc.extension.JavaRccExtension;
import org.roboticsapi.facet.javarcc.extension.JavaRccExtensionPoint;
import org.roboticsapi.facet.runtime.rpi.RpiParameters;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdoubleArray;
import org.roboticsapi.framework.multijoint.javarcc.devices.JMockMultijointDevice;
import org.roboticsapi.framework.multijoint.javarcc.interfaces.JMultijointInterface;
import org.roboticsapi.framework.multijoint.javarcc.primitives.JJointMonitor;
import org.roboticsapi.framework.multijoint.javarcc.primitives.JJointPosition;
import org.roboticsapi.framework.multijoint.javarcc.primitives.JToolParameters;

public class MultijointJavaRccExtension extends JavaRccExtension {

	@Override
	public void extend(JavaRccExtensionPoint ep) {
		ep.registerPrimitive("RobotArm::JointMonitor", JJointMonitor.class);
		ep.registerPrimitive("RobotArm::JointPosition", JJointPosition.class);
		ep.registerPrimitive("RobotArm::ToolParameters", JToolParameters.class);

		ep.registerInterface("robotarm",
				(device) -> device instanceof JMultijointInterface
						? new RpiParameters().with("jointcount", "" + ((JMultijointInterface) device).getJointCount())
						: null);

		ep.registerDevice("multijoint_sim", (p, d) -> {
			double[] min_joint = param(p, "min_joint");
			int jointCount = min_joint.length;
			double[] max_joint = param(p, "max_joint");
			double[] home_joint = param(p, "home_joint");
			double[] max_vel = param(p, "max_vel");
			double[] max_acc = param(p, "max_acc");
			return new JMockMultijointDevice(jointCount, min_joint, max_joint, home_joint, max_vel, max_acc);
		});
	}

	private double[] param(RpiParameters p, String name) {
		RPIdoubleArray val = p.get(RPIdoubleArray.class, name);
		double[] ret = new double[val.getSize()];
		for (int i = 0; i < ret.length; i++)
			ret[i] = val.get(i).get();
		return ret;
	}
}
