/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform.driver;

import java.util.List;

import org.roboticsapi.cartesianmotion.device.CartesianActuatorDriver;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.platform.PlatformDriver;
import org.roboticsapi.runtime.AbstractSoftRobotActuatorDriver;
import org.roboticsapi.runtime.platform.PlatformWheelPositionDoubleSensor;
import org.roboticsapi.runtime.platform.PlatformWheelVelocityDoubleSensor;
import org.roboticsapi.runtime.platform.RobotBaseOdometryConnection;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;

public class SoftRobotRobotBaseDriver extends AbstractSoftRobotActuatorDriver
		implements PlatformDriver, CartesianActuatorDriver {

	private static final String DEVICE_INTERFACE = "robotbase";

	private Frame odometryOrigin;
	private Frame odometryFrame;

	@Override
	public Relation createNewOdometryRelation() {
		return new RobotBaseOdometryConnection(this, getDeviceName());
	}

	@Override
	protected boolean checkDeviceInterfaces(List<String> interfaces) {
		return interfaces.contains(DEVICE_INTERFACE);
	}

	@Override
	public void setup(Frame odometryOrigin, Frame odometryFrame) {
		this.odometryOrigin = odometryOrigin;
		this.odometryFrame = odometryFrame;
	}

	@Override
	public Frame getOdometryOrigin() {
		return odometryOrigin;
	}

	@Override
	public Frame getOdometryFrame() {
		return odometryFrame;
	}

	@Override
	public Frame getReferenceFrame() {
		return getOdometryOrigin();
	}

	@Override
	public Frame getMovingFrame() {
		return getOdometryFrame();
	}

	@Override
	public Relation createRelation() {
		return createNewOdometryRelation();
	}

	@Override
	public DoubleSensor getWheelPositionSensor(int i) {
		return new PlatformWheelPositionDoubleSensor(this, i);
	}

	@Override
	public DoubleSensor getWheelVelocitySensor(int i) {
		return new PlatformWheelVelocityDoubleSensor(this, i);
	}

}
