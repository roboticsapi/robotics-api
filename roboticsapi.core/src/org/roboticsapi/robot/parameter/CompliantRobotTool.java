/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.robot.parameter;

import org.roboticsapi.world.Frame;

public class CompliantRobotTool extends RobotTool {
	final Frame centerOfCompliance;

	public CompliantRobotTool(double mass, Frame centerOfCompliance, Frame centerOfMass) {
		this(mass, centerOfCompliance, centerOfMass, 0, 0, 0);
	}

	public CompliantRobotTool(double mass, Frame centerOfCompliance, Frame centerOfMass, double jx, double jy,
			double jz) {
		super(mass, centerOfMass, jx, jy, jz);
		this.centerOfCompliance = centerOfCompliance;
	}

	public Frame getCenterOfCompliance() {
		return centerOfCompliance;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CompliantRobotTool)) {
			return false;
		}
		return super.equals(obj) && centerOfCompliance.equals(((CompliantRobotTool) obj).getCenterOfCompliance());
	}
}
