/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.runtime.rpi.activity;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.activity.Activities;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.runtime.ActuatorInterfaceImpl;
import org.roboticsapi.core.activity.runtime.FromCommandActivity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.framework.io.action.MirrorAnalogValue;
import org.roboticsapi.framework.io.action.SetAnalogValue;
import org.roboticsapi.framework.io.activity.AnalogOutputInterface;
import org.roboticsapi.framework.io.analog.AnalogOutput;
import org.roboticsapi.framework.io.runtime.rpi.AnalogOutputGenericDriver;

public class AnalogOutputInterfaceImpl extends ActuatorInterfaceImpl implements AnalogOutputInterface {

	private final AnalogOutput output;

	public AnalogOutputInterfaceImpl(AnalogOutputGenericDriver driver) {
		super(driver);
		this.output = driver.getDevice();
	}

	@Override
	public Activity mirrorValue(RealtimeDouble sensor) throws RoboticsException {
		final DeviceParameterBag params = getDefaultParameters();
		return new FromCommandActivity("Mirror value from double sensor to analog output",
				() -> createRuntimeCommand(new MirrorAnalogValue(sensor), params), getDevice());
	}

	@Override
	public Activity mirrorVoltage(RealtimeDouble voltageSensor) throws RoboticsException {
		return mirrorValue(output.convertToValue(voltageSensor));
	}

	@Override
	public Activity setValue(RealtimeDouble sensor) throws RoboticsException {
		final DeviceParameterBag params = getDefaultParameters();
		return new FromCommandActivity("Set value from double sensor on analog output",
				() -> createRuntimeCommand(new SetAnalogValue(sensor), params), getDevice());
	}

	@Override
	public Activity setValue(RealtimeDouble sensor, double delay) throws RoboticsException {
		return Activities.strictlySequential("Set value on analog output and wait", setValue(sensor), sleep(delay));
	}

	@Override
	public Activity setValue(final double value) throws RoboticsException {
		final DeviceParameterBag params = getDefaultParameters();
		return new FromCommandActivity("Set constant value on analog output",
				() -> createRuntimeCommand(new SetAnalogValue(value), params), getDevice());
	}

	@Override
	public Activity setValue(final double value, double delay) throws RoboticsException {
		return Activities.strictlySequential("Set value on analog output and wait", setValue(value), sleep(delay));
	}

	@Override
	public Activity setVoltage(final double voltage) throws RoboticsException {
		double value = output.convertToValue(voltage);
		return setValue(value);
	}

	@Override
	public Activity setVoltage(RealtimeDouble voltageSensor) throws RoboticsException {
		return setValue(output.convertToValue(voltageSensor));
	}

	@Override
	public Activity setVoltage(final double voltage, double delay) throws RoboticsException {
		double value = output.convertToValue(voltage);
		return setValue(value, delay);
	}

	@Override
	public Activity setVoltage(RealtimeDouble voltageSensor, double delay) throws RoboticsException {
		return setValue(output.convertToValue(voltageSensor), delay);
	}

}
