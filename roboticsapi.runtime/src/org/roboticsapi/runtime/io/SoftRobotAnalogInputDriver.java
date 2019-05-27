/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.io;

import java.util.List;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.sensor.DeviceBasedDoubleSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.io.analog.AnalogInputDriver;
import org.roboticsapi.runtime.AbstractSoftRobotDeviceDriver;
import org.roboticsapi.runtime.SoftRobotRuntime;

/**
 * {@link AnalogInputDriver} implementation for the SoftRobot RCC.
 */
public class SoftRobotAnalogInputDriver extends AbstractSoftRobotDeviceDriver implements AnalogInputDriver {

	private int number;

	/**
	 * Constructor.
	 */
	public SoftRobotAnalogInputDriver() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param number     the number
	 * @param runtime    the runtime
	 * @param deviceName the device name
	 */
	protected SoftRobotAnalogInputDriver(int number, SoftRobotRuntime runtime, String deviceName) {
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
	public DoubleSensor getSensor() {
		return new AnalogInputValue(this);
	}

	/**
	 * {@link DoubleSensor} implementation for {@link SoftRobotAnalogInputDriver}
	 */
	public static final class AnalogInputValue extends DeviceBasedDoubleSensor<SoftRobotAnalogInputDriver> {

		public AnalogInputValue(final SoftRobotAnalogInputDriver parent) {
			super(parent);
		}

	}

	@Override
	protected boolean checkDeviceInterfaces(List<String> interfaces) {
		if (!interfaces.contains("io")) {
			return false;
		}

		return true;
	}

}
