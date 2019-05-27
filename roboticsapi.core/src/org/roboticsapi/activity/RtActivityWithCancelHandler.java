/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.exception.RoboticsException;

public class RtActivityWithCancelHandler extends ComposedRtActivity {

	private final RtActivity main, handler;

	public RtActivityWithCancelHandler(RtActivity main, RtActivity handler) {
		this("Activity with cancel handler", main, handler);
	}

	public RtActivityWithCancelHandler(String name, RtActivity main, RtActivity handler) {
		super(name, main, handler);

		this.main = main;
		this.handler = handler;
	}

	@Override
	protected Set<Device> prepareMultipleActivities(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException {
		TransactionCommand trans = main.getRuntime().createTransactionCommand();

		Set<Device> controlled = main.prepare(prevActivities);
		Command mainCommand = main.getCommand();
		trans.addStartCommand(mainCommand);

		handler.prepare(new HashMap<Device, Activity>());
		Command handlerCommand = handler.getCommand();
		trans.addCommand(handlerCommand);

		trans.addStateFirstEnteredHandler(mainCommand.getCompletedState().and(trans.getCancelState()),
				new CommandStarter(handlerCommand));

		// TAKE OVER
		trans.addTakeoverAllowedCondition(mainCommand.getTakeoverAllowedState());

		// CANCEL
		trans.addStateEnteredHandler(trans.getCancelState().and(mainCommand.getActiveState()),
				new CommandCanceller(mainCommand));

		// DONE
		// trans.addDoneStateCondition(maintainCommand.getDoneState());

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
