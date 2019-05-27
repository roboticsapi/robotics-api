/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.driver;

import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.sensor.TransformationSensor;

public final class SoftRobotRobotArmForwardKinematicsSensor extends TransformationSensor {

	private final DoubleSensor[] jointSensors;
	private final SoftRobotRobotArmDriver driver;

	public SoftRobotRobotArmForwardKinematicsSensor(SoftRobotRobotArmDriver driver, DoubleSensor[] joints) {
		super(driver.getRuntime());
		this.driver = driver;
		this.jointSensors = joints;
	}

	public SoftRobotRobotArmDriver getDriver() {
		return driver;
	}

	public DoubleSensor[] getJointSensors() {
		return jointSensors;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && driver.equals(((SoftRobotRobotArmForwardKinematicsSensor) obj).driver)
				&& jointSensors.equals(((SoftRobotRobotArmForwardKinematicsSensor) obj).jointSensors);
	}

	@Override
	public int hashCode() {
		return classHash(driver, jointSensors);
	}

	@Override
	public boolean isAvailable() {
		return driver.isPresent() && areAvailable(jointSensors);
	}
}
