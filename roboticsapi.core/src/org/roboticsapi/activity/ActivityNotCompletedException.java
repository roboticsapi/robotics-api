/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

/**
 * Exception notifying that a required precondition activity is not completed
 * yet.
 */
public class ActivityNotCompletedException extends ActivitySchedulingException {
	private static final long serialVersionUID = 1L;
	private final Activity activity;

	/**
	 * Creates a new exception notifying that a required precondition activity is
	 * not completed yet.
	 *
	 * @param activity the activity
	 */
	public ActivityNotCompletedException(Activity activity) {
		super("Activity not completed: " + activity.getName());
		this.activity = activity;
	}

	/**
	 * Gets the activity.
	 *
	 * @return the activity
	 */
	public Activity getActivity() {

		return activity;
	}
}
