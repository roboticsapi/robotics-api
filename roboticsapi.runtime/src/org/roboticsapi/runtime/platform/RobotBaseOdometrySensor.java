/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform;

import org.roboticsapi.platform.PlatformDriver;
import org.roboticsapi.world.sensor.TransformationSensor;

public final class RobotBaseOdometrySensor extends TransformationSensor {

	private final PlatformDriver platformDriver;
	private final String deviceName;

	public RobotBaseOdometrySensor(PlatformDriver platformDriver, String deviceName) {
		super(platformDriver.getRuntime());
		this.platformDriver = platformDriver;
		this.deviceName = deviceName;
	}

	public PlatformDriver getPlatformDriver() {
		return platformDriver;
	}

	public String getDeviceName() {
		return deviceName;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && platformDriver.equals(((RobotBaseOdometrySensor) obj).platformDriver)
				&& deviceName.equals(((RobotBaseOdometrySensor) obj).deviceName);
	}

	@Override
	public int hashCode() {
		return classHash(platformDriver, deviceName);
	}

	@Override
	public boolean isAvailable() {
		return platformDriver.isPresent();
	}
}
