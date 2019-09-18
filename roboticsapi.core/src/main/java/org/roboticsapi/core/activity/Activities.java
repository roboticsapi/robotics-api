/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity;

import java.util.function.Predicate;

import org.roboticsapi.core.activity.composed.ConditionalActivity;
import org.roboticsapi.core.activity.composed.ParallelActivity;
import org.roboticsapi.core.activity.composed.StrictlySequentialActivity;

public class Activities {

	public static Activity parallel(Activity first, Activity... further) {
		for (int i = 0; i < further.length; i++) {
			if (further[i] == null)
				continue;
			else if (first == null)
				first = further[i];
			else
				first = new ParallelActivity(first, further[i]);
		}
		return first;
	}

	public static Activity parallel(String name, Activity first, Activity... further) {
		for (int i = 0; i < further.length; i++) {
			if (further[i] == null)
				continue;
			else if (first == null)
				first = further[i];
			else
				first = new ParallelActivity(name, first, further[i]);
		}
		return first;
	}

	public static Activity strictlySequential(Activity first, Activity... further) {
		for (int i = 0; i < further.length; i++) {
			if (further[i] == null)
				continue;
			else if (first == null)
				first = further[i];
			else
				first = new StrictlySequentialActivity(first, further[i]);
		}
		return first;
	}

	public static Activity strictlySequential(String name, Activity first, Activity... further) {
		for (int i = 0; i < further.length; i++) {
			if (further[i] == null)
				continue;
			else if (first == null)
				first = further[i];
			else
				first = new StrictlySequentialActivity(name, first, further[i]);
		}
		return first;
	}

	public static Activity conditional(String name, Predicate<ActivityResult> condition, Activity ifTrue,
			Activity ifFalse) {
		return new ConditionalActivity(name, condition, ifTrue, ifFalse);
	}
}
