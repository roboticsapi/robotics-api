/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.OnlineObject;
import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.OnlineObject.OperationStateListener;
import org.roboticsapi.core.RoboticsRuntime.CommandHook;
import org.roboticsapi.core.RuntimeCommand;
import org.roboticsapi.core.TransactionCommand;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.RAPILogger;

/**
 * Scheduler for executing series of Activities. Considers previously executed
 * Activities, the Devices that they affected as well as the Devices affected by
 * a newly scheduled Activity to decide when and how to schedule it.
 *
 * This class provides a singleton instance.
 */
public class ActivityScheduler implements CommandHook, OperationStateListener {

	public interface ActivityHook {
		void activityStatusChanged(Activity activity);
	}

	private static ActivityScheduler theInstance = new ActivityScheduler();
	private final HashMap<Device, Activity> activities = new HashMap<Device, Activity>();
	private final List<Activity> waitingActivities = new ArrayList<Activity>();
	private final List<ActivityHook> hooks = new Vector<ActivityScheduler.ActivityHook>();
	private final List<Command> expectedCommands = new Vector<Command>();

	public void addActivityHook(ActivityHook hook) {
		hooks.add(hook);
	}

	private ActivityScheduler() {
	}

	protected void schedule(final Activity activity, Set<Device> devicesTakenOver) throws RoboticsException {

		synchronized (waitingActivities) {
			for (Activity t : waitingActivities) {
				for (Device d : activity.getAffectedDevices()) {
					if (t.getAffectedDevices().contains(d)) {
						throw new RoboticsException(
								"Device " + d.getName() + " is blocked by another activity: " + t.getName());
					}
				}
			}
			waitingActivities.add(activity);
		}

		try {

			activity.addStatusListener(new ActivityStatusListener() {
				@Override
				public void activityStatusChanged(Activity activity, ActivityStatus newStatus) {
					for (ActivityHook hook : hooks) {
						hook.activityStatusChanged(activity);
					}
				}
			});

			for (ActivityHook hook : hooks) {
				hook.activityStatusChanged(activity);
			}

			Map<Device, Activity> prev = getPreviousActivities(activity);

			// find previous running activities
			final List<Activity> running = new ArrayList<Activity>();
			synchronized (running) {
				for (Activity prevActivity : prev.values()) {
					if (prevActivity != null
							&& (prevActivity.getStatus() == ActivityStatus.SCHEDULED
									|| prevActivity.getStatus() == ActivityStatus.RUNNING
									|| prevActivity.getStatus() == ActivityStatus.MAINTAINING)
							&& !running.contains(prevActivity)) {
						running.add(prevActivity);
					}
				}
			}

			// wait for all but one
			try {
				final Semaphore sem = new Semaphore(1);
				if (running.size() > 1) {
					sem.acquire();
					for (final Activity prevActivities : new ArrayList<Activity>(running)) {
						prevActivities.addStatusListener(new ActivityStatusListener() {
							@Override
							public void activityStatusChanged(Activity activity, ActivityStatus newStatus) {
								if (newStatus != ActivityStatus.RUNNING && newStatus != ActivityStatus.MAINTAINING) {
									synchronized (running) {
										running.remove(prevActivities);
										if (running.size() == 1) {
											sem.release();
										}
									}
								}
							}
						});
					}
				}
				sem.acquire();
			} catch (InterruptedException e) {
				throw new RoboticsException(e);
			}

			// schedule or run new activity
			Activity runningActivity = null;
			synchronized (running) {
				if (running.size() == 1) {
					runningActivity = running.get(0);
				} else if (running.size() > 1) {
					throw new RoboticsException("BUG: Multiple activities running!");
				}
			}

			if (activity instanceof RtActivity) {

				RtActivity rtactivity = (RtActivity) activity;

				expectedCommands.add(rtactivity.getCommand());
				if (runningActivity != null && runningActivity instanceof RtActivity) {
					// new Activity may take over running Activity if all
					// Devices
					// controlled by the running Activity will be controlled by
					// the new Activity
					if (devicesTakenOver != null
							&& devicesTakenOver.containsAll(runningActivity.getControlledDevices())) {

						RAPILogger.getLogger().log(RAPILogger.DEBUGLEVEL,
								DateFormat.getDateTimeInstance().format(new Date()) + " Scheduling activity "
										+ rtactivity.getName() + " for takeover");
						rtactivity.getCommand()
								.scheduleAfter(((RtActivity) runningActivity).getCommand().getCommandHandle());
						awaitEnd(runningActivity);
					} else {

						// if the currently Activity is in maintaining state, it
						// is only allowed to start new Activities that can take
						// over all devices, otherwise we have a deadlock
						if (runningActivity.getStatus() == ActivityStatus.MAINTAINING) {
							ArrayList<Device> notTakenOver = new ArrayList<Device>(
									runningActivity.getControlledDevices());
							if (devicesTakenOver != null) {
								notTakenOver.removeAll(devicesTakenOver);
							}

							throw new IllegalSuccessorException(
									"Previous Activity is maintaining, but scheduled Activity can not take over devices: "
											+ Arrays.toString(notTakenOver.toArray()) + "",
									runningActivity, activity);
						}

						awaitEnd(runningActivity);
						RAPILogger.getLogger().log(RAPILogger.DEBUGLEVEL,
								DateFormat.getDateTimeInstance().format(new Date()) + " Scheduling activity "
										+ rtactivity.getName() + " as distinct successor");
						rtactivity.getCommand()
								.scheduleAfter(((RtActivity) runningActivity).getCommand().getCommandHandle());
					}
				} else {
					RAPILogger.getLogger().log(RAPILogger.DEBUGLEVEL,
							DateFormat.getDateTimeInstance().format(new Date()) + " Starting activity "
									+ rtactivity.getName() + " (no predecessor)");

					rtactivity.getCommand().start();
				}
			} else {
				activity.beginExecute();
			}

			// store activity as current
			for (Device dev : activity.getAffectedDevices()) {
				if (activities.get(dev) == null) {
					dev.addOperationStateListener(this);
				}
				activities.put(dev, activity);
			}

		} finally {
			synchronized (waitingActivities) {
				waitingActivities.remove(activity);
			}
		}
	}

