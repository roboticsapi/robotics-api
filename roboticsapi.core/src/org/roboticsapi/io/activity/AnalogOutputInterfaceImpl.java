/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.io.activity;

import org.roboticsapi.activity.ActuatorInterfaceImpl;
import org.roboticsapi.activity.FromCommandActivity;
import org.roboticsapi.activity.RtActivities;
import org.roboticsapi.activity.RtActivity;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.io.action.MirrorAnalogValue;
import org.roboticsapi.io.action.SetAnalogValue;
import org.roboticsapi.io.analog.AnalogOutput;

public class AnalogOutputInterfaceImpl extends ActuatorInterfaceImpl<AnalogOutput> implements AnalogOutputInterface {

	public AnalogOutputInterfaceImpl(AnalogOutput device) {
		super(device);
	}

	@Override
	public RtActivity mirrorValue(DoubleSensor sensor) throws RoboticsException {
		final DeviceParameterBag params = getDefaultParameters();
		final RuntimeCommand command = createRuntimeCommand(new MirrorAnalogValue(sensor), params);
		return new FromCommandActivity("Mirror value from double sensor to analog output", command,
				new Device[] { getDevice() }, new Device[] { getDevice() });
	}

	@Override
	public RtActivity mirrorVoltage(DoubleSensor voltageSensor) throws RoboticsException {
		return mirrorValue(getDevice().convertToValue(voltageSensor));
	}

	@Override
	public RtActivity setValue(DoubleSensor sensor) throws RoboticsException {
		final DeviceParameterBag params = getDefaultParameters();
		final RuntimeCommand command = createRuntimeCommand(new SetAnalogValue(sensor), params);
		return new FromCommandActivity("Set value from double sensor on analog output", command,
				new Device[] { getDevice() }, new Device[] { getDevice() });
	}

	@Override
	public RtActivity setValue(DoubleSensor sensor, double delay) throws RoboticsException {
		return RtActivities.strictlySequential("Set value on analog output and wait", setValue(sensor), sleep(delay));
	}

	@Override
	public RtActivity setValue(final double value) throws RoboticsException {
		final DeviceParameterBag params = getDefaultParameters();
		final RuntimeCommand command = createRuntimeCommand(new SetAnalogValue(value), params);
		return new FromCommandActivity("Set constant value on analog output", command, new Device[] { getDevice() },
				new Device[] { getDevice() });
	}

	@Override
	public RtActivity setValue(final double value, double delay) throws RoboticsException {
		return RtActivities.strictlySequential("Set value on analog output and wait", setValue(value), sleep(delay));
	}

	@Override
	public RtActivity setVoltage(final double voltage) throws RoboticsException {
		double value = getDevice().convertToValue(voltage);
		return setValue(value);
	}

	@Override
	public RtActivity setVoltage(DoubleSensor voltageSensor) throws RoboticsException {
		return setValue(getDevice().convertToValue(voltageSensor));
	}

	@Override
	public RtActivity setVoltage(final double voltage, double delay) throws RoboticsException {
		double value = getDevice().convertToValue(voltage);
		return setValue(value, delay);
	}

	@Override
	public RtActivity setVoltage(DoubleSensor voltageSensor, double delay) throws RoboticsException {
		return setValue(getDevice().convertToValue(voltageSensor), delay);
	}

}
