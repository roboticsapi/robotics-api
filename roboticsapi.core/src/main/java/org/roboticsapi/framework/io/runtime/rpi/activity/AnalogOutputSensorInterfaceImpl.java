/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.runtime.rpi.activity;

import org.roboticsapi.core.activity.runtime.SensorInterfaceImpl;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.framework.io.activity.AnalogOutputSensorInterface;
import org.roboticsapi.framework.io.runtime.rpi.AnalogOutputGenericDriver;

public class AnalogOutputSensorInterfaceImpl extends SensorInterfaceImpl implements AnalogOutputSensorInterface {
	private final AnalogOutputGenericDriver driver;

	public AnalogOutputSensorInterfaceImpl(AnalogOutputGenericDriver driver) {
		super(driver.getDevice(), driver.getRuntime());
		this.driver = driver;
	}

	@Override
	public RealtimeDouble getVoltageSensor() {
		return driver.getDevice().convertToVoltage(driver.getSensor());
	}

	@Override
	public RealtimeDouble getRawValueSensor() {
		return driver.getSensor();
	}

}
