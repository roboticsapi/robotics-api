/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.ImmediateFuture;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.eventhandler.JavaExecutor;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanSensor;

public class ConditionalRtActivity extends ComposedRtActivity {

	private final Map<RtActivity, BooleanSensor> conditions = new HashMap<RtActivity, BooleanSensor>();

	private final Semaphore decisionTaken = new Semaphore(1);
	private RtActivity chosen;

	public ConditionalRtActivity(String name, BooleanSensor condition, RtActivity ifActivity, RtActivity elseActivity)
			throws RoboticsException {
		super(name, ifActivity, elseActivity);

		conditions.put(ifActivity, condition);
		conditions.put(elseActivity, condition.not());
	}

	public ConditionalRtActivity(BooleanSensor condition, RtActivity ifActivity) throws RoboticsException {
		this(ifActivity.getName() + "[?] {}", condition, ifActivity,
				new NothingRtActivity(ifActivity.getRuntime(), ifActivity.getControlledDevices()));
	}

	public ConditionalRtActivity(String name, BooleanSensor condition, RtActivity ifActivity) throws RoboticsException {
		this(name, condition, ifActivity,
				new NothingRtActivity(ifActivity.getRuntime(), ifActivity.getControlledDevices()));
	}

	public ConditionalRtActivity(BooleanSensor condition, RtActivity ifActivity, RtActivity elseActivity)
			throws RoboticsException {
		this("[?] " + elseActivity.getName(), condition, ifActivity, elseActivity);
	}

	@Override
	protected Set<Device> prepareMultipleActivities(Map<Device, Activity> prevActivities)
			throws RoboticsException, ActivityNotCompletedException {
		TransactionCommand trans = getControlledDevices().get(0).getDriver().getRuntime()
				.createTransactionCommand("ConditionalActivity");

		Set<Device> controlledDevices = new HashSet<Device>();

		for (final RtActivity t : innerActivities) {

			Set<Device> tDevs = t.prepare(prevActivities);
			if (tDevs != null) {
				controlledDevices.addAll(tDevs);
			}

			trans.addCommand(t.getCommand());

			BooleanSensor cond = conditions.get(t);

			if (cond == null) {
				trans.addStartCommand(t.getCommand());
			} else {
				trans.addStartCommand(cond, t.getCommand());
			}

			trans.addTakeoverAllowedCondition(t.getCommand().getTakeoverAllowedState());

			trans.addStateFirstEnteredHandler(t.getCommand().getStartedState(), new JavaExecutor(new Runnable() {

				@Override
				public void run() {
					chosen = t;

					decisionTaken.release();

				}
			}));
		}

		setCommand(trans, prevActivities);

		try {
			decisionTaken.acquire();
		} catch (InterruptedException e) {
			throw new RoboticsException(e);
		}

		return controlledDevices;
	}

	@Override
	public ConditionalRtActivity beginExecute() throws RoboticsException {

		return (ConditionalRtActivity) super.beginExecute();
	}

	@Override
	public <T extends ActivityProperty> Future<T> getFutureProperty(Device device, Class<T> property) {
		Future<T> prop = super.getFutureProperty(device, property);

		if (prop != null) {
			return prop;
		}

		final HashMap<RtActivity, Future<T>> innerProps = new HashMap<RtActivity, Future<T>>();

		boolean common = true;

		for (RtActivity i : innerActivities) {

			Future<T> currProp = i.getFutureProperty(device, property);

			innerProps.put(i, currProp);

			if (currProp == null) {
				common = false;
			} else if (prop != null && !(currProp.equals(prop))) {
				common = false;
			}

			prop = currProp;
		}

		if (common) {
			return prop;
		}

		return new ChosenActivityFuture<T>(innerProps);
	}

	public Future<RtActivity> getDecision() {
		final HashMap<RtActivity, Future<RtActivity>> innerProps = new HashMap<RtActivity, Future<RtActivity>>();

		for (RtActivity i : innerActivities) {

			innerProps.put(i, new ImmediateFuture<RtActivity>(i));

		}

		return new ChosenActivityFuture<RtActivity>(innerProps);

	}

	public boolean hasChosen(RtActivity activity) throws RoboticsException {
		try {
			return getDecision().get() == activity;
		} catch (InterruptedException e) {
			throw new RoboticsException(e);
		} catch (ExecutionException e) {
			throw new RoboticsException(e);
		}
	}

	private final class ChosenActivityFuture<T> implements Future<T> {
		private final HashMap<RtActivity, Future<T>> innerProps;

		private ChosenActivityFuture(HashMap<RtActivity, Future<T>> innerProps) {
			this.innerProps = innerProps;
		}

		@Override
		public boolean cancel(boolean arg0) {
			return false;
		}

		@Override
		public T get() throws InterruptedException, ExecutionException {
			if (!checkStarted()) {
				return null;
			}

			decisionTaken.acquire();

			decisionTaken.release();

			return innerProps.get(chosen).get();
		}

		@Override
		public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			if (!decisionTaken.tryAcquire(timeout, unit)) {
				throw new TimeoutException();
			}

			decisionTaken.release();

			return innerProps.get(chosen).get();
		}

		private boolean checkStarted() {
			return getCommand().getCommandHandle() != null;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return chosen != null;
		}
	}

}
