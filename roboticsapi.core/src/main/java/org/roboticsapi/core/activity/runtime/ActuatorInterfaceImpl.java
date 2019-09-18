/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity.runtime;

import java.util.List;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.activity.Activities;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActivityScheduler;
import org.roboticsapi.core.activity.ActuatorInterface;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.runtime.CommandRealtimeException;

public abstract class ActuatorInterfaceImpl implements ActuatorInterface {

	/** Default device parameters for this interface */
	private DeviceParameterBag defaultParameters = new DeviceParameterBag();

	private ActuatorDriver driver;

	public ActuatorInterfaceImpl(ActuatorDriver driver) {
		this.driver = driver;
	}

	public ActuatorDriver getDriver() {
		return driver;
	}

	@Override
	public Actuator getDevice() {
		return driver.getDevice();
	}

	@Override
	public final RoboticsRuntime getRuntime() {
		return driver.getRuntime();
	}

	protected final RuntimeCommand createRuntimeCommand(Action action, DeviceParameterBag parameterBag)
			throws RoboticsException {
		return getRuntime().createRuntimeCommand(getDevice().getDriver(), action, parameterBag);
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

	protected <T extends ActuatorInterface> T use(Class<T> type) throws InvalidParametersException {
		T ret = getDevice().use(type);
		for (DeviceParameters param : getDefaultParameters().getArray()) {
			ret.addDefaultParameters(param);
		}
		return ret;
	}

	@Override
	public final void cancel() throws RoboticsException {
		ActivityScheduler.getInstance().cancel(driver.getDevice());
	}

	@Override
	public final void endExecute() throws RoboticsException {
		ActivityScheduler.getInstance().endExecute(driver.getDevice());
	}

	public final Activity sleep(double seconds) throws RoboticsException {
		return new SleepActivity(getRuntime(), seconds);
	}

	public final Activity sleep(RealtimeBoolean waitFor) throws RoboticsException {
		return new SleepActivity(getRuntime(), waitFor);
	}

	public final Activity ifElse(String name, RealtimeBoolean condition, Activity ifTrue, Activity ifFalse)
			throws RoboticsException {
		Activity sleep = sleep(RealtimeBoolean.FALSE);
		CommandRealtimeException conditionMet = new CommandRealtimeException("Condition true");
		CommandRealtimeException conditionNotMet = new CommandRealtimeException("Condition false");
		sleep.addErrorCondition(conditionMet, condition);
		sleep.addErrorCondition(conditionNotMet, condition.not());
		return Activities.conditional(name, x -> x.isFailedWhenActive() == conditionMet, ifTrue, ifFalse);
	}

}