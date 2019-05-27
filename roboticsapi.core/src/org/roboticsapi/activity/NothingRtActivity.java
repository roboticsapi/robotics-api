/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.exception.RoboticsException;

class NothingRtActivity extends AbstractRtActivity implements RtActivity {

	private final RoboticsRuntime runtime;
	private final List<Device> controlledDevices;
	private Map<Device, Activity> prevActivities;

	public NothingRtActivity(RoboticsRuntime runtime, List<Device> controlledDevices) {
		this.runtime = runtime;
		this.controlledDevices = controlledDevices;

	}

	@Override
	public RoboticsRuntime getRuntime() {
		return runtime;
	}

	@Override
	public Set<Device> prepare(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException {

		this.prevActivities = prevActivities;

		setCommand(runtime.createWaitCommand(0.01), prevActivities);

		return null;
	}

	@Override
	public List<Device> getControlledDevices() {
		return controlledDevices;
	}

	@Override
	public <T extends ActivityProperty> Future<T> getFutureProperty(Device device, Class<T> property) {
		return prevActivities.get(device).getFutureProperty(device, property);
	}

}
