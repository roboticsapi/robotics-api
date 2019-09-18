/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.activity.ActivityProperty;
import org.roboticsapi.core.activity.ActivityPropertyProvider;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.exception.RoboticsException;

public class SimpleResult extends ActivityResult {

	public SimpleResult(String name, Device device, boolean isCompleted, RoboticsException isFailed,
			boolean allowsFreshStart, List<ActivityPropertyProvider<?>> metadataProviders,
			Map<Class<? extends ActivityProperty>, ActivityProperty> metadata) {
		super(name, device, isCompleted, isFailed, allowsFreshStart, metadataProviders, metadata);
	}

	public SimpleResult(String name, Set<Device> devices, boolean isCompleted, RoboticsException isFailed,
			boolean allowsFreshStart, List<ActivityPropertyProvider<?>> metadataProviders,
			Map<Device, Map<Class<? extends ActivityProperty>, ActivityProperty>> metadata) {
		super(name, devices, isCompleted, isFailed, allowsFreshStart, metadataProviders, metadata);
	}

	@Override
	public ActivityResult and(ActivityResult other) {
		Set<Device> devices = new HashSet<>();
		List<ActivityPropertyProvider<?>> metadataProviders = new ArrayList<>();
		Map<Device, Map<Class<? extends ActivityProperty>, ActivityProperty>> metadata = new HashMap<>();
		devices.addAll(getDevices());
		devices.addAll(other.getDevices());
		metadata.putAll(this.getMetadata());
		metadata.putAll(other.getMetadata());
		metadataProviders.addAll(this.getMetadataProviders());
		metadataProviders.addAll(other.getMetadataProviders());
		SimpleResult ret = new SimpleResult(getName() + " + " + other.getName(), devices,
				isCompletedWhenActive() && other.isCompletedWhenActive(),
				isFailedWhenActive() != null ? isFailedWhenActive() : other.isFailedWhenActive(),
				allowsFreshStartWhenActive() && other.allowsFreshStartWhenActive(), metadataProviders, metadata);

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

	@Override
	protected void updateStatus(Status status) {
		super.updateStatus(status);
	}

	public void setActive() {
		updateStatus(Status.ACTIVE);
	}

	public void setPossible() {
		updateStatus(Status.POSSIBLE);
	}

	public void setImpossible() {
		updateStatus(Status.IMPOSSIBLE);
	}

}
