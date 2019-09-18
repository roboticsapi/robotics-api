/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.mockclass;

import java.util.List;
import java.util.Set;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandFilter;
import org.roboticsapi.core.CommandHandleOperation;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.CommandSchedule;
import org.roboticsapi.core.CommandSchedule.CommandScheduleStatusListener;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListenerRegistration;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.WaitCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class MockRoboticsRuntime extends RoboticsRuntime {

	@Override
	public double getOverride() {
		return 0;
	}

	@Override
	public void setOverride(double newOverride) {
	}

	@Override
	public void addExtension(String extensionId) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getRegisteredExtensions() throws RoboticsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAvailableExtensions() throws RoboticsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAvailable(String extensionId) throws RoboticsException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public WaitCommand createWaitCommand(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WaitCommand createWaitCommand(String name, double duration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WaitCommand createWaitCommand(String name, RealtimeBoolean waitFor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected RuntimeCommand createRuntimeCommandInternal(ActuatorDriver actuator, Action action,
			DeviceParameterBag parameters) throws RoboticsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommandSchedule scheduleOperations(List<CommandResult> condition, List<CommandHandleOperation> operations)
			throws CommandException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void scheduleOperations(List<CommandResult> condition, List<CommandHandleOperation> operations,
			CommandScheduleStatusListener listener) throws CommandException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCommandHook(CommandHook hook) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeCommandHook(CommandHook hook) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<CommandHook> getCommandHooks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCommandFilter(CommandFilter filter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeCommandFilter(CommandFilter filter) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRelatimeValueListeners(List<RealtimeValueListenerRegistration<?>> listeners)
			throws RoboticsException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T getRealtimeValue(RealtimeValue<T> value) throws RealtimeValueReadException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeRealtimeValueListeners(List<RealtimeValueListenerRegistration<?>> listeners)
			throws RoboticsException {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkBlockEventHandlerThread() throws RoboticsException {
		// TODO Auto-generated method stub

	}

}
