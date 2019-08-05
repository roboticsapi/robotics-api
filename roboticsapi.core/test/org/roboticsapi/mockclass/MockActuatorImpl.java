/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.state.ActuatorState;

public class MockActuatorImpl extends MockDeviceImpl implements Actuator {
	ActuatorState completedState = null;
	DeviceParameterBag defaultParameters = new DeviceParameterBag();

	private final List<ActuatorBusyListener> busyListeners = new ArrayList<ActuatorBusyListener>();

	@Override
	public void addBusyListener(ActuatorBusyListener listener) {
		busyListeners.add(listener);
	}

	@Override
	public void removeBusyListener(ActuatorBusyListener listener) {
		if (!busyListeners.isEmpty()) {
			busyListeners.remove(listener);
		}
	}

	public void setCompletedState(ActuatorState completedState) {
		this.completedState = completedState;
	}

	@Override
	public ActuatorState getCompletedState() {
		return completedState;
	}

	@Override
	public void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
	}

	@Override
	public void addDefaultParameters(DeviceParameters newParameters) throws InvalidParametersException {
		defaultParameters.withParameters(newParameters);
	}

	@Override
	public DeviceParameterBag getDefaultParameters() {
		return defaultParameters;
	}

	@Override
	public void checkParameterBounds(DeviceParameters parameters) throws InvalidParametersException {
	}

	@Override
	public void checkParameterBounds(DeviceParameters... parameters) throws InvalidParametersException {
	}

	@Override
	public void checkParameterBounds(DeviceParameterBag parameters) throws InvalidParametersException {
	}

	@Override
	public ActuatorDriver getDriver() {
		DeviceDriver superDriver = super.getDriver();

		if (superDriver instanceof ActuatorDriver) {
			return (ActuatorDriver) superDriver;
		}

		return new MockActuatorDriverImpl(null);
	}
}