	protected Activity get(Device dev) {
		return activities.get(dev);
	}

	/**
	 * Gets the singleton {@link ActivityScheduler} instance.
	 *
	 * @return ActivityScheduler singleton instance
	 */
	public static ActivityScheduler instance() {
		return theInstance;
	}

	protected Map<Device, Activity> getPreviousActivities(Activity activity) throws RoboticsException {
		Map<Device, Activity> ret = new HashMap<Device, Activity>();
		for (Device dev : activity.getAffectedDevices()) {
			Activity prevActivity = ActivityScheduler.instance().get(dev);
			if (prevActivity != null) {
				if (prevActivity.getStatus() != ActivityStatus.FAILED && prevActivity.getException() == null) {
					ret.put(dev, prevActivity);
				} else {
					awaitEnd(prevActivity);
				}
			}

		}
		return ret;
	}

	private void awaitEnd(Activity activity) throws RoboticsException {
		try {
			activity.endExecute();
		} catch (RoboticsException ex) {
			throw new PreviousActivityFailedException(activity.getException());
		}

	}

	/**
	 * Cancels the execution of the {@link Activity} the given {@link Actuator}
	 * currently executes (if any). If no such Activity exists, does nothing.
	 *
	 * @param actuator the Actuator whose Activity should be cancelled
	 * @throws RoboticsException if cancelling fails
	 */
	public void cancel(Actuator actuator) throws RoboticsException {
		Activity activity = activities.get(actuator);
		if (activity == null) {
			return;
		}
		activity.cancelExecute();
		activity.endExecute();
	}

	/**
	 * Waits until the {@link Activity} that the given {@link Actuator} currently
	 * executes (if any) has terminated. If no such Activity exists, does nothing.
	 *
	 * @param actuator the Actuator for which to wait
	 * @throws RoboticsException if any exception occurs during Activity execution
	 */
	public void endExecute(Actuator actuator) throws RoboticsException {
		Activity activity = activities.get(actuator);
		if (activity == null) {
			return;
		}
		activity.endExecute();
	}

	protected void commandLoaded(Command command) {
		if (expectedCommands.contains(command)) {
			// command belongs to our activity
			expectedCommands.remove(command);
		} else {
			// unknown command -> invalidate state
			for (Device dev : getControlledDevices(command)) {
				dev.removeOperationStateListener(this);
				activities.put(dev, null);
			}
		}
	}

	@Override
	public void commandSealHook(Command command) {
	}

	@Override
	public void commandLoadHook(Command command) {
		ActivityScheduler.instance().commandLoaded(command);
	}

	@Override
	public void commandHandleHook(CommandHandle handle) {
	}

	private List<Device> getControlledDevices(Command command) {
		List<Device> result = new ArrayList<Device>();
		if (command instanceof RuntimeCommand) {
			Device dev = ((RuntimeCommand) command).getDevice();
			result.add(dev);
		} else if (command instanceof TransactionCommand) {
			for (Command cmd : ((TransactionCommand) command).getCommandsInTransaction()) {
				for (Device dev : getControlledDevices(cmd)) {
					if (!result.contains(dev)) {
						result.add(dev);
					}
				}
			}
		}
		return result;
	}

	@Override
	public void operationStateChanged(OnlineObject object, OperationState newState) {
		if (newState != OperationState.OPERATIONAL) {
			object.removeOperationStateListener(this);
			if (activities.get(object) != null) {
				activities.put((Device) object, null);
			}
		}
	}
}
