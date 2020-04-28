/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.exception.RoboticsException;

public abstract class ActivityResult {

	public enum Status {
		POSSIBLE, IMPOSSIBLE, ACTIVE
	};

	public abstract ActivityResult and(ActivityResult other);

	public abstract ActivityResult withMetadataFor(Set<Device> devices);

	private Status status = Status.POSSIBLE;
	private final Set<Device> devices = new HashSet<>();
	private final Map<Device, Map<Class<? extends ActivityProperty>, ActivityProperty>> metadata = new HashMap<>();
	private final List<ActivityPropertyProvider<?>> metadataProviders;
	private final List<Consumer<Status>> statusConsumers = new ArrayList<>();
	private final String name;
	private final boolean isCompleted;
	private final boolean allowsFreshStart;
	private final RoboticsException isFailed;

	public ActivityResult(String name, Device device, boolean isCompleted, RoboticsException isFailed,
			boolean allowsFreshStart, List<ActivityPropertyProvider<?>> metadataProviders,
			Map<Class<? extends ActivityProperty>, ActivityProperty> metadata) {
		this.name = name;
		this.isCompleted = isCompleted;
		this.isFailed = isFailed;
		this.metadataProviders = metadataProviders;
		this.devices.add(device);
		this.allowsFreshStart = allowsFreshStart;
		this.metadata.put(device, metadata);
	}

	public ActivityResult(String name, Set<Device> devices, boolean isCompleted, RoboticsException isFailed,
			boolean allowsFreshStart, List<ActivityPropertyProvider<?>> metadataProviders,
			Map<Device, Map<Class<? extends ActivityProperty>, ActivityProperty>> metadata) {
		this.name = name;
		this.isCompleted = isCompleted;
		this.isFailed = isFailed;
		this.metadataProviders = metadataProviders;
		this.devices.addAll(devices);
		this.allowsFreshStart = allowsFreshStart;
		this.metadata.putAll(metadata);
	}

	public Set<Device> getDevices() {
		return devices;
	}

	public boolean allowsFreshStartWhenActive() {
		return allowsFreshStart;
	}

	public boolean isCompletedWhenActive() {
		return isCompleted;
	}

	public <T extends ActivityProperty> void addMetadata(Device device, T property) {
		if (metadata.get(device) == null)
			metadata.put(device, new HashMap<>());
		metadata.get(device).put(property.getClass(), property);
	}

	public final <T extends ActivityProperty> T getMetadata(Device device, Class<T> property) {
		// if we have the requested meta data, everything is fine
		T ret = provideMetadata(device, property);
		if (ret == null) {
			// otherwise, search for a meta data provider that can provide this
			// type of meta data
			for (ActivityPropertyProvider<?> provider : metadataProviders) {
				if (provider.getProvidedType().equals(property) && provider.getSupportedDevices().contains(device)) {
					// collect types of all dependencies, and (recursively) get
					// these meta data
					Map<Class<? extends ActivityProperty>, ActivityProperty> deps = new HashMap<Class<? extends ActivityProperty>, ActivityProperty>();
					for (Class<? extends ActivityProperty> prop : provider.getDependencies()) {
						ActivityProperty dep = getMetadata(device, prop);
						if (dep == null) {
							break;
						}
						deps.put(prop, dep);
					}
					// then let the provider do its work
					return property.cast(provider.provideProperty(device, deps));
				}
			}
		}
		return ret;
	}

	private <T extends ActivityProperty> T provideMetadata(Device device, Class<T> type) {
		Map<Class<? extends ActivityProperty>, ActivityProperty> forDevice = metadata.get(device);
		if (forDevice == null) {
			return null;
		}
		ActivityProperty forType = forDevice.get(type);
		return type.cast(forType);
	}

	public Map<Device, Map<Class<? extends ActivityProperty>, ActivityProperty>> getMetadata() {
		return metadata;
	}

	public List<ActivityPropertyProvider<?>> getMetadataProviders() {
		return metadataProviders;
	}

	public Status getStatus() {
		return status;
	}

	protected void updateStatus(Status status) {
		synchronized (statusConsumers) {
			if (this.status == status || this.status == Status.IMPOSSIBLE)
				return;
			this.status = status;
			for (Consumer<Status> consumer : statusConsumers) {
				consumer.accept(status);
			}
		}
	}

	public void observeStatus(Consumer<Status> consumer) {
		synchronized (statusConsumers) {
			consumer.accept(status);
			statusConsumers.add(consumer);
		}
	}

	public void observeStatus(RoboticsConsumer<Status> consumer, Consumer<RoboticsException> errorHandler) {
		observeStatus(status -> {
			try {
				consumer.accept(status);
			} catch (RoboticsException e) {
				errorHandler.accept(e);
			}
		});
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name + " " + devices;
	}

	public RoboticsException isFailedWhenActive() {
		return isFailed;
	}

}
