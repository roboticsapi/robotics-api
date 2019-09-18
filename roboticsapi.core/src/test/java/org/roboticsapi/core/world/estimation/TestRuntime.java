/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.estimation;

import java.util.List;
import java.util.Set;

import org.roboticsapi.core.AbstractRuntime;
import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandleOperation;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.CommandSchedule;
import org.roboticsapi.core.CommandSchedule.CommandScheduleStatusListener;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.WaitCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class TestRuntime extends AbstractRuntime {

	@Override
	public double getOverride() {
		return 0;
	}

	@Override
	public void setOverride(double newOverride) {
	}

	@Override
	public void addExtension(String extensionId) {
	}

	@Override
	public Set<String> getRegisteredExtensions() throws RoboticsException {
		return null;
	}

	@Override
	public Set<String> getAvailableExtensions() throws RoboticsException {
		return null;
	}

	@Override
	public boolean isAvailable(String extensionId) throws RoboticsException {
		return true;
	}

	@Override
	public WaitCommand createWaitCommand(String name) throws RoboticsException {
		throw new RoboticsException("Unimplemented");
	}

	@Override
	public WaitCommand createWaitCommand(String name, double duration) throws RoboticsException {
		throw new RoboticsException("Unimplemented");
	}

	@Override
	public WaitCommand createWaitCommand(String name, RealtimeBoolean waitFor) throws RoboticsException {
		throw new RoboticsException("Unimplemented");
	}

	@Override
	protected RuntimeCommand createRuntimeCommandInternal(ActuatorDriver driver, Action action,
			DeviceParameterBag parameters) throws RoboticsException {
		throw new RoboticsException("Unimplemented");
	}

	@Override
	public CommandSchedule scheduleOperations(List<CommandResult> condition, List<CommandHandleOperation> operations)
			throws CommandException {
		throw new CommandException("Unimplemented");
	}

	@Override
	public void scheduleOperations(List<CommandResult> condition, List<CommandHandleOperation> operations,
			CommandScheduleStatusListener listener) throws CommandException {
		throw new CommandException("Unimplemented");
	}

	@Override
	public <T> T getRealtimeValue(RealtimeValue<T> value) throws RealtimeValueReadException {
		return null;
	}

	@Override
	public void checkBlockEventHandlerThread() throws RoboticsException {
	}

}