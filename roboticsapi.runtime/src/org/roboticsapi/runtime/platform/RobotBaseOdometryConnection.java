/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform;

import org.roboticsapi.platform.PlatformDriver;
import org.roboticsapi.world.DynamicConnection;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public class RobotBaseOdometryConnection extends DynamicConnection {

	private final PlatformDriver platformDriver;
	private RelationSensor sensor;
	private VelocitySensor velSensor, cmdVelSensor;
	private final String deviceName;

	public RobotBaseOdometryConnection(final PlatformDriver platformDriver, String deviceName) {
		this.platformDriver = platformDriver;
		this.deviceName = deviceName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	@Override
	protected void setFrames(Frame from, Frame to) {
		super.setFrames(from, to);
		sensor = new RelationSensor(new RobotBaseOdometrySensor(platformDriver, deviceName), from, to);
		velSensor = new RobotBaseVelocitySensor(platformDriver, deviceName, from, to);
		cmdVelSensor = new RobotBaseCommandedVelocitySensor(platformDriver, deviceName, from, to);
	}

	@Override
	public boolean isDeletable() {
		return !platformDriver.isInitialized();
	}

	@Override
	public RelationSensor getRelationSensor() {
		return sensor;
	}

	@Override
	public RelationSensor getMeasuredRelationSensor() {
		return sensor;
	}

	@Override
	public VelocitySensor getVelocitySensor() {
		return cmdVelSensor;
	}

	@Override
	public VelocitySensor getMeasuredVelocitySensor() {
		return velSensor;
	}
}
