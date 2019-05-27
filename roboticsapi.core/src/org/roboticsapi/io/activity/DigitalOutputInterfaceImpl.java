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
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.io.action.MirrorDigitalValue;
import org.roboticsapi.io.action.SetDigitalValue;
import org.roboticsapi.io.digital.DigitalOutput;

public class DigitalOutputInterfaceImpl extends ActuatorInterfaceImpl<DigitalOutput> implements DigitalOutputInterface {

	public DigitalOutputInterfaceImpl(DigitalOutput device) {
		super(device);
	}

	@Override
	public RtActivity mirrorValue(BooleanSensor sensor) throws RoboticsException {
		final DeviceParameterBag params = getDefaultParameters();
		final RuntimeCommand command = createRuntimeCommand(new MirrorDigitalValue(sensor), params);
		return new FromCommandActivity("Mirror value from boolean sensor to digital output", command,
				new Device[] { getDevice() }, new Device[] { getDevice() });
	}

	@Override
	public RtActivity pulse(boolean value, double duration) throws RoboticsException {
		return RtActivities.strictlySequential("Pulse boolean value on digital output", setValue(value),
				sleep(duration), setValue(!value));
	}

	@Override
	public RtActivity setValue(BooleanSensor sensor) throws RoboticsException {
		final DeviceParameterBag params = getDefaultParameters();
		final RuntimeCommand command = createRuntimeCommand(new SetDigitalValue(sensor), params);
		return new FromCommandActivity("Set value from boolean sensor on digital output", command,
				new Device[] { getDevice() }, new Device[] { getDevice() });
	}

	@Override
	public RtActivity setValue(final boolean value) throws RoboticsException {
		final DeviceParameterBag params = getDefaultParameters();
		final RuntimeCommand command = createRuntimeCommand(new SetDigitalValue(value), params);
		return new FromCommandActivity("Set boolean value on digital output", command, new Device[] { getDevice() },
				new Device[] { getDevice() });
	}

	@Override
	public RtActivity setValue(BooleanSensor sensor, double delay) throws RoboticsException {
		return RtActivities.strictlySequential("Set boolean value on digital output and wait", setValue(sensor),
				sleep(delay));
	}

	@Override
	public RtActivity setValue(final boolean value, double delay) throws RoboticsException {
		return RtActivities.strictlySequential("Set value from boolean sensor on digital output and wait",
				setValue(value), sleep(delay));
	}

}
