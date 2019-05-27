/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.io;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DeviceBasedBooleanSensor;
import org.roboticsapi.io.Output.CommunicationException;
import org.roboticsapi.io.Output.ValueOutOfRangeException;
import org.roboticsapi.io.digital.DigitalOutputDriver;
import org.roboticsapi.runtime.AbstractSoftRobotActuatorDriver;
import org.roboticsapi.runtime.SoftRobotRuntime;

/**
 * {@link DigitalOutputDriver} implementation for the SoftRobot RCC.
 */
public class SoftRobotDigitalOutputDriver extends AbstractSoftRobotActuatorDriver implements DigitalOutputDriver {

	private int number;

	/**
	 * Constructor.
	 */
	public SoftRobotDigitalOutputDriver() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param number     the number
	 * @param runtime    the runtime
	 * @param deviceName the device name
	 */
	protected SoftRobotDigitalOutputDriver(int number, SoftRobotRuntime runtime, String deviceName) {
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
		return new DigitalOutputValue(this);
	}

	/**
	 * {@link BooleanSensor} implementation for {@link SoftRobotDigitalOutputDriver}
	 */
	public static final class DigitalOutputValue extends DeviceBasedBooleanSensor<SoftRobotDigitalOutputDriver> {

		public DigitalOutputValue(final SoftRobotDigitalOutputDriver parent) {
			super(parent);
		}

	}

	@Override
	public List<ActuatorDriverRealtimeException> defineActuatorDriverExceptions() {
		final List<ActuatorDriverRealtimeException> errors = new ArrayList<ActuatorDriverRealtimeException>();
		errors.add(new CommunicationException(this));
		errors.add(new ValueOutOfRangeException(this));
		return errors;
	}

}
