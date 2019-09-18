/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity.runtime;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.activity.AbstractActivity;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActivityHandle;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.ActivityResultContainer;
import org.roboticsapi.core.activity.ActivitySchedule;
import org.roboticsapi.core.exception.RoboticsException;

public class FromCommandActivity extends AbstractActivity {

	private final CommandSupplier command;
	private final Device[] devices;

	public FromCommandActivity(String name, CommandSupplier command, Device... devices) {
		super(name, devices);
		this.command = command;
		this.devices = devices;

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

	/**
	 * Helper method to create a set out of a given array of {@link Activity}s
	 *
	 * @param results {@link Activity}s for the set
	 * @return {@link Set} containing the {@link Activity}s
	 */
	protected Set<Activity> activitySet(Activity... results) {
		return new HashSet<Activity>(Arrays.asList(results));
	}

	/**
	 * Helper method to create a set out of a given array of
	 * {@link ActivitySchedule}s
	 *
	 * @param results {@link ActivitySchedule}s for the set
	 * @return {@link Set} containing the {@link ActivitySchedule}s
	 */
	protected Set<ActivitySchedule> scheduleSet(ActivitySchedule... results) {
		return new HashSet<ActivitySchedule>(Arrays.asList(results));
	}

	/**
	 * Helper method to create a set out of a given array of {@link CommandResult}s
	 *
	 * @param results {@link CommandResult}s for the set
	 * @return {@link Set} containing the {@link CommandResult}s
	 */
	protected Set<CommandResult> commandResultSet(CommandResult... results) {
		return new HashSet<CommandResult>(Arrays.asList(results));
	}

	/**
	 * Helper method to create a set out of a given {@link Collection} of
	 * {@link CommandResult}s
	 *
	 * @param results {@link CommandResult}s for the set
	 * @return {@link Set} containing the {@link CommandResult}s
	 */
	protected Set<CommandResult> commandResultSet(Collection<CommandResult> results) {
		return new HashSet<CommandResult>(results);
	}

	/**
	 * Helper method to create a set out of a given array of {@link Device}s
	 *
	 * @param devices {@link Device}s for the set
	 * @return {@link Set} containing the {@link Device}s
	 */
	protected Set<Device> deviceSet(Device... devices) {
		return new HashSet<Device>(Arrays.asList(devices));
	}

	protected ActivityResultContainer commandErrorActivityResultSet(Command command) throws RoboticsException {
		ActivityResultContainer ret = new ActivityResultContainer();
		for (CommandResult er : command.getExceptionResults()) {
			ret.addResult(new RuntimeResult(this + " " + er.getName(), getDevices(), true, er.getException(), true,
					getPropertyProviders(), new HashMap<>(), er));
		}
		return ret;
	}

	public FromCommandActivity(CommandSupplier command, Device... devices) throws RoboticsException {
		this(command.get().getName(), command, devices);
	}

	@Override
	public Set<Device> getDevices() {
		return deviceSet(devices);
	}

	@Override
	public ActivityHandle createHandle() throws RoboticsException {
		Command command = this.command.get();
		return new ActivityHandle(this) {
			@Override
			public ActivitySchedule prepare(ActivityResult result, StackTraceElement[] errorStack)
					throws RoboticsException {
				command.seal(errorStack);
				ActivityResultContainer results = commandErrorActivityResultSet(command);
				for (CommandResult complete : command.getCompletionResults()) {
					results.addResult(new RuntimeResult(this + " completed [" + complete + "]", deviceSet(devices),
							true, null, true, getPropertyProviders(), new HashMap<>(), complete));
				}
				return new RuntimeSchedule(result, this, results, command);
			}
		};
	}

	public interface CommandSupplier {
		Command get() throws RoboticsException;
	}

}
