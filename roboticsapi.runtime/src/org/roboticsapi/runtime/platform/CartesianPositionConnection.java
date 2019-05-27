/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.world.DynamicConnection;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public class CartesianPositionConnection extends DynamicConnection {

	private RelationSensor msrPosSensor, cmdPosSensor;
	private VelocitySensor msrVelSensor, cmdVelSensor;
	private final String deviceName;
	private final ActuatorDriver driver;

	public CartesianPositionConnection(ActuatorDriver driver, String deviceName) {
		this.driver = driver;
		this.deviceName = deviceName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	@Override
	protected void setFrames(Frame from, Frame to) {
		super.setFrames(from, to);
		msrPosSensor = new RelationSensor(new CartesianMeasuredPositionSensor(driver, deviceName), from, to);
		cmdPosSensor = new RelationSensor(new CartesianCommandedPositionSensor(driver, deviceName), from, to);
		msrVelSensor = new CartesianMeasuredVelocitySensor(driver, deviceName, from, to);
		cmdVelSensor = new CartesianCommandedVelocitySensor(driver, deviceName, from, to);
	}

	@Override
	public boolean isDeletable() {
		return !driver.isInitialized();
	}

	@Override
	public RelationSensor getRelationSensor() {
		return cmdPosSensor;
	}

	@Override
	public RelationSensor getMeasuredRelationSensor() {
		return msrPosSensor;
	}

	@Override
	public VelocitySensor getVelocitySensor() {
		return cmdVelSensor;
	}

	@Override
	public VelocitySensor getMeasuredVelocitySensor() {
		return msrVelSensor;
	}
}
