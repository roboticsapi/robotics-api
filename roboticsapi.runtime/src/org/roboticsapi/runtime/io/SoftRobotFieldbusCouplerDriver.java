/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.io;

import java.util.List;

import org.roboticsapi.io.FieldbusCouplerDriver;
import org.roboticsapi.io.analog.AnalogInputDriver;
import org.roboticsapi.io.analog.AnalogOutputDriver;
import org.roboticsapi.io.digital.DigitalInputDriver;
import org.roboticsapi.io.digital.DigitalOutputDriver;
import org.roboticsapi.runtime.AbstractSoftRobotDeviceDriver;

/**
 * {@link FieldbusCouplerDriver} implementation for the SoftRobot RCC.
 */
public class SoftRobotFieldbusCouplerDriver extends AbstractSoftRobotDeviceDriver implements FieldbusCouplerDriver {

	@Override
	public DigitalInputDriver createDigitalInputDriver(int number) {
		return new SoftRobotDigitalInputDriver(number, getRuntime(), getDeviceName());
	}

	@Override
	public AnalogInputDriver createAnalogInputDriver(int number) {
		return new SoftRobotAnalogInputDriver(number, getRuntime(), getDeviceName());
	}

	@Override
	public DigitalOutputDriver createDigitalOutputDriver(int number) {
		return new SoftRobotDigitalOutputDriver(number, getRuntime(), getDeviceName());
	}

	@Override
	public AnalogOutputDriver createAnalogOutputDriver(int number) {
		return new SoftRobotAnalogOutputDriver(number, getRuntime(), getDeviceName());
	}

	@Override
	protected boolean checkDeviceInterfaces(List<String> interfaces) {
		return interfaces.contains("io");
	}

}
