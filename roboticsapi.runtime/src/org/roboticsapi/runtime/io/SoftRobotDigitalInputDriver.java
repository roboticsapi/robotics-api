/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.io;

import java.util.List;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DeviceBasedBooleanSensor;
import org.roboticsapi.io.digital.DigitalInputDriver;
import org.roboticsapi.runtime.AbstractSoftRobotDeviceDriver;
import org.roboticsapi.runtime.SoftRobotRuntime;

/**
 * {@link DigitalInputDriver} implementation for the SoftRobot RCC.
 */
public final class SoftRobotDigitalInputDriver extends AbstractSoftRobotDeviceDriver implements DigitalInputDriver {

	private int number;

	/**
	 * Constructor.
	 */
	public SoftRobotDigitalInputDriver() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param number     the number
	 * @param runtime    the runtime
	 * @param deviceName the device name
	 */
	protected SoftRobotDigitalInputDriver(int number, SoftRobotRuntime runtime, String deviceName) {
		this();
		setNumber(number);
		setRuntime(runtime);
		setDeviceName(deviceName);
	}

	@ConfigurationProperty(Optional = false)
	public void setNumber(int number) {
		immutableWhenInitialized();
		this.number = number;
	}

	@Override
	public int getNumber() {
		return number;
	}

	@Override
	protected boolean checkDeviceInterfaces(List<String> interfaces) {
		if (!interfaces.contains("io")) {
			return false;
		}

		return true;
	}

	@Override
	public BooleanSensor getSensor() {
		return new DigitalInputValue(this);
	}

	/**
	 * {@link BooleanSensor} implementation for {@link SoftRobotDigitalInputDriver}
	 */
	public static final class DigitalInputValue extends DeviceBasedBooleanSensor<SoftRobotDigitalInputDriver> {

		public DigitalInputValue(final SoftRobotDigitalInputDriver parent) {
			super(parent);
		}

	}
}
