/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.runtime.rpi.driver;

import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.mapping.NamedActuatorDriver;
import org.roboticsapi.framework.robot.RobotArm;

public class RobotArmInverseKinematicsRealtimeDoubleArray extends RealtimeDoubleArray {

	private final NamedActuatorDriver<? extends RobotArm> driver;
	private final RealtimeTransformation transformation;
	private final RealtimeDouble[] hintJoints;

	public RobotArmInverseKinematicsRealtimeDoubleArray(NamedActuatorDriver<? extends RobotArm> driver,
			RealtimeTransformation transformation, RealtimeDouble[] hintJoints) {
		super(transformation.getRuntime() == null ? driver.getRuntime() : transformation.getRuntime(),
				hintJoints.length);
		this.driver = driver;
		this.transformation = transformation;
		this.hintJoints = hintJoints;
	}

	public NamedActuatorDriver<? extends RobotArm> getDriver() {
		return driver;
	}

	public RealtimeTransformation getTransformation() {
		return transformation;
	}

	public RealtimeDouble[] getHintJoints() {
		return hintJoints;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && driver.equals(((RobotArmInverseKinematicsRealtimeDoubleArray) obj).driver)
				&& transformation.equals(((RobotArmInverseKinematicsRealtimeDoubleArray) obj).transformation)
				&& hintJoints.equals(((RobotArmInverseKinematicsRealtimeDoubleArray) obj).hintJoints);
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
