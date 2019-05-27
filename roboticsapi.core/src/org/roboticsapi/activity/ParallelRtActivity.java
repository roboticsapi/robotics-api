/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.State;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.state.AndState;
import org.roboticsapi.core.state.OrState;

public class ParallelRtActivity extends ComposedRtActivity {

	public List<RtActivity> getActivities() {
		return Collections.unmodifiableList(innerActivities);
	}

	public ParallelRtActivity(RtActivity... activities) throws RoboticsException {
		this("ParallelActivity", activities);
	}

	public ParallelRtActivity(String name, RtActivity... activities) throws RoboticsException {
		super(name, activities);
	}

	public <T extends RtActivity> T addActivity(T activity) throws RoboticsException {
		for (Device d : activity.getAffectedDevices()) {
			for (RtActivity i : innerActivities) {
				if (i.getAffectedDevices().contains(d)) {
					throw new RoboticsException(
							"Some of the devices affected by the new Activity are already affected by another Activity");
				}
			}
		}

		innerActivities.add(activity);

		return activity;
	};

	@Override
	protected Set<Device> prepareMultipleActivities(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException {

		Set<Device> controlledDevices = new HashSet<Device>();

		for (RtActivity t : innerActivities) {
			Set<Device> tDevs = t.prepare(prevActivities);

			if (tDevs != null) {
				controlledDevices.addAll(tDevs);
			}
		}

		TransactionCommand trans = innerActivities.get(0).getCommand().getRuntime().createTransactionCommand(getName());

		AndState allDone = new AndState();
		AndState allTakeover = new AndState();
		AndState noneRunning = new AndState();
		OrState anyMaintaining = new OrState();
		for (RtActivity t : innerActivities) {
			Command innerCmd = t.getCommand();
			trans.addStartCommand(innerCmd);
			allDone.addState(t.getCompletedState());

			// inner activity is cancelled when transaction is cancelled
			trans.addStateFirstEnteredHandler(trans.getCancelState(), new CommandCanceller(innerCmd));

			allTakeover.addState(innerCmd.getTakeoverAllowedState().or(innerCmd.getActiveState().not()));

			State completedState = t.getCompletedState();
			State maintainingState = t.getMaintainingState();

			if (completedState != null && maintainingState != null) {
				noneRunning.addState(completedState.or(maintainingState));
			} else if (completedState == null) {
				noneRunning.addState(maintainingState);
			} else if (maintainingState == null) {
				noneRunning.addState(completedState);
			}

			if (maintainingState != null) {
				anyMaintaining.addState(maintainingState);
			}
		}

		trans.addTakeoverAllowedCondition(allTakeover);
		trans.addDoneStateCondition(allDone);

		setCommand(trans, prevActivities, noneRunning.and(anyMaintaining));

		return controlledDevices;
	}

	@Override
	public <T extends ActivityProperty> Future<T> getFutureProperty(Device device, Class<T> property) {
		Future<T> prop = super.getFutureProperty(device, property);

		if (prop != null) {
			return prop;
		}

		for (int i = innerActivities.size() - 1; i >= 0; i--) {
			if (innerActivities.get(i).getAffectedDevices().contains(device)) {
				return innerActivities.get(i).getFutureProperty(device, property);
			}
		}

		return null;
	}

}
