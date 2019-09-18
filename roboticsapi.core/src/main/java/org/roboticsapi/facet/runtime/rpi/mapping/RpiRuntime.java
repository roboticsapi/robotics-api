/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.roboticsapi.core.AbstractRuntime;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandHandleOperation;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.CommandSchedule;
import org.roboticsapi.core.CommandSchedule.CommandScheduleStatus;
import org.roboticsapi.core.CommandSchedule.CommandScheduleStatusListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.facet.runtime.rpi.ControlCore;
import org.roboticsapi.facet.runtime.rpi.Fragment;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.NetResult;
import org.roboticsapi.facet.runtime.rpi.NetSynchronizationRule;
import org.roboticsapi.facet.runtime.rpi.NetSynchronizationRule.SynchronizationRuleStatus;
import org.roboticsapi.facet.runtime.rpi.RpiException;

public abstract class RpiRuntime extends AbstractRuntime {
	/**
	 * Interface for a hook executed during the mapping of a command
	 */
	public interface CommandMappingHook {
		/**
		 * Called for the net created for a command.
		 * 
		 * @param net net for the command
		 */
		void netHook(Fragment net);
	}

	public abstract ControlCore getControlCore();

	public abstract MapperRegistry getMapperRegistry();

	protected final NetHandle callHooksAndLoadFragment(Fragment fragment, String description, boolean realtime)
			throws RoboticsException {
		for (CommandMappingHook hook : mappingHooks)
			hook.netHook(fragment);
		return loadFragment(fragment, description, realtime);
	}

	protected abstract NetHandle loadFragment(Fragment fragment, String description, boolean realtime)
			throws RoboticsException;

	public abstract RealtimeDouble getOverrideSensor();

	private final List<CommandMappingHook> mappingHooks = new Vector<CommandMappingHook>();

	public void addCommandMappingHook(CommandMappingHook hook) {
		mappingHooks.add(hook);
	}

	public void removeCommandMappingHook(CommandMappingHook hook) {
		mappingHooks.remove(hook);
	}

	protected List<CommandMappingHook> getMappingHooks() {
		return mappingHooks;
	}

	@Override
	public void scheduleOperations(List<CommandResult> condition, List<CommandHandleOperation> operations,
			final CommandScheduleStatusListener listener) throws CommandException {
		List<NetResult> results = new ArrayList<NetResult>();
		List<NetHandle> startNets = new ArrayList<NetHandle>();
		List<NetHandle> cancelNets = new ArrayList<NetHandle>();
		List<NetHandle> stopNets = new ArrayList<NetHandle>();
		collectScheduleParameters(condition, operations, results, startNets, cancelNets, stopNets);
		try {
			NetSynchronizationRule.SynchronizationRuleListener srl = (listener == null) ? null
					: new NetSynchronizationRule.SynchronizationRuleListener() {
						@Override
						public void ruleStatusChanged(SynchronizationRuleStatus status) {
							listener.scheduleStatusChanged(getStatus(status));
						}
					};
			getControlCore().schedule(results, stopNets, cancelNets, startNets, srl);
		} catch (RpiException ex) {
			throw new CommandException("Failed to schedule operations", ex);
		}
	}

	@Override
	public CommandSchedule scheduleOperations(List<CommandResult> condition, List<CommandHandleOperation> operations)
			throws CommandException {
		List<NetResult> results = new ArrayList<NetResult>();
		List<NetHandle> startNets = new ArrayList<NetHandle>();
		List<NetHandle> cancelNets = new ArrayList<NetHandle>();
		List<NetHandle> stopNets = new ArrayList<NetHandle>();
		collectScheduleParameters(condition, operations, results, startNets, cancelNets, stopNets);
		try {
			final NetSynchronizationRule schedule = getControlCore().schedule(results, stopNets, cancelNets, startNets);
			if (schedule == null)
				return null;

			return new CommandSchedule() {
				@Override
				public void addStatusListener(final CommandScheduleStatusListener listener) {
					schedule.addStatusListener(new NetSynchronizationRule.SynchronizationRuleListener() {
						@Override
						public void ruleStatusChanged(SynchronizationRuleStatus status) {
							listener.scheduleStatusChanged(getStatus(status));
						}
					});
				}
			};
		} catch (RpiException ex) {
			return null;
		}
	}

	private void collectScheduleParameters(List<CommandResult> condition, List<CommandHandleOperation> operations,
			List<NetResult> results, List<NetHandle> startNets, List<NetHandle> cancelNets, List<NetHandle> stopNets) {
		for (CommandResult result : condition) {
			RpiCommandHandle handle = checkHandle(result.getCommand().getCommandHandle());
			if (handle == null) {
				throw new IllegalArgumentException("Illegal command result " + result.getCommand().getCommandHandle());
			}
			results.addAll(handle.getNetResults(result));
		}
		for (CommandHandleOperation op : operations) {
			RpiSchedulingOperation rpiOp = checkOperation(op);
			if (rpiOp == null)
				throw new IllegalArgumentException("Unsupported operation type " + op.getClass());
			if (rpiOp.getStartHandles() != null)
				startNets.addAll(rpiOp.getStartHandles());
			if (rpiOp.getCancelHandles() != null)
				cancelNets.addAll(rpiOp.getCancelHandles());
			if (rpiOp.getAbortHandles() != null)
				stopNets.addAll(rpiOp.getAbortHandles());
		}
	}

	private CommandScheduleStatus getStatus(NetSynchronizationRule.SynchronizationRuleStatus status) {
		switch (status) {
		case ACTIVE:
			return CommandScheduleStatus.SCHEDULED;
		case FIRED:
			return CommandScheduleStatus.STARTED;
		case FIREFAILED:
			return CommandScheduleStatus.FAILED;
		case INACTIVE:
			return CommandScheduleStatus.OBSOLETE;
		default:
			return CommandScheduleStatus.FAILED;
		}
	}

	private RpiSchedulingOperation checkOperation(CommandHandleOperation op) {
		if (op instanceof RpiSchedulingOperation) {
			return (RpiSchedulingOperation) op;
		} else {
			return null;
		}
	}

	protected RpiCommandHandle checkHandle(CommandHandle handle) {
		if (handle instanceof RpiCommandHandle)
			return (RpiCommandHandle) handle;
		else
			return null;
	}

	public abstract void addDeviceStatusListener(String deviceName, RpiDeviceStatusListener listener);

	public abstract void removeDeviceStatusListener(String deviceName, RpiDeviceStatusListener listener);

}