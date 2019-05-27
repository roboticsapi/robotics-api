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
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.eventhandler.ExceptionThrower;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;

public class ErrorRtActivity extends AbstractRtActivity {

	private final RoboticsRuntime runtime;
	private final CommandRealtimeException exception;

	public ErrorRtActivity(String name, RoboticsRuntime runtime, CommandRealtimeException exception) {
		super(name);
		this.runtime = runtime;
		this.exception = exception;
	}

	public ErrorRtActivity(RoboticsRuntime runtime, CommandRealtimeException exception) {
		this("Error", runtime, exception);
	}

	@Override
	public List<Device> getControlledDevices() {
		return new ArrayList<Device>();
	}

	@Override
	public Set<Device> prepare(Map<Device, Activity> prevActivity)
			throws RoboticsException, ActivityNotCompletedException {
		Command wait = runtime.createWaitCommand();
		wait.addStateFirstEnteredHandler(wait.getStartedState(), new CommandCanceller());
		wait.addStateFirstEnteredHandler(wait.getStartedState(), new ExceptionThrower(exception));
		setCommand(wait, prevActivity);
		return null;
	}

	@Override
	public RoboticsRuntime getRuntime() {
		return runtime;
	}
}
