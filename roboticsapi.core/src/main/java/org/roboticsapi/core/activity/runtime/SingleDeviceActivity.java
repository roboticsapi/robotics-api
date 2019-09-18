/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.CommandRuntimeException;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.activity.AbstractActivity;
import org.roboticsapi.core.activity.ActivityHandle;
import org.roboticsapi.core.activity.ActivityProperty;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.ActivityResultContainer;
import org.roboticsapi.core.activity.ActivitySchedule;
import org.roboticsapi.core.exception.RoboticsException;

public abstract class SingleDeviceActivity extends AbstractActivity {

	private ActuatorDriver driver;

	public SingleDeviceActivity(String name, ActuatorDriver driver) {
		super(name, driver.getDevice());
		this.driver = driver;
	}

	/**
	 * Helper method to create a set out of a given array of {@link ActivityResult}s
	 *
	 * @param results {@link ActivityResult}s for the set
	 * @return {@link Set} containing the {@link ActivityResult}s
	 */
	protected Set<ActivityResult> activityResultSet(ActivityResult... results) {
		return new HashSet<ActivityResult>(Arrays.asList(results));
	}

	protected Set<ActivityResult> commandErrorActivityResultSet(Command command) {
		Set<ActivityResult> ret = new HashSet<>();
		for (CommandResult er : command.getExceptionResults()) {
			ret.add(errorResult(er, er.getException()));
		}
		return ret;
	}

	@Override
	public ActivityHandle createHandle() throws RoboticsException {
		return new ActivityHandle(this) {
			@Override
			public ActivitySchedule prepare(ActivityResult result, StackTraceElement[] errorStack)
					throws RoboticsException {
				return prepareForResult(this, result, errorStack);
			}
		};
	}

	public ActivitySchedule prepareForResult(ActivityHandle handle, ActivityResult result,
			StackTraceElement[] errorStack) throws RoboticsException {
		if (!driver.isPresent()) {
			throw new RoboticsException("The device " + driver.getDevice() + " is not present.");
		}
		if (result != null && result.isFailedWhenActive() != null) {
			return null;
		}
		Command cmd = getCommandForResult(result);
		if (cmd == null) {
			return null;
		}
		ActivityResultContainer results = new ActivityResultContainer();

		// handle expected results
		for (ActivityResult res : getResultsForCommand(cmd))
			results.addResult(res);

		// seal the command to make sure all results are defined
		cmd.seal(errorStack);

		// handle errors of command as activity errors
		for (ActivityResult res : commandErrorActivityResultSet(cmd))
			results.addResult(res);

		// check that all command termination reasons are mapped to activity results
		List<CommandResult> unhandledResults = cmd.getCompletionResults();
		results.provide(rr -> {
			if (rr instanceof RuntimeResult)
				unhandledResults.removeAll(((RuntimeResult) rr).getCommandResults());
		});
		if (!unhandledResults.isEmpty())
			throw new RoboticsException("Unhandled completion command result(s): " + unhandledResults);

		return new RuntimeSchedule(result, handle, results, cmd);
	}

	protected RuntimeCommand createRuntimeCommand(Action action, DeviceParameterBag parameters)
			throws RoboticsException {
		return getRuntime().createRuntimeCommand(driver, action, parameters);
	}

	protected RuntimeCommand createRuntimeCommand(Action action) throws RoboticsException {
		return getRuntime().createRuntimeCommand(driver, action);
	}

	protected abstract Set<ActivityResult> getResultsForCommand(Command command) throws RoboticsException;

	protected abstract Command getCommandForResult(ActivityResult result) throws RoboticsException;

	public ActuatorDriver getDriver() {
		return driver;
	}

	public Device getDevice() {
		return driver.getDevice();
	}

	public final RoboticsRuntime getRuntime() {
		return driver.getRuntime();
	}

	private Map<Class<? extends ActivityProperty>, ActivityProperty> props(ActivityProperty... props) {
		Map<Class<? extends ActivityProperty>, ActivityProperty> bag = new HashMap<>();
		for (ActivityProperty prop : props) {
			if (prop != null) {
				bag.put(prop.getClass(), prop);
			}
		}
		return bag;
	}

	protected ActivityResult maintainingResult(CommandResult result, ActivityProperty... props) {
		return new RuntimeResult(this + " maintaining", this.getDevice(), true, null, false, getPropertyProviders(),
				props(props), result);
	}

	protected ActivityResult completionResult(CommandResult result, ActivityProperty... props) {
		return new RuntimeResult(this + " completed", this.getDevice(), true, null, true, getPropertyProviders(),
				props(props), result);
	}

	protected ActivityResult cancelResult(CommandResult result, ActivityProperty... props) {
		return new RuntimeResult(this + " cancelled", this.getDevice(), true, null, true, getPropertyProviders(),
				props(props), result);
	}

	protected ActivityResult errorResult(CommandResult result, CommandRuntimeException error,
			ActivityProperty... props) {
		return new RuntimeResult(this + " " + result.getName(), this.getDevice(), true, error, true,
				getPropertyProviders(), props(props), result);
	}

	protected ActivityResult takeoverResult(CommandResult result, ActivityProperty... props) {
		return new RuntimeResult(this + " " + result, this.getDevice(), false, null, false, getPropertyProviders(),
				props(props), result);
	}
}
