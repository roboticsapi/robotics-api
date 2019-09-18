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
import org.roboticsapi.core.activity.ActivitySchedule;
import org.roboticsapi.core.exception.RoboticsException;

public class ParallelActivity extends AbstractActivity {

	private Activity first;
	private Activity second;

	public ParallelActivity(String name, Activity first, Activity second) {
		super(name, first, second);
		this.first = first;
		this.second = second;
	}

	public ParallelActivity(Activity first, Activity second) {
		super(first + " || " + second, first, second);
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
				ActivitySchedule secondSchedule = secondHandle.prepare(result, errorStack);
				if (firstSchedule == null || secondSchedule == null)
					return null;
				return firstSchedule.withParallel(this, secondSchedule);
			}
		};
	}

}
