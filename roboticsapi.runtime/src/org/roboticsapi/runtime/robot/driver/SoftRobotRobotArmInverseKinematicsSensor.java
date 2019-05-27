/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.robot.driver;

import org.roboticsapi.core.sensor.DoubleArraySensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.sensor.TransformationSensor;

public final class SoftRobotRobotArmInverseKinematicsSensor extends DoubleArraySensor {

	private final SoftRobotRobotArmDriver driver;
	private final TransformationSensor transformation;
	private final DoubleSensor[] hintJoints;

	public SoftRobotRobotArmInverseKinematicsSensor(SoftRobotRobotArmDriver driver, TransformationSensor transformation,
			DoubleSensor[] hintJoints) {
		super(driver.getRuntime(), driver.getJointCount());
		this.driver = driver;
		this.transformation = transformation;
		this.hintJoints = hintJoints;
	}

	public SoftRobotRobotArmDriver getDriver() {
		return driver;
	}

	public TransformationSensor getTransformation() {
		return transformation;
	}

	public DoubleSensor[] getHintJoints() {
		return hintJoints;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && driver.equals(((SoftRobotRobotArmInverseKinematicsSensor) obj).driver)
				&& transformation.equals(((SoftRobotRobotArmInverseKinematicsSensor) obj).transformation)
				&& hintJoints.equals(((SoftRobotRobotArmInverseKinematicsSensor) obj).hintJoints);
	}

	@Override
	public int hashCode() {
		return classHash(driver, transformation, hintJoints);
	}

	@Override
	public boolean isAvailable() {
		return driver.isPresent() && areAvailable(hintJoints) && transformation.isAvailable();
	}
}
