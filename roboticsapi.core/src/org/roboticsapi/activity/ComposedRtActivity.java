/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.exception.RoboticsException;

public abstract class ComposedRtActivity extends AbstractRtActivity {

	protected List<RtActivity> innerActivities = new ArrayList<RtActivity>();
	private TransactionCommand trans;
	private RtActivity[] terminateActivities;

	public ComposedRtActivity() {
		super("ComposedActivity");
	}

	public ComposedRtActivity(String name) {
		super(name);
	}

	public ComposedRtActivity(String name, RtActivity... activities) {
		super(name);
		for (RtActivity t : activities) {
			if (t != null) {
				innerActivities.add(t);
			}
		}
	}

	public ComposedRtActivity(RtActivity... activities) throws RoboticsException {
		this("ComposedActivity", activities);
	}

	protected TransactionCommand getTransactionCommand() {
		return trans;
	}

	@Override
	public List<Device> getAffectedDevices() {
		List<Device> affected = new ArrayList<Device>();

		affected.addAll(super.getAffectedDevices());

		for (RtActivity inner : innerActivities) {
			affected.addAll(inner.getAffectedDevices());
		}

		return affected;
	}

	@Override
	public List<Device> getControlledDevices() {
		List<Device> controlled = new ArrayList<Device>();

		for (RtActivity inner : innerActivities) {
			controlled.addAll(inner.getControlledDevices());
		}

		return controlled;
	}

	public void setTerminateActivities(RtActivity... terminateActivities) {
		this.terminateActivities = terminateActivities;
	}

	@Override
	public RoboticsRuntime getRuntime() {
		if (innerActivities.size() > 0) {
			return innerActivities.get(0).getRuntime();
		} else {
			return null;
		}
	}

	@Override
	protected void beforeCommandSeal(Command command) throws RoboticsException {
		super.beforeCommandSeal(command);

		if (terminateActivities == null) {
			return;
		}

		for (RtActivity a : terminateActivities) {
			command.addDoneStateCondition(a.getCommand().getCompletedState());
		}
	}

	@Override
	public Set<Device> prepare(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException {

		if (innerActivities.size() > 0) {
			return prepareMultipleActivities(prevActivities);
		} else {
			return null;
		}
	}

	protected abstract Set<Device> prepareMultipleActivities(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException;
}
