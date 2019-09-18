/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.EventHandler;
import org.roboticsapi.core.Observer;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.Assignment;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.RpiException;

public abstract class RpiRuntimeCommand extends RuntimeCommand {

	protected abstract BasicCommandHandle createCommandHandle(NetHandle handle,
			Map<CommandResult, NetcommValue> resultMap) throws CommandException;

	public RpiRuntimeCommand(RpiRuntime runtime, Action action, ActuatorDriver device, DeviceParameterBag parameters)
			throws RoboticsException {
		super(action, device, parameters);
	}

	@Override
	public RpiRuntime getRuntime() {
		return (RpiRuntime) super.getRuntime();
	}

	@Override
	protected CommandHandle createHandle() throws RoboticsException {
		final CommandFragment ret = new CommandFragment(this);

		ret.addRealtimeValueSource(getRuntime().getMapperRegistry());
		ret.addRealtimeValueAliasFactory(
				new CommandStateMapper<CancelRealtimeBoolean>(CancelRealtimeBoolean.class, this, ret.getCancel()));
		ret.addRealtimeValueAliasFactory(
				new TypedRealtimeValueAliasFactory<Double, Command.ExecutionTimeRealtimeDouble>(
						ExecutionTimeRealtimeDouble.class) {
					@Override
					protected RealtimeValue<Double> createAlias(ExecutionTimeRealtimeDouble value) {
						if (value.getScope() == RpiRuntimeCommand.this)
							return ret.getTime();
						else
							return null;
					}
				});

		// map action
		ActionResult actionResult = getRuntime().getMapperRegistry().mapAction(getAction(), getDeviceParameters(),
				ret.getCancel(), getRuntime().getOverrideSensor(), ret.getTime(), getPlans());
		ret.addRealtimeValueSource(actionResult);

		// map driver
		RealtimeValueConsumerFragment driver = getRuntime().getMapperRegistry().mapDriver(getDevice(), actionResult,
				getDeviceParameters(), ret.getCancel(), getRuntime().getOverrideSensor(), ret.getTime());
		ret.addConsumerFragment(driver);

		for (Observer<?> o : getObservers())
			ret.addObserver(o);
		for (Assignment<?> a : getAssignments())
			ret.addAssignment(a);
		for (EventHandler handler : getEventHandlers())
			ret.addEventHandler(handler);
		for (CommandResult result : getCommandResults())
			ret.addCommandResult(getResultConditions(result), result);
		ret.setTermination(RealtimeBoolean.FALSE);

		CommandResult cancelResult = new CommandResult("Inner Cancel", this, null, false, false);
		ret.addCommandResult(getCancelConditions(), cancelResult);

		NetHandle netHandle = getRuntime().callHooksAndLoadFragment(ret, getName(), true);
		Map<CommandResult, NetcommValue> resultMap = ret.getResultMap();

		BasicCommandHandle handle = createCommandHandle(netHandle, resultMap);

		try {
			getRuntime().getControlCore().schedule(handle.getNetResults(cancelResult), new ArrayList<NetHandle>(),
					asList(netHandle), new ArrayList<NetHandle>(), null);

			for (CommandResult result : getCommandResults()) {
				if (result.isCompleted())
					getRuntime().getControlCore().schedule(handle.getNetResults(result), asList(netHandle),
							new ArrayList<NetHandle>(), new ArrayList<NetHandle>(), null);
			}

			setCommandHandle(handle);
			return handle;

		} catch (RpiException e) {
			throw new CommandException("Failed to load command", e);
		}
	}

	private <T> List<T> asList(T handle) {
		ArrayList<T> ret = new ArrayList<T>();
		ret.add(handle);
		return ret;
	}
}
