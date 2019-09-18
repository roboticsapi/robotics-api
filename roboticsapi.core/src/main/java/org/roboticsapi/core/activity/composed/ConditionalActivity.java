/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity.composed;

import java.util.function.Predicate;

import org.roboticsapi.core.activity.AbstractActivity;
import org.roboticsapi.core.activity.Activity;
import org.roboticsapi.core.activity.ActivityHandle;
import org.roboticsapi.core.activity.ActivityResult;
import org.roboticsapi.core.activity.ActivitySchedule;
import org.roboticsapi.core.exception.RoboticsException;

public class ConditionalActivity extends AbstractActivity {

	private final Predicate<ActivityResult> condition;
	private final Activity ifTrue;
	private final Activity ifFalse;

	public ConditionalActivity(String name, Predicate<ActivityResult> condition, Activity ifTrue, Activity ifFalse) {
		super(name, ifTrue, ifFalse);
		this.condition = condition;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}

	@Override
	public ActivityHandle createHandle() throws RoboticsException {
		ActivityHandle trueHandle = ifTrue.createHandle();
		ActivityHandle falseHandle = ifFalse.createHandle();

		return new ActivityHandle(this) {
			@Override
			public ActivitySchedule prepare(ActivityResult result, StackTraceElement[] errorStack)
					throws RoboticsException {
				if (condition.test(result)) {
					return trueHandle.prepare(result, errorStack);
				} else {
					return falseHandle.prepare(result, errorStack);
				}
			}
		};
	}

}
