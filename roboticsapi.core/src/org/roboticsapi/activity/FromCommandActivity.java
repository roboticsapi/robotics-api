/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.State;
import org.roboticsapi.core.exception.RoboticsException;

public class FromCommandActivity extends AbstractRtActivity {

	private final Command command;
	private final Device[] controlled;
	private final Device[] takenOver;
	private final State maintainingState;

	public FromCommandActivity(String name, Command command, State maintainingState, Device[] takenOver,
			Device[] controlled) {
		super(name);
		this.command = command;
		this.maintainingState = maintainingState;
		this.takenOver = takenOver;
		this.controlled = controlled;
	}

	public FromCommandActivity(String name, Command command, Device[] takenOver, Device[] controlled) {
		this(name, command, null, takenOver, controlled);
	}

	public FromCommandActivity(String name, Command command, State maintainingState, Device... controlled) {
		this(name, command, maintainingState, null, controlled);
	}

	public FromCommandActivity(Command command, State maintainingState, Device... devices) {
		this("", command, maintainingState, null, devices);
	}

	public FromCommandActivity(String name, Command command, Device... devices) {
		this(name, command, null, null, devices);

	}

	public FromCommandActivity(Command command, Device... devices) {
		this(command, null, devices);

	}

	@Override
	public List<Device> getControlledDevices() {
		return Arrays.asList(controlled);
	}

	@Override
	public Set<Device> prepare(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException {
		setCommand(command, prevActivities, maintainingState);
		return takenOver == null ? null : getTakenOverAsSet();
	}

	private Set<Device> getTakenOverAsSet() {
		HashSet<Device> set = new HashSet<Device>();
		for (Device d : takenOver) {
			set.add(d);
		}

		return set;
	}

	@Override
	public RoboticsRuntime getRuntime() {
		return command.getRuntime();
	}

}
