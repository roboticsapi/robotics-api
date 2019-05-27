/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.List;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.State;
import org.roboticsapi.core.exception.RoboticsException;

public abstract class ActuatorInterfaceImpl<A extends Actuator> implements DeviceInterface {

	private final A device;

	/** Default device parameters for this interface */
	private DeviceParameterBag defaultParameters = new DeviceParameterBag();

	public ActuatorInterfaceImpl(A device) {
		this.device = device;
	}

	@Override
	public final A getDevice() {
		return device;
	}

	protected final RoboticsRuntime getRuntime() {
		ActuatorDriver driver = getDevice().getDriver();

		if (driver != null) {
			return driver.getRuntime();
		}
		return null;
	}

	protected final RuntimeCommand createRuntimeCommand(Action action, DeviceParameterBag parameterBag)
			throws RoboticsException {
		return getRuntime().createRuntimeCommand(getDevice(), action, parameterBag);
	}

	/**
	 * Validates the given device parameters.
	 * 
	 * This implementation accepts all parameters p that are accepted by
	 * validateParameters(p) of the associated Actuator.
	 * 
	 * @param parameters parameters to check
	 * @throws InvalidParametersException when the parameters are invalid
	 */
	public void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
		getDevice().validateParameters(parameters);
	}

	/**
	 * Sets the default parameters for this interface (to be used by all Activities
	 * that don't have their own).
	 * 
	 * Overrides default parameters of this interface's Actuator.
	 * 
	 * @param newParameters default parameters for this interface
	 * @throws InvalidParametersException if the parameters are invalid for this
	 *                                    interface
	 */
	@Override
	public void addDefaultParameters(final DeviceParameters newParameters) throws InvalidParametersException {
		validateParameters(newParameters);
		this.defaultParameters = this.defaultParameters.withParameters(newParameters);
	}

	@Override
	public DeviceParameterBag getDefaultParameters() {
		List<DeviceParameters> defaultList = defaultParameters.getAll();
		DeviceParameters[] defaultArray = defaultList.toArray(new DeviceParameters[defaultList.size()]);

		return getDevice().getDefaultParameters().withParameters(defaultArray);
	}

	@Override
	public final void cancel() throws RoboticsException {
		ActivityScheduler.instance().cancel(device);
	}

	@Override
	public final void endExecute() throws RoboticsException {
		ActivityScheduler.instance().endExecute(device);
	}

	public final RtActivity sleep(double seconds) {
		return sleep(seconds, new State[0]);
	}

	public final RtActivity sleep(double seconds, State... takeOverAllowedConditions) {
		if (getDevice() == null) {
			return null;
		}
		return new SleepRtActivity(getDevice().getDriver().getRuntime(), seconds, takeOverAllowedConditions);
	}
}