/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.sensor;

import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;

public class CartesianCommandedRealtimeTwist extends RealtimeTwist {

	private final ActuatorDriver platformDriver;
	private final String deviceName;

	public CartesianCommandedRealtimeTwist(ActuatorDriver platformDriver, String deviceName) {
		super(platformDriver.getRuntime());
		this.platformDriver = platformDriver;
		this.deviceName = deviceName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && platformDriver.equals(((CartesianCommandedRealtimeTwist) obj).platformDriver)
				&& deviceName.equals(((CartesianCommandedRealtimeTwist) obj).deviceName);
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
