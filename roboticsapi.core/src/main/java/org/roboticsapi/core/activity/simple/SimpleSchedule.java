/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity.simple;

import org.roboticsapi.core.activity.ActivityHandle;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.ActivityResults;
import org.roboticsapi.core.activity.ActivitySchedule;
import org.roboticsapi.core.exception.RoboticsException;

public class SimpleSchedule extends ActivitySchedule {

	private final Runnable task;

	public SimpleSchedule(ActivityResult result, ActivityHandle activity, ActivityResults results, Runnable task) {
		super(result, activity, results);
		this.task = task;
	}

	public Runnable getTask() {
		return task;
	}

	public void doLoad() {
	}

	@Override
	public void doActivate() {
		if (task == null) {
			return;
		}
		getResult().observeStatus(status -> {
			if (status == ActivityResult.Status.ACTIVE) {
				new Thread(() -> {
					task.run();
					getResults().provide(result -> {
						if (result instanceof SimpleResult && result.getStatus() == ActivityResult.Status.POSSIBLE) {
							((SimpleResult) result).updateStatus(ActivityResult.Status.IMPOSSIBLE);
						}
					});
				}).start();
			}
		});
	}

	@Override
	public ActivitySchedule withParallel(ActivityHandle composite, ActivitySchedule otherSchedule)
			throws RoboticsException {
		if (!(otherSchedule instanceof SimpleSchedule)) {
			throw new IllegalArgumentException();
		}
		SimpleSchedule other = (SimpleSchedule) otherSchedule;
		ActivityResult myResult = getResult().and(other.getResult());
		ActivityResults myResults = getResults().cross(other.getResults());

		SimpleSchedule ret = new SimpleSchedule(myResult, composite, myResults, () -> {
			new Thread(SimpleSchedule.this.getTask()).start();
			new Thread(other.getTask()).start();
		});
		for (ActivitySchedule dependency : dependencies)
			ret.addDependency(dependency);
		for (ActivitySchedule dependency : ((SimpleSchedule) otherSchedule).dependencies)
			ret.addDependency(dependency);
		return ret;
	}

	@Override
	public ActivitySchedule withResults(ActivityHandle composite, ActivityResults results) throws RoboticsException {
		SimpleSchedule ret = new SimpleSchedule(getResult(), composite, results, null);
		for (ActivitySchedule dependency : dependencies)
			ret.addDependency(dependency);
		return ret;
	}

	@Override
	public void cancel() {
		// TODO: cancel the task
	}

}
