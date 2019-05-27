/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.exception.RoboticsException;

public class RtActivityWithMaintaining extends ComposedRtActivity {

	private final RtActivity main, maintain;

	public RtActivityWithMaintaining(RtActivity main, RtActivity maintain) {
		this("Activity with maintaining", main, maintain);
	}

	public RtActivityWithMaintaining(String name, RtActivity main, RtActivity maintain) {
		super(name, main, maintain);

		this.main = main;
		this.maintain = maintain;
	}

	@Override
	protected Set<Device> prepareMultipleActivities(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException {
		TransactionCommand trans = main.getRuntime().createTransactionCommand();

		Set<Device> controlled = main.prepare(prevActivities);
		Command mainCommand = main.getCommand();
		trans.addStartCommand(mainCommand);

		for (Device device : controlled) {
			prevActivities.put(device, main);
		}
		maintain.prepare(prevActivities);
		Command maintainCommand = maintain.getCommand();
		trans.addCommand(maintainCommand);

		trans.addStateFirstEnteredHandler(mainCommand.getDoneState(), new CommandStarter(maintainCommand));

		// TAKE OVER
		trans.addTakeoverAllowedCondition(maintainCommand.getTakeoverAllowedState());

		// CANCEL
		trans.addStateEnteredHandler(trans.getCancelState().and(mainCommand.getActiveState()),
				new CommandCanceller(mainCommand));
		trans.addStateEnteredHandler(trans.getCancelState().and(maintainCommand.getActiveState()),
				new CommandCanceller(maintainCommand));

		// DONE
		trans.addDoneStateCondition(maintainCommand.getDoneState());

		setCommand(trans, prevActivities, maintainCommand.getActiveState());
		return controlled;
	}

	@Override
	public <T extends ActivityProperty> Future<T> getFutureProperty(Device device, Class<T> property) {
		Future<T> prop = super.getFutureProperty(device, property);

		if (prop != null) {
			return prop;
		}

		if (maintain.getAffectedDevices().contains(device)) {
			return maintain.getFutureProperty(device, property);
		}
		return null;
	}
}
