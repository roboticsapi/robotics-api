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

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.EventHandler;
import org.roboticsapi.core.Observer;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.WaitCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.Assignment;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.RpiException;

public abstract class RpiWaitCommand extends WaitCommand {

	protected abstract BasicCommandHandle createCommandHandle(NetHandle handle,
			Map<CommandResult, NetcommValue> resultMap) throws CommandException;

	public RpiWaitCommand(RpiRuntime runtime, double duration) {
		super(runtime, duration);
	}

	@Override
	public RpiRuntime getRuntime() {
		return (RpiRuntime) super.getRuntime();
	}

	public RpiWaitCommand(RpiRuntime runtime, RealtimeBoolean waitFor) {
		super(runtime, waitFor);
	}

	public RpiWaitCommand(RpiRuntime runtime) {
		super(runtime);
	}

	public RpiWaitCommand(String name, RpiRuntime runtime) {
		super(name, runtime);
	}

	protected RpiWaitCommand(String name, RpiRuntime runtime, double duration) {
		super(name, runtime, duration);
	}

	protected RpiWaitCommand(String name, RpiRuntime runtime, RealtimeBoolean waitFor) {
		super(name, runtime, waitFor);
	}

	@Override
	protected CommandHandle createHandle() throws RoboticsException {
		final CommandFragment ret = new CommandFragment(this);

		for (RealtimeValueFragmentFactory factory : getRuntime().getMapperRegistry()
				.getRealtimeValueFragmentFactories())
			ret.addRealtimeValueFragmentFactory(factory);
		for (RealtimeValueAliasFactory factory : getRuntime().getMapperRegistry().getRealtimeValueAliasFactories())
			ret.addRealtimeValueAliasFactory(factory);

		ret.addRealtimeValueAliasFactory(
				new CommandStateMapper<CancelRealtimeBoolean>(CancelRealtimeBoolean.class, this, ret.getCancel()));
		ret.addRealtimeValueAliasFactory(
				new TypedRealtimeValueAliasFactory<Double, Command.ExecutionTimeRealtimeDouble>(
						ExecutionTimeRealtimeDouble.class) {
					@Override
					protected RealtimeValue<Double> createAlias(ExecutionTimeRealtimeDouble value) {
						if (value.getScope() == RpiWaitCommand.this)
							return ret.getTime();
						else
							return null;
					}
				});

		for (Observer<?> o : getObservers())
			ret.addObserver(o);
		for (Assignment<?> a : getAssignments())
			ret.addAssignment(a);
		for (EventHandler handler : getEventHandlers())
			ret.addEventHandler(handler);
		for (CommandResult result : getCommandResults())
			ret.addCommandResult(getResultConditions(result), result);
		ret.setTermination(RealtimeBoolean.FALSE);

		CommandResult cancelResult = new CommandResult("Cancel yourself", this, null, false, false);
		ret.addCommandResult(getCancelConditions(), cancelResult);

		boolean realtime = false;
		if (!getAssignments().isEmpty())
			realtime = true;
		NetHandle netHandle = getRuntime().callHooksAndLoadFragment(ret, getName(), realtime);
		Map<CommandResult, NetcommValue> resultMap = ret.getResultMap();

		BasicCommandHandle handle = createCommandHandle(netHandle, resultMap);
		try {
			setCommandHandle(handle);
			getRuntime().getControlCore().schedule(handle.getNetResults(cancelResult), new ArrayList<NetHandle>(),
					asList(netHandle), new ArrayList<NetHandle>(), null);

			for (CommandResult result : getCommandResults()) {
				if (result.isCompleted())
					getRuntime().getControlCore().schedule(handle.getNetResults(result), asList(netHandle),
							new ArrayList<NetHandle>(), new ArrayList<NetHandle>(), null);
			}

			return handle;

		} catch (RpiException e) {
			throw new CommandException("Could not load command", e);
		}
	}

	private <T> List<T> asList(T handle) {
		ArrayList<T> ret = new ArrayList<T>();
		ret.add(handle);
		return ret;
	}
}
