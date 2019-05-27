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
import java.util.Vector;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.State;
import org.roboticsapi.core.exception.RoboticsException;

/**
 * Abstract implementation of {@link Activity}. Uses the
 * {@link ActivityScheduler} singleton instance to schedule execution.
 */
public abstract class AbstractActivity implements Activity {

	private final List<Device> additionalAffectedDevices = new Vector<Device>();
	private final List<ActivityStatusListener> statusListeners = new Vector<ActivityStatusListener>();
	protected String name;
	private ActivityStatus status = ActivityStatus.NEW;
	private boolean exceptionHasBeenThrown = false;

	/**
	 * Creates a new AbstractActivity instance.
	 * 
	 * @param name the Activity's name
	 */
	public AbstractActivity(String name) {
		this.name = name;
	}

	/**
	 * Creates a new AbstractActivity instance.
	 */
	public AbstractActivity() {
		this("Activity");
	}

	/**
	 * protected static wrapper to getAffectedDevices
	 * 
	 * @param activity activity to get the affected devices for
	 * @return list of affected devices
	 */
	public static List<Device> getAffectedDevices(RtActivity activity) {
		return activity.getAffectedDevices();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void addStatusListener(ActivityStatusListener listener) {
		statusListeners.add(listener);

		listener.activityStatusChanged(this, getStatus());
	}

	@Override
	public void removeStatusListener(ActivityStatusListener listener) {
		statusListeners.remove(listener);
	}

	@Override
	public void execute() throws RoboticsException {
		beginExecute();

		// System.out.println("beginExecute() finished");

		endExecute();
	}

	/**
	 * Starts execution of this Activity. This implementation uses the
	 * {@link ActivityScheduler} to schedule execution.
	 * 
	 * @return this Activity instance
	 * @throws RoboticsException thrown if scheduling fails
	 */
	@Override
	public Activity beginExecute() throws RoboticsException {
		if (getStatus() != ActivityStatus.NEW) {
			throw new RoboticsException("The activity has already been started.");
		}

		Map<Device, Activity> prev = ActivityScheduler.instance().getPreviousActivities(this);

		Set<Device> controlledDevs = null;

		while (true) {
			try {
				controlledDevs = prepare(prev);
				break;
			} catch (ActivityNotCompletedException ex) {
				ex.getActivity().endExecute();
			}
		}

		// changeStatus(ActivityStatus.SCHEDULED);

		scheduleActivity(controlledDevs);

		return this;
	}

	protected void scheduleActivity(Set<Device> controlledDevs) throws RoboticsException {
		ActivityScheduler.instance().schedule(this, controlledDevs);
	}

	@Override
	public void endExecute() throws RoboticsException {
		if (exceptionHasBeenThrown) {
			return;
		}

		if (getStatus() == ActivityStatus.NEW) {
			throw new IllegalStateException("endExecute() may only be called after calling beginExecute()");
		}

		if (getStatus() == ActivityStatus.FAILED) {
			exceptionHasBeenThrown = true;
			throw new RoboticsException("Activity execution failed!");
		}

	}

	protected void notifyStatusChanged(ActivityStatus status) {

		for (ActivityStatusListener listener : new ArrayList<ActivityStatusListener>(statusListeners)) {
			listener.activityStatusChanged(this, status);
		}
	}

	protected void changeStatus(ActivityStatus newStatus) {
		if (newStatus == status) {
			return;
		}

		status = newStatus;

		notifyStatusChanged(status);
	}

	@Override
	public ActivityStatus getStatus() {
		return status;
	}

	@Override
	public void addAdditionalAffectedDevice(Device device) {
		additionalAffectedDevices.add(device);
	}

	@Override
	public void addAdditionalAffectedDevices(List<Device> devices) {
		additionalAffectedDevices.addAll(devices);
	}

	@Override
	public List<Device> getAffectedDevices() {
		ArrayList<Device> retval = new ArrayList<Device>(getControlledDevices());

		retval.addAll(additionalAffectedDevices);

		return retval;
	}

	/**
	 * protected static wrapper to getControlledDevices
	 * 
	 * @param activity activity to get the controlled devices for
	 * @return list of controlled devices
	 */
	public static List<Device> getControlledDevices(RtActivity activity) {
		return activity.getControlledDevices();
	}

	@Override
	public abstract List<Device> getControlledDevices();

	@Override
	public final void cancelExecute() throws RoboticsException {
		if (getStatus() == ActivityStatus.NEW) {
			throw new IllegalStateException("Activity may not be cancelled before it was started");
		}

		cancelExecuteInternal();
	}

	protected abstract void cancelExecuteInternal() throws RoboticsException;

	class StateListening<T> {
		public final State state;
		public final T listener;

		public StateListening(State state, T listener) {
			this.state = state;
			this.listener = listener;

		}
	}

	@Override
	public String toString() {
		String name = getName();
		return getClass().getSimpleName() + name == null ? "" : " '" + name + "'";
	}

}
