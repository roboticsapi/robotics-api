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
import java.util.Map.Entry;
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
		for (Entry<Device, Map<Class<? extends ActivityProperty>, ActivityProperty>> data : other.getMetadata()
				.entrySet()) {
			if (metadata.containsKey(data.getKey()))
				throw new IllegalArgumentException(
						"Cannot combine two ActivityResults with metadata for the same device.");
			metadata.put(data.getKey(), data.getValue());
		}
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
	public ActivityResult withMetadataFor(Set<Device> devices) {
		List<ActivityPropertyProvider<?>> metadataProviders = new ArrayList<>();
		Map<Device, Map<Class<? extends ActivityProperty>, ActivityProperty>> metadata = new HashMap<>();
		for (Device device : devices) {
			metadata.put(device, getMetadata().get(device));
		}
		for(ActivityPropertyProvider<?> provider: getMetadataProviders()) {
			for(Device device: devices) {
				if(provider.getSupportedDevices().contains(device)) {
					metadataProviders.add(provider);
					break;
				}
			}
		}

		
		return new SimpleResult(getName(), getDevices(), isCompletedWhenActive(), isFailedWhenActive(), allowsFreshStartWhenActive(), metadataProviders, metadata);
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
