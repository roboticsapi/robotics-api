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
import org.roboticsapi.core.sensor.DeviceBasedDoubleSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.io.Output.CommunicationException;
import org.roboticsapi.io.Output.ValueOutOfRangeException;
import org.roboticsapi.io.analog.AnalogOutputDriver;
import org.roboticsapi.runtime.AbstractSoftRobotActuatorDriver;
import org.roboticsapi.runtime.SoftRobotRuntime;

/**
 * {@link AnalogOutputDriver} implementation for the SoftRobot RCC.
 */
public class SoftRobotAnalogOutputDriver extends AbstractSoftRobotActuatorDriver implements AnalogOutputDriver {
	private int number;

	/**
	 * Constructor.
	 */
	public SoftRobotAnalogOutputDriver() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param number     the number
	 * @param runtime    the runtime
	 * @param deviceName the device name
	 */
	protected SoftRobotAnalogOutputDriver(int number, SoftRobotRuntime runtime, String deviceName) {
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
	public DoubleSensor getSensor() {
		return new AnalogOutputValue(this);
	}

	/**
	 * {@link BooleanSensor} implementation for {@link SoftRobotAnalogOutputDriver}
	 */
	public static final class AnalogOutputValue extends DeviceBasedDoubleSensor<SoftRobotAnalogOutputDriver> {

		/**
		 * Constructor.
		 * 
		 * @param parent the driver
		 */
		public AnalogOutputValue(final SoftRobotAnalogOutputDriver parent) {
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
