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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.CommandResultListener;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.activity.ActivityProperty;
import org.roboticsapi.core.activity.ActivityPropertyProvider;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.exception.RoboticsException;

public class RuntimeResult extends ActivityResult {

	private final List<CommandResult> commandResults;

	public RuntimeResult(String name, Device device, boolean isCompleted, RoboticsException isFailed,
			boolean allowsFreshStart, List<ActivityPropertyProvider<?>> metadataProviders,
			Map<Class<? extends ActivityProperty>, ActivityProperty> metadata, List<CommandResult> commandResults) {
		super(name, device, isCompleted, isFailed, allowsFreshStart, metadataProviders, metadata);
		this.commandResults = commandResults;
	}

	public RuntimeResult(String name, Device device, boolean isCompleted, RoboticsException isFailed,
			boolean allowsFreshStart, List<ActivityPropertyProvider<?>> metadataProviders,
			Map<Class<? extends ActivityProperty>, ActivityProperty> metadata, CommandResult commandResult) {
		super(name, device, isCompleted, isFailed, allowsFreshStart, metadataProviders, metadata);
		this.commandResults = Arrays.asList(commandResult);
	}

	public RuntimeResult(String name, Set<Device> devices, boolean isCompleted, RoboticsException isFailed,
			boolean allowsFreshStart, List<ActivityPropertyProvider<?>> metadataProviders,
			Map<Device, Map<Class<? extends ActivityProperty>, ActivityProperty>> metadata,
			List<CommandResult> commandResults) {
		super(name, devices, isCompleted, isFailed, allowsFreshStart, metadataProviders, metadata);
		this.commandResults = commandResults;
	}

	public RuntimeResult(String name, Set<Device> devices, boolean isCompleted, RoboticsException isFailed,
			boolean allowsFreshStart, List<ActivityPropertyProvider<?>> metadataProviders,
			Map<Device, Map<Class<? extends ActivityProperty>, ActivityProperty>> metadata,
			CommandResult commandResult) {
		super(name, devices, isCompleted, isFailed, allowsFreshStart, metadataProviders, metadata);
		this.commandResults = Arrays.asList(commandResult);
	}

	@Override
	public ActivityResult and(ActivityResult other) {
		if (!(other instanceof RuntimeResult)) {
			throw new IllegalArgumentException("RuntimeResults can only be combined with RuntimeResults");
		}
		Set<Device> devices = new HashSet<>();
		List<CommandResult> commandResults = new ArrayList<>();
		List<ActivityPropertyProvider<?>> metadataProviders = new ArrayList<>();
		Map<Device, Map<Class<? extends ActivityProperty>, ActivityProperty>> metadata = new HashMap<>();
		devices.addAll(getDevices());
		devices.addAll(other.getDevices());
		metadata.putAll(this.getMetadata());
		metadata.putAll(other.getMetadata());
		metadataProviders.addAll(this.getMetadataProviders());
		metadataProviders.addAll(other.getMetadataProviders());
		commandResults.addAll(getCommandResults());
		commandResults.addAll(((RuntimeResult) other).getCommandResults());
		RuntimeResult ret = new RuntimeResult(getName() + " + " + other.getName(), devices,
				isCompletedWhenActive() && other.isCompletedWhenActive(),
				isFailedWhenActive() != null ? isFailedWhenActive() : other.isFailedWhenActive(),
				allowsFreshStartWhenActive() && other.allowsFreshStartWhenActive(), metadataProviders, metadata,
				commandResults);

		Consumer<Status> deriveStatus = status -> {
			if (getStatus() == Status.ACTIVE && other.getStatus() == Status.ACTIVE) {
				ret.updateStatus(Status.ACTIVE);
			} else if (getStatus() == Status.IMPOSSIBLE || other.getStatus() == Status.IMPOSSIBLE) {
				ret.updateStatus(Status.IMPOSSIBLE);
			} else {
				ret.updateStatus(Status.POSSIBLE);
			}
		};
		observeStatus(deriveStatus);
		other.observeStatus(deriveStatus);
		return ret;
	}

	public List<CommandResult> getCommandResults() {
		return commandResults;
	}

	@Override
	protected void updateStatus(Status status) {
		super.updateStatus(status);
	}

	private Set<CommandResult> activeResults = null;

	public void registerStatusListener() {
		if (activeResults != null) {
			return;
		}
		activeResults = new HashSet<>();

		CommandResultListener statusHandler = (result, status) -> {
			if (!commandResults.contains(result) || getStatus() == Status.IMPOSSIBLE) {
				return;
			}
			if (status == CommandResult.Status.ACTIVE) {
				activeResults.add(result);
			} else if (status == CommandResult.Status.POSSIBLE) {
				activeResults.remove(result);
			} else {
				updateStatus(Status.IMPOSSIBLE);
			}
			if (activeResults.containsAll(commandResults)) {
				updateStatus(Status.ACTIVE);
			} else {
				updateStatus(Status.POSSIBLE);
			}
		};

		Set<CommandHandle> handles = new HashSet<>();

		for (CommandResult result : commandResults) {
			handles.add(result.getCommand().getCommandHandle());
		}
		for (CommandHandle handle : handles) {
			handle.addCommandResultListener(statusHandler);
		}
	}

}
