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
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.CommandCanceller;
import org.roboticsapi.core.eventhandler.CommandStarter;
import org.roboticsapi.core.eventhandler.CommandStopper;
import org.roboticsapi.core.exception.RoboticsException;

/**
 * A SequentialRtActivity combines multiple {@link RtActivity} instances and
 * executes them sequentially with real-time guarantees.
 */
public class SequentialRtActivity extends ComposedRtActivity {
	private final Set<RtActivity> contActivities = new HashSet<RtActivity>();

	/**
	 * Gets the RtActivities contained in this sequence in the order they will be
	 * executed.
	 *
	 * @return RtActivities contained in the sequence
	 */
	public List<RtActivity> getActivites() {
		return Collections.unmodifiableList(innerActivities);
	}

	/**
	 * Instantiates a new SequentialRtActivity.
	 */
	public SequentialRtActivity() {
	}

	/**
	 * Instantiates a new SequentialRtActivity with a given name.
	 *
	 * @param name the name of the created Activity
	 */
	public SequentialRtActivity(String name) {
		super(name);
	}

	/**
	 * Adds a new {@link RtActivity} to the end of the sequence.
	 *
	 * @param <T>      the type of Activity to add
	 * @param activity the Activity to add
	 * @return the added Activity
	 */
	public <T extends RtActivity> T addActivity(T activity) throws RoboticsException {
		innerActivities.add(activity);

		return activity;
	}

	/**
	 * Adds a new {@link RtActivity} to the end of the sequence. The added
	 * RtActivity will be blendable by subsequent RtActivities, if any.
	 *
	 * @param <T>      the type of Activity to add
	 * @param activity the Activity to add
	 * @return the added Activity
	 */
	public <T extends RtActivity> T addContActivity(T activity) throws RoboticsException {
		contActivities.add(activity);

		return addActivity(activity);
	}

	@Override
	protected Set<Device> prepareMultipleActivities(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException {

		Set<Device> controlledDevs = innerActivities.get(0).prepare(prevActivities);

		Command start = innerActivities.get(0).getCommand();
		TransactionCommand trans = innerActivities.get(0).getCommand().getRuntime()
				.createTransactionCommand("SequentialActivity", start);
		trans.addStartCommand(start);
		trans.addStateEnteredHandler(trans.getCancelState().and(innerActivities.get(0).getCommand().getActiveState()),
				new CommandCanceller(innerActivities.get(0).getCommand()));

		for (int i = 1; i < innerActivities.size(); i++) {

			RtActivity prev = innerActivities.get(i - 1);
			RtActivity cur = innerActivities.get(i);

			for (Device aff : prev.getAffectedDevices()) {
				prevActivities.put(aff, prev);
			}

			Set<Device> takenOver = null;
			try {
				takenOver = cur.prepare(prevActivities);
			} catch (ActivityNotCompletedException e) {
				throw new RoboticsException("Cannot pre-plan Activity sequence: Activity " + e.getActivity()
						+ " does not provide information required by " + cur, e);
			}

			trans.addCommand(cur.getCommand());
			trans.addStateFirstEnteredHandler(prev.getCommand().getCompletedState().and(trans.getCancelState().not()),
					new CommandStarter(cur.getCommand()));

			if (contActivities.contains(prev) && takenOver != null
					&& takenOver.containsAll(prev.getControlledDevices())) {
				trans.addStateFirstEnteredHandler(prev.getCommand().getTakeoverAllowedState(),
						new CommandStopper(prev.getCommand()));
				trans.addStateFirstEnteredHandler(prev.getCommand().getTakeoverAllowedState(),
						new CommandStarter(cur.getCommand()));
			}

			trans.addStateEnteredHandler(trans.getCancelState().and(cur.getCommand().getActiveState()),
					new CommandCanceller(cur.getCommand()));
		}

		RtActivity last = innerActivities.get(innerActivities.size() - 1);
		if (contActivities.contains(last) || last.getMaintainingState() != null) {
			trans.addTakeoverAllowedCondition(last.getCommand().getTakeoverAllowedState());
		}

		trans.addDoneStateCondition(last.getCommand().getCompletedState());
		if (last.getMaintainingState() != null) {
			setCommand(trans, prevActivities, last.getMaintainingState());
		} else {
			setCommand(trans, prevActivities);
		}

		return controlledDevs;
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
