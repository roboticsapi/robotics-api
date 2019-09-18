/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.activity.ActivityHandle.Status;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.runtime.CommandRealtimeException;

public abstract class AbstractActivity implements Activity {

	/** The cancel states. */
	private final List<RealtimeBoolean> cancelConditions = new ArrayList<RealtimeBoolean>();

	/** The exceptions caused by other exceptions . */
	private final Map<CommandRealtimeException, List<Class<? extends CommandRealtimeException>>> exceptionErrors = new HashMap<>();

	/** The exceptions caused by RealtimeBooleans . */
	private final Map<CommandRealtimeException, List<RealtimeBoolean>> stateErrors = new HashMap<>();

	/** The ignored exceptions. */
	private final List<Class<? extends CommandRealtimeException>> ignoredErrors = new ArrayList<>();

	/** The property providers */
	private final List<ActivityPropertyProvider<?>> propertyProviders = new ArrayList<ActivityPropertyProvider<?>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.core.activity.Activity#execute()
	 */
	@Override
	public void execute() throws RoboticsException {
		beginExecute(createStackTrace()).endExecute();
	}

	private StackTraceElement[] createStackTrace() {
		StackTraceElement[] stackTrace = new Exception().getStackTrace();
		StackTraceElement[] newStackTrace = new StackTraceElement[stackTrace.length - 2];
		for (int i = 0; i < newStackTrace.length; i++)
			newStackTrace[i] = stackTrace[i + stackTrace.length - newStackTrace.length];
		return newStackTrace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.core.activity.Activity#beginExecute()
	 */
	@Override
	public ActivityHandle beginExecute() throws RoboticsException {
		return beginExecute(createStackTrace());
	}

	private ActivityHandle beginExecute(StackTraceElement[] errorStack) throws RoboticsException {
		ActivityResults results = null;
		Set<ActivityResults> seenResults = new HashSet<>();
		for (Device device : getDevices()) {
			ActivityResults deviceResults = ActivityScheduler.getInstance().getResults(device);
			if (seenResults.add(deviceResults)) {
				if (results == null) {
					results = deviceResults;
				} else if (deviceResults != null) {
					results = results.cross(deviceResults);
				}
			}
		}

		ActivityHandle ret = createHandle();
		ret.bind(results, errorStack);

		ret.waitForStatus(s -> s != Status.BOUND && s != Status.LOADED);

		if (ret.getException() != null)
			throw ret.getException();
		return ret;
	}

	private final Set<Device> devices;
	private final String name;

	protected AbstractActivity(String name, Device... devices) {
		this.name = name;
		this.devices = new HashSet<>(Arrays.asList(devices));
	}

	protected AbstractActivity(String name, Activity... activities) {
		this.name = name;
		this.devices = new HashSet<>();
		for (Activity activity : activities) {
			for (Device d : activity.getDevices()) {
				this.devices.add(d);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.core.activity.Activity#getDevices()
	 */
	@Override
	public Set<Device> getDevices() {
		return devices;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.core.activity.Activity#createHandle()
	 */
	@Override
	public abstract ActivityHandle createHandle() throws RoboticsException;

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void addCancelCondition(RealtimeBoolean state) {
		cancelConditions.add(state);
	}

	@Override
	public void addErrorCondition(CommandRealtimeException exception, Class<? extends CommandRealtimeException> cause) {
		if (exceptionErrors.get(exception) == null)
			exceptionErrors.put(exception, new ArrayList<>());
		exceptionErrors.get(exception).add(cause);
	}

	@Override
	public void addErrorCondition(CommandRealtimeException exception, RealtimeBoolean cause) {
		if (stateErrors.get(exception) == null)
			stateErrors.put(exception, new ArrayList<>());
		stateErrors.get(exception).add(cause);
	}

	@Override
	public void ignoreError(Class<? extends CommandRealtimeException> type) {
		ignoredErrors.add(type);
	}

	public void addPropertyProvider(ActivityPropertyProvider<?> propertyProvider) {
		propertyProviders.add(propertyProvider);
	}

	public List<Class<? extends CommandRealtimeException>> getIgnoredErrors() {
		return ignoredErrors;
	}

	@Override
	public Map<CommandRealtimeException, List<Class<? extends CommandRealtimeException>>> getExceptionErrorConditions() {
		return exceptionErrors;
	}

	@Override
	public Map<CommandRealtimeException, List<RealtimeBoolean>> getRealtimeBooleanErrorConditions() {
		return stateErrors;
	}

	public List<RealtimeBoolean> getCancelConditions() {
		return cancelConditions;
	}

	public List<ActivityPropertyProvider<?>> getPropertyProviders() {
		return propertyProviders;
	}

}
