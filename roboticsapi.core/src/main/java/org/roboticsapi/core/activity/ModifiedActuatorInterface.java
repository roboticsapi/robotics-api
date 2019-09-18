/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class ModifiedActuatorInterface<T extends ActuatorInterface> implements ActuatorInterface {

	protected final T instance;

	public ModifiedActuatorInterface(T instance) {
		this.instance = instance;
	}

	@Override
	public void addDefaultParameters(DeviceParameters newParameters) throws InvalidParametersException {
		instance.addDefaultParameters(newParameters);
	}

	@Override
	public DeviceParameterBag getDefaultParameters() {
		return instance.getDefaultParameters();
	}

	@Override
	public void cancel() throws RoboticsException {
		instance.cancel();
	}

	@Override
	public void endExecute() throws RoboticsException {
		instance.endExecute();
	}

	@Override
	public Device getDevice() {
		return instance.getDevice();
	}

	@Override
	public RoboticsRuntime getRuntime() {
		return instance.getRuntime();
	}

	@Override
	public Activity sleep(double seconds) throws RoboticsException {
		return instance.sleep(seconds);
	}

	@Override
	public Activity sleep(RealtimeBoolean waitFor) throws RoboticsException {
		return instance.sleep(waitFor);
	}

	@Override
	public Activity ifElse(String name, RealtimeBoolean condition, Activity ifTrue, Activity ifFalse)
			throws RoboticsException {
		return ifElse(name, condition, ifTrue, ifFalse);
	}

}
