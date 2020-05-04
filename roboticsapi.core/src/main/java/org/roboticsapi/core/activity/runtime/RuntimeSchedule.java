/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandHandleOperation;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.CommandSchedule.CommandScheduleStatus;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.activity.ActivityHandle;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.ActivityResults;
import org.roboticsapi.core.activity.ActivitySchedule;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.RAPILogger;

public class RuntimeSchedule extends ActivitySchedule {

	private final List<Command> startCommands;

	public RuntimeSchedule(ActivityResult result, ActivityHandle activity, ActivityResults results,
			List<Command> startCommands) {
		super(result == null
				? new RuntimeResult("Fresh start", activity.getActivity().getDevices(), true, null, true,
						activity.getActivity().getPropertyProviders(), new HashMap<>(), Arrays.asList())
				: result, activity, results);
		this.startCommands = startCommands;
		addStatusListener(status -> {
			if (status == ActivitySchedule.Status.IMPOSSIBLE)
				doUnload();
		});
		activity.addStatusListener(status -> {
			if (status == ActivityHandle.Status.FAILED)
				doUnload();
//			if(status == ActivityHandle.Status.COMPLETED doUnload(); //FIXME: This causes following activities to block on RCC if they changed from READY to SCHEDULED after unloading of dependent net
		});
	}

	public RuntimeSchedule(ActivityResult result, ActivityHandle activity, ActivityResults results,
			Command startCommand) {
		this(result, activity, results, Arrays.asList(startCommand));
	}

	protected void doUnload() {
		for (Command c : startCommands) {
			try {
				c.unload();
			} catch (RoboticsException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	@Override
	protected void doLoad() {
		for (Command c : startCommands) {
			try {
				c.load();
			} catch (RoboticsException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	@Override
	protected void doActivate() {
		try {
			RuntimeResult result = RuntimeResult.class.cast(getResult());
			if (result == null) {
				throw new IllegalArgumentException("Predecessor result must be of type RuntimeResult");
			}

			List<CommandHandleOperation> startOps = new ArrayList<>();
			RoboticsRuntime runtime = null;
			for (Command c : startCommands) {
				runtime = c.getRuntime();
				CommandHandle handle = c.load();
				startOps.add(handle.getStartOperation());
			}
			for (CommandResult cr : result.getCommandResults()) {
				startOps.add(cr.getCommand().getCommandHandle().getAbortOperation());
			}

			runtime.scheduleOperations(result.getCommandResults(), startOps, status -> {
				if (status == CommandScheduleStatus.STARTED) {
					markTaken();
					result.updateStatus(ActivityResult.Status.ACTIVE);

					for (CommandResult cr : result.getCommandResults()) {
						try {
							cr.getCommand().unload();
						} catch (CommandException e) {
							// ignore
						}
					}
				}
			});

			getResults().provide(r -> {
				if (r instanceof RuntimeResult) {
					((RuntimeResult) r).registerStatusListener();
				}
			});

		} catch (RoboticsException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void cancel() {
		List<CommandHandleOperation> cancelOperations = new ArrayList<>();
		RoboticsRuntime runtime = null;
		for (Command c : startCommands) {
			runtime = c.getRuntime();
			cancelOperations.add(c.getCommandHandle().getCancelOperation());
		}
		try {
			runtime.executeOperations(cancelOperations);
		} catch (CommandException e) {
			RAPILogger.logException(this, e);
		}
	}

	@Override
	public ActivitySchedule withParallel(ActivityHandle composite, ActivitySchedule otherSchedule)
			throws RoboticsException {
		if (!(otherSchedule instanceof RuntimeSchedule)) {
			throw new IllegalArgumentException();
		}

		RuntimeSchedule other = (RuntimeSchedule) otherSchedule;

		Set<Device> thisDevices = getActivityHandle().getActivity().getDevices();
		Set<Device> otherDevices = other.getActivityHandle().getActivity().getDevices();
		for (Device device : thisDevices)
			if (otherDevices.contains(device))
				throw new IllegalArgumentException(
						"Cannot combine schedules with devices " + thisDevices + ", " + otherDevices);

		ActivityResult result = getResult().withMetadataFor(thisDevices)
				.and(other.getResult().withMetadataFor(otherDevices));
		ActivityResults results = getResults().withMetadataFor(thisDevices)
				.cross(other.getResults().withMetadataFor(otherDevices));

		List<Command> startCommands = new ArrayList<>();
		startCommands.addAll(this.startCommands);
		startCommands.addAll(other.startCommands);

		RuntimeSchedule ret = new RuntimeSchedule(result, composite, results, startCommands);
		for (ActivitySchedule dependency : dependencies)
			ret.addDependency(dependency);
		for (ActivitySchedule dependency : other.dependencies)
			ret.addDependency(dependency);
		ret.addStatusListener(status -> {
			setStatus(status);
			other.setStatus(status);
		});
		return ret;
	}

	@Override
	public ActivitySchedule withResults(ActivityHandle composite, ActivityResults results) throws RoboticsException {
		RuntimeSchedule ret = new RuntimeSchedule(getResult(), composite, results, startCommands);
		for (ActivitySchedule dependency : dependencies)
			ret.addDependency(dependency);
		ret.addStatusListener(status -> {
			setStatus(status);
		});
		return ret;
	}

}
