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

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.RAPILogger;

public abstract class ActivitySchedule {

	public enum Status {
		READY, PREPARED, ACTIVE, TAKEN, IMPOSSIBLE
	}

	private Status status = Status.READY;
	private final List<Consumer<Status>> statusConsumers = new ArrayList<>();
	private final ActivityResults results;
	private final ActivityResult result;
	private final ActivityHandle activity;
	protected final List<ActivitySchedule> dependencies = new ArrayList<>();

	public ActivitySchedule(ActivityResult result, ActivityHandle activity, ActivityResults results) {
		this.result = result;
		this.activity = activity;
		this.results = results;
	}

	public ActivityResult getResult() {
		return result;
	}

	public ActivityHandle getActivityHandle() {
		return activity;
	}

	public ActivityResults getResults() {
		return results;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		if (this.status == status)
			return;
		RAPILogger.getLogger(ActivitySchedule.class)
				.fine(this.getActivityHandle() + " [" + this.hashCode() + "] changed to " + status);
		this.status = status;
		for (Consumer<Status> consumer : statusConsumers) {
			consumer.accept(status);
		}
	}

	public void addDependency(ActivitySchedule dependency) throws RoboticsException {
		if (status != Status.READY)
			dependency.load();

		if (status != Status.READY && status != Status.PREPARED)
			dependency.activate();

		dependencies.add(dependency);
	}

	public void addStatusListener(Consumer<Status> consumer) {
		consumer.accept(status);
		statusConsumers.add(consumer);
	}

	public final void load() {
		if (status != Status.READY && status != Status.PREPARED)
			return;

		for (ActivitySchedule dependency : dependencies)
			dependency.load();

		doLoad();

		setStatus(Status.PREPARED);
	}

	public final void activate() throws RoboticsException {
		if (status == Status.READY)
			load();

		if (status != Status.PREPARED)
			return;

		if (getActivityHandle().getStatus() == ActivityHandle.Status.RUNNING) {
			setStatus(Status.IMPOSSIBLE);
			return;
		}

		setStatus(Status.ACTIVE);
		for (ActivitySchedule dependency : dependencies)
			dependency.activate();

		getActivityHandle().updateStatus(ActivityHandle.Status.BOUND);
		if (getResult() == null) {
			doActivate();

			setStatus(Status.TAKEN);
			activity.updateStatus(ActivityHandle.Status.RUNNING);
			return;
		}

		getActivityHandle().addStatusListener(status -> {
			if (this.status == ActivitySchedule.Status.ACTIVE && status == ActivityHandle.Status.RUNNING) {
				markImpossible();
			}
		});

		getResult().observeStatus(status -> {
			if (status == ActivityResult.Status.IMPOSSIBLE && getStatus() != Status.IMPOSSIBLE) {
				markImpossible();
			}
		});
		doActivate();

	}

	protected void markTaken() {
		setStatus(Status.TAKEN);
		activity.updateStatus(ActivityHandle.Status.RUNNING);
	}
	
	private void markImpossible() {
		setStatus(Status.IMPOSSIBLE);
		getResults().provide(result -> {
			if (result != null) {
				result.updateStatus(ActivityResult.Status.IMPOSSIBLE);
			}
		});
	}

	public abstract void cancel();

	protected abstract void doLoad();

	protected abstract void doActivate();

	public abstract ActivitySchedule withParallel(ActivityHandle composite, ActivitySchedule otherSchedule)
			throws RoboticsException;

	public abstract ActivitySchedule withResults(ActivityHandle composite, ActivityResults results)
			throws RoboticsException;

}
