/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.mockclass;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceInterfaceFactory;
import org.roboticsapi.core.DeviceInterfaceModifier;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.realtimevalue.realtimeboolean.ActuatorRealtimeBoolean;

public class MockActuatorImpl extends MockDeviceImpl implements Actuator {

	@Override
	public <U extends DeviceInterface> U use(Class<U> deviceInterface, DeviceInterface compatibleWith) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <U extends DeviceInterface> U use(Class<U> deviceInterface, RoboticsRuntime compatibleWith) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DeviceInterface> void addInterfaceFactory(Class<T> type, DeviceInterfaceFactory<T> factory) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends DeviceInterface> void addInterfaceModifier(Class<T> type, DeviceInterfaceModifier<T> factory) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addInterfaceListener(InterfaceListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeInterfaceListener(InterfaceListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public ActuatorRealtimeBoolean getCompletedState(Command scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDefaultParameters(DeviceParameters newParameters) throws InvalidParametersException {
		// TODO Auto-generated method stub

	}

	@Override
	public DeviceParameterBag getDefaultParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void checkParameterBounds(DeviceParameters parameters) throws InvalidParametersException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkParameterBounds(DeviceParameters... parameters) throws InvalidParametersException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkParameterBounds(DeviceParameterBag parameters) throws InvalidParametersException {
		// TODO Auto-generated method stub

	}

	@Override
	public ActuatorDriver getDriver(RoboticsRuntime runtime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActuatorDriver getDriver() {
		// TODO Auto-generated method stub
		return null;
	}

}
