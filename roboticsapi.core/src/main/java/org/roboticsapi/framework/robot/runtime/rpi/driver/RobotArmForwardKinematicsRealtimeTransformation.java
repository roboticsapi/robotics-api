/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.driver;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.mapping.NamedActuatorDriver;
import org.roboticsapi.framework.robot.RobotArm;

public final class RobotArmForwardKinematicsRealtimeTransformation extends RealtimeTransformation {

	private final RealtimeDouble[] jointValues;
	private final NamedActuatorDriver<? extends RobotArm> driver;

	public RobotArmForwardKinematicsRealtimeTransformation(NamedActuatorDriver<? extends RobotArm> driver,
			RealtimeDouble[] joints) {
		super(driver.getRuntime());
		this.driver = driver;
		this.jointValues = joints;
	}

	public NamedActuatorDriver<? extends RobotArm> getDriver() {
		return driver;
	}

	public RealtimeDouble[] getJointValues() {
		return jointValues;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && driver.equals(((RobotArmForwardKinematicsRealtimeTransformation) obj).driver)
				&& jointValues.equals(((RobotArmForwardKinematicsRealtimeTransformation) obj).jointValues);
	}

	@Override
	public int hashCode() {
		return classHash(driver, jointValues);
	}

	@Override
	public boolean isAvailable() {
		return driver.isPresent() && areAvailable(jointValues);
	}
}
