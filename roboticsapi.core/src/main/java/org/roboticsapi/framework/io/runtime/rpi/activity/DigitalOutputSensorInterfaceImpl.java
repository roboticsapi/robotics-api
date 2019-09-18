/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.runtime.rpi.activity;

import org.roboticsapi.core.activity.runtime.SensorInterfaceImpl;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.framework.io.activity.DigitalOutputSensorInterface;
import org.roboticsapi.framework.io.runtime.rpi.DigitalOutputGenericDriver;

public class DigitalOutputSensorInterfaceImpl extends SensorInterfaceImpl implements DigitalOutputSensorInterface {
	private final DigitalOutputGenericDriver driver;

	public DigitalOutputSensorInterfaceImpl(DigitalOutputGenericDriver driver) {
		super(driver.getDevice(), driver.getRuntime());
		this.driver = driver;
	}

	@Override
	public RealtimeBoolean getSensor() {
		return driver.getSensor();
	}

}
