/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.runtime.rpi;

import org.roboticsapi.core.activity.runtime.SensorInterfaceImpl;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.framework.multijoint.JointSensorInterface;

public class JointSensorInterfaceImpl extends SensorInterfaceImpl implements JointSensorInterface {

	private final JointGenericDriver driver;

	public JointSensorInterfaceImpl(JointGenericDriver driver) {
		super(driver.getDevice(), driver.getRuntime());
		this.driver = driver;
	}

	@Override
	public RealtimeDouble getCommandedPositionSensor() {
		return driver.getCommandedPositionSensor();
	}

	@Override
	public RealtimeDouble getMeasuredPositionSensor() {
		return driver.getMeasuredPositionSensor();
	}

	@Override
	public RealtimeDouble getCommandedVelocitySensor() {
		return driver.getCommandedVelocitySensor();
	}

	@Override
	public RealtimeDouble getMeasuredVelocitySensor() {
		return driver.getMeasuredVelocitySensor();
	}

}
