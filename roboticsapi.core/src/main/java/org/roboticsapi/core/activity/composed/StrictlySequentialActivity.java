/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity.composed;

import org.roboticsapi.core.activity.AbstractActivity;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActivityHandle;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.ActivityResultContainer;
import org.roboticsapi.core.activity.ActivitySchedule;
import org.roboticsapi.core.exception.RoboticsException;

public class StrictlySequentialActivity extends AbstractActivity {

	private final Activity first;
	private final Activity second;

	public StrictlySequentialActivity(String name, Activity first, Activity second) {
		super(name, first, second);
		this.first = first;
		this.second = second;
	}

	public StrictlySequentialActivity(Activity first, Activity second) {
		super(first + " -> " + second, first, second);
		this.first = first;
		this.second = second;
	}

	@Override
	public ActivityHandle createHandle() throws RoboticsException {
		ActivityHandle firstHandle = first.createHandle();
		ActivityHandle secondHandle = second.createHandle();
		return new ActivityHandle(this) {

			@Override
			public ActivitySchedule prepare(ActivityResult result, StackTraceElement[] errorStack)
					throws RoboticsException {
				ActivitySchedule firstSchedule = firstHandle.prepare(result, errorStack);
				if (firstSchedule == null) {
					return null;
				}
				ActivityResultContainer results = new ActivityResultContainer();
				ActivitySchedule ret = firstSchedule.withResults(this, results);
				firstSchedule.getResults().provide(firstResult -> {
					if (firstResult == null || firstResult.getStatus() == ActivityResult.Status.IMPOSSIBLE) {
						return;
					}
					if (!firstResult.isCompletedWhenActive())
						return;
					ActivitySchedule secondSchedule = secondHandle.prepare(firstResult, errorStack);
					if (secondSchedule == null)
						return;
					secondSchedule.addStatusListener(status -> {
						if (status == ActivitySchedule.Status.TAKEN) {
							runWhenCancelled(secondSchedule::cancel);
						}
					});
					secondSchedule.getResults().provide(innerResult -> {
						if (innerResult != null) {
							results.addResult(innerResult);
						}
					});
					ret.addDependency(secondSchedule);
				}, this::fail);
				if (getException() != null)
					throw getException();
				return ret;
			}

		};
	}

}
