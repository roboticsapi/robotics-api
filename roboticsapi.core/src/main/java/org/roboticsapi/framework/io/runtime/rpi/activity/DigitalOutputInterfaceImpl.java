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
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.framework.io.action.MirrorDigitalValue;
import org.roboticsapi.framework.io.action.SetDigitalValue;
import org.roboticsapi.framework.io.activity.DigitalOutputInterface;
import org.roboticsapi.framework.io.runtime.rpi.DigitalOutputGenericDriver;

public class DigitalOutputInterfaceImpl extends ActuatorInterfaceImpl implements DigitalOutputInterface {

	public DigitalOutputInterfaceImpl(DigitalOutputGenericDriver driver) {
		super(driver);
	}

	@Override
	public Activity mirrorValue(RealtimeBoolean sensor) throws RoboticsException {
		final DeviceParameterBag params = getDefaultParameters();
		return new FromCommandActivity("Mirror value from boolean sensor to digital output",
				() -> createRuntimeCommand(new MirrorDigitalValue(sensor), params), getDevice());
	}

	@Override
	public Activity pulse(boolean value, double duration) throws RoboticsException {
		return Activities.strictlySequential("Pulse boolean value on digital output", setValue(value), sleep(duration),
				setValue(!value));
	}

	@Override
	public Activity setValue(RealtimeBoolean sensor) throws RoboticsException {
		final DeviceParameterBag params = getDefaultParameters();
		return new FromCommandActivity("Set value from boolean sensor on digital output",
				() -> createRuntimeCommand(new SetDigitalValue(sensor), params), getDevice());
	}

	@Override
	public Activity setValue(final boolean value) throws RoboticsException {
		final DeviceParameterBag params = getDefaultParameters();
		return new FromCommandActivity("Set boolean value on digital output",
				() -> createRuntimeCommand(new SetDigitalValue(value), params), getDevice());
	}

	@Override
	public Activity setValue(RealtimeBoolean sensor, double delay) throws RoboticsException {
		return Activities.strictlySequential("Set boolean value on digital output and wait", setValue(sensor),
				sleep(delay));
	}

	@Override
	public Activity setValue(final boolean value, double delay) throws RoboticsException {
		return Activities.strictlySequential("Set value from boolean sensor on digital output and wait",
				setValue(value), sleep(delay));
	}

}
