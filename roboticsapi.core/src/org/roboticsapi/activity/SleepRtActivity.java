/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.State;
import org.roboticsapi.core.eventhandler.ExceptionIgnorer;
import org.roboticsapi.core.exception.ActionCancelledException;
import org.roboticsapi.core.exception.RoboticsException;

public class SleepRtActivity extends AbstractRtActivity {

	private static final int FOREVER = -1;

	private final RoboticsRuntime runtime;
	private final double seconds;
	private final State[] takeOverAllowedConditions;

	public SleepRtActivity(RoboticsRuntime runtime, State... takeOverAllowedConditions) {
		this("Sleep(forever)", runtime, takeOverAllowedConditions);
	}

	public SleepRtActivity(RoboticsRuntime runtime, double seconds, State... takeOverAllowedConditions) {
		this("Sleep(" + seconds + "ms)", runtime, seconds, takeOverAllowedConditions);
	}

	public SleepRtActivity(String name, RoboticsRuntime runtime, State... takeOverAllowedConditions) {
		this(name, runtime, FOREVER, takeOverAllowedConditions);
	}

	public SleepRtActivity(String name, RoboticsRuntime runtime, double seconds, State... takeOverAllowedConditions) {
		super(name);
		this.runtime = runtime;
		this.seconds = seconds;
		this.takeOverAllowedConditions = takeOverAllowedConditions;
	}

	@Override
	public List<Device> getControlledDevices() {
		return new ArrayList<Device>();
	}

	@Override
	public Set<Device> prepare(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException {
		Command wait = seconds == -1 ? runtime.createWaitCommand(getName())
				: runtime.createWaitCommand(getName(), seconds);
		wait.addExceptionHandler(ActionCancelledException.class, new ExceptionIgnorer(), true);

		for (State condition : this.takeOverAllowedConditions) {
			wait.addTakeoverAllowedCondition(condition);
		}
		setCommand(wait, prevActivities);
		return null;
	}

	@Override
	public RoboticsRuntime getRuntime() {
		return runtime;
	}
}
