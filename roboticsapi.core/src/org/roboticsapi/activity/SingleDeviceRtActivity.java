/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.State;
import org.roboticsapi.core.exception.RoboticsException;

public abstract class SingleDeviceRtActivity<D extends Actuator> extends AbstractRtActivity {

	private final D device;
	private Command command;

	public SingleDeviceRtActivity(D device, Command command, State maintainingState) throws RoboticsException {
		this(command.getName(), device);
		this.command = command;
		// setCommand(command, maintainingState);
	}

	public SingleDeviceRtActivity(D device) {
		super();
		this.device = device;
	}

	public SingleDeviceRtActivity(String name, D device) {
		super(name);
		this.device = device;
	}

	@Override
	public Set<Device> prepare(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException {
		if (command != null) {
			setCommand(command, prevActivities);
		}
		if (prepare(prevActivities.get(device))) {
			return new HashSet<Device>(getControlledDevices());
		} else {
			return null;
		}
	}

	protected abstract boolean prepare(Activity prevActivity) throws RoboticsException, ActivityNotCompletedException;

	protected void setCommand(Command command, Activity prevActivity) throws RoboticsException {
		Map<Device, Activity> prev = new HashMap<Device, Activity>();
		prev.put(getDevice(), prevActivity);
		setCommand(command, prev);
	}

	protected void setCommand(Command command, Activity prevActivity, State maintainingState) throws RoboticsException {
		Map<Device, Activity> prev = new HashMap<Device, Activity>();
		prev.put(getDevice(), prevActivity);
		setCommand(command, prev, maintainingState);
	}

	@Override
	public final List<Device> getControlledDevices() {
		List<Device> result = new ArrayList<Device>();
		result.add(getDevice());
		return result;
	}

	protected final D getDevice() {
		return device;
	}

	@Override
	public final RoboticsRuntime getRuntime() {
		return getDevice().getDriver().getRuntime();
	}

}
