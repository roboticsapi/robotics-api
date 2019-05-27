/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.platform;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.sensor.VelocitySensor;

public class CartesianMeasuredVelocitySensor extends VelocitySensor {

	private final ActuatorDriver platformDriver;
	private final String deviceName;

	public CartesianMeasuredVelocitySensor(ActuatorDriver platformDriver, String deviceName, Frame from, Frame to) {
		super(platformDriver.getRuntime(), to, from, to.getPoint(), to.getOrientation());
		this.platformDriver = platformDriver;
		this.deviceName = deviceName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && platformDriver.equals(((CartesianMeasuredVelocitySensor) obj).platformDriver)
				&& deviceName.equals(((CartesianMeasuredVelocitySensor) obj).deviceName);
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
