/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.roboticsapi.core.Device;
import org.roboticsapi.core.Predicate;
import org.roboticsapi.core.UnhandledErrorsException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.RAPILogger;

public abstract class ActivityHandle {

	public enum Status {
		LOADED, BOUND, RUNNING, FAILED, COMPLETED
	}

	private RoboticsException failureReason = null;
	private Activity activity;
	private Status status = Status.LOADED;
	private RoboticsException error = null;
	private final List<Consumer<Status>> statusConsumers = new ArrayList<>();
	private final List<Runnable> whenCancelled = new ArrayList<>();
	private volatile boolean cancelled = false;
	private final ActivityResultContainer results = new ActivityResultContainer();

	public ActivityHandle(Activity activity) throws RoboticsException {
		this.activity = activity;

		results.provide(result -> {
			if (result == null) {
				return;
			}
			result.observeStatus(status -> {
				if (status == ActivityResult.Status.ACTIVE && result.isCompletedWhenActive()) {
					updateStatus(Status.COMPLETED);
				}
				RoboticsException failedWhenActive = result.isFailedWhenActive();
				if (status == ActivityResult.Status.ACTIVE && failedWhenActive != null) {
					fail(failedWhenActive);
				}
			});
		});
	}

	protected void fail(RoboticsException e) {
		this.error = e;
		updateStatus(Status.FAILED);
	}

	public Activity getActivity() {
		return activity;
	}

	public Status getStatus() {
		return status;
	}

	protected void updateStatus(Status status) {
		synchronized (statusConsumers) {
			if (this.status == status)
				return;
			RAPILogger.getLogger(ActivityHandle.class).fine(this + " changed to " + status);
			this.status = status;
			for (Consumer<Status> consumer : new ArrayList<>(statusConsumers)) {
				consumer.accept(status);
			}
		}
	}

	protected void runWhenCancelled(Runnable whenCancelled) {
		synchronized (this.whenCancelled) {
			this.whenCancelled.add(whenCancelled);
			if (cancelled) {
				whenCancelled.run();
			}
		}
	}

	public void addStatusListener(Consumer<Status> consumer) {
		synchronized (statusConsumers) {
			consumer.accept(status);
			statusConsumers.add(consumer);
		}
	}

	public void endExecute() throws RoboticsException {
		waitForStatus(s -> s == Status.COMPLETED || s == Status.FAILED);
		if (error != null) {
			throw (error instanceof UnhandledErrorsException)
					&& ((UnhandledErrorsException) error).getInnerExceptions().size() == 1
							? ((UnhandledErrorsException) error).getInnerExceptions().get(0)
							: error;
		}
	}

	void bind(ActivityResults predecessor, StackTraceElement[] errorStack) throws RoboticsException {
		for (Device d : getActivity().getDevices()) {
			ActivityScheduler.getInstance().setResults(d, this, results);
		}

		if (predecessor == null) {
			if(!prepareAndActivate(null, errorStack)) {
				failureReason.setStackTrace(errorStack);
				fail(failureReason);
			}
			return;
		}

		predecessor.provide(result -> {
			if (result == null && !hasSchedules) {
				notifyNotValidInThisContext(errorStack, predecessor);
				return;
			}
			if (result.getStatus() == ActivityResult.Status.IMPOSSIBLE) {
				return;
			}
			if (!prepareAndActivate(result, errorStack) && result.isCompletedWhenActive()) {
				result.observeStatus(status -> {
					if (status == ActivityResult.Status.ACTIVE
							&& (getStatus() == Status.BOUND || getStatus() == Status.LOADED)) {
						if (!result.allowsFreshStartWhenActive() || !prepareAndActivate(null, errorStack)) {
							notifyNotValidInThisContext(errorStack, predecessor);
						}
					}
				}, this::fail);
			}
		}, this::fail);
		if (getException() != null)
			throw getException();
	}

	private void notifyNotValidInThisContext(StackTraceElement[] errorStack, ActivityResults predecessor) {
		RoboticsException err = new RoboticsException("The Activity cannot be executed in this context", failureReason);
		err.setStackTrace(errorStack);
		fail(err);
		for (Device d : getActivity().getDevices()) {
			ActivityScheduler.getInstance().setResults(d, this, predecessor);
		}
	}

	private boolean hasSchedules = false;

	private boolean prepareAndActivate(ActivityResult result, StackTraceElement[] errorStack) throws RoboticsException {
		ActivitySchedule schedule = null;
		try {
			schedule = prepare(result, errorStack);
		} catch (RoboticsException e) {
			failureReason = e;
			return false;
		}
		if (schedule == null) {
			return false;
		}
		ActivitySchedule finalSchedule = schedule;
		finalSchedule.activate();
		finalSchedule.addStatusListener(schedulestatus -> {
			if (schedulestatus == org.roboticsapi.core.activity.ActivitySchedule.Status.TAKEN) {
				runWhenCancelled(finalSchedule::cancel);
			}
		});
		finalSchedule.getResults().provide(scheduleresult -> {
			if (scheduleresult != null) {
				results.addResult(scheduleresult);
			}
		});
		hasSchedules = true;
		return true;
	}

	public abstract ActivitySchedule prepare(ActivityResult result, StackTraceElement[] errorStack)
			throws RoboticsException;

	public ActivityResults getResults() {
		return results;
	}

	public RoboticsException getException() {
		return error;
	}

	public final void cancelExecute() throws RoboticsException {
		synchronized (this.whenCancelled) {
			cancelled = true;
			for (Runnable whenCancelled : this.whenCancelled) {
				whenCancelled.run();
			}
			endExecute();
		}
	}

	@Override
	public String toString() {
		return activity.toString();
	}

	protected void waitForStatus(Predicate<Status> waitFor) throws RoboticsException {
		Object observer = new Object();
		addStatusListener(status -> {
			synchronized (observer) {
				observer.notifyAll();
			}
		});
		synchronized (observer) {
			while (!waitFor.appliesTo(getStatus())) {
				try {
					observer.wait();
				} catch (InterruptedException e) {
					throw new RoboticsException(e);
				}
			}
		}
	}

}