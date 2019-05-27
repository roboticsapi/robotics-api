/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.State;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.state.OrState;

public class RtActivityWithSubactivities extends ComposedRtActivity {

	private final List<TriggerActivity> subactivities = new ArrayList<TriggerActivity>();
	private final RtActivity main;

	public RtActivityWithSubactivities(String name, RtActivity main) throws RoboticsException {
		super(name);
		innerActivities.add(main);
		this.main = main;
	}

	@Override
	public List<Device> getControlledDevices() {
		return main.getControlledDevices();
	}

	public RtActivityWithSubactivities(RtActivity main) throws RoboticsException {
		this("[+] " + main.getName(), main);
	}

	public <T extends RtActivity> T addSubActivity(State condition, T subactivity) throws RoboticsException {
		for (Device d : subactivity.getAffectedDevices()) {
			if (main.getAffectedDevices().contains(d)) {
				throw new RoboticsException(
						"Some of the devices affected by the new Activity are already affected by another Activity");
			}

		}

		subactivities.add(new TriggerActivity(condition, subactivity));

		innerActivities.add(subactivity);

		return subactivity;
	}

	@Override
	protected Set<Device> prepareMultipleActivities(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException {
		TransactionCommand trans = main.getRuntime().createTransactionCommand();

		Set<Device> controlled = main.prepare(prevActivities);

		trans.addStartCommand(main.getCommand());

		OrState subactivityActive = new OrState();
		for (TriggerActivity t : subactivities) {

			t.activity.prepare(new HashMap<Device, Activity>());

			trans.addCommand(t.activity.getCommand());

			if (t.condition != null) {
				trans.addStateFirstEnteredHandler(t.condition, new CommandStarter(t.activity.getCommand()));
			}

			subactivityActive.addState(
					t.activity.getCommand().getActiveState().and(t.activity.getCommand().getDoneState().not()));
		}

		trans.addTakeoverAllowedCondition(main.getCommand().getTakeoverAllowedState().and(subactivityActive.not()));

		trans.addDoneStateCondition(main.getCommand().getCompletedState().and(subactivityActive.not()));

		trans.addStateEnteredHandler(trans.getCancelState(), new CommandCanceller(main.getCommand()));

		setCommand(trans, prevActivities);

		return controlled;
	}

	@Override
	public <T extends ActivityProperty> Future<T> getFutureProperty(Device device, Class<T> property) {
		Future<T> prop = super.getFutureProperty(device, property);

		if (prop != null) {
			return prop;
		}

		if (main.getAffectedDevices().contains(device)) {
			return main.getFutureProperty(device, property);
		}

		return null;
	}
}
