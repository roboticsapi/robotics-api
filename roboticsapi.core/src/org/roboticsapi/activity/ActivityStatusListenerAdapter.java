/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import org.roboticsapi.core.exception.RoboticsException;

public abstract class ActivityStatusListenerAdapter implements ActivityStatusListener {

	@Override
	public void activityStatusChanged(Activity activity, ActivityStatus newStatus) {
		switch (newStatus) {
		case NEW:
			break;
		case SCHEDULED:
			onActivityScheduled();
			break;
		case RUNNING:
			onActivityStarted();
			break;
		case COMPLETED:
			onActivityCompleted();
			break;
		case FAILED:
			onActivityFailed(activity.getException());
			break;
		case MAINTAINING:
			onActivityMaintaining();
			break;
		default:
			break;
		}

	}

	public void onActivityFailed(RoboticsException roboticsException) {
		// empty default implementation
	}

	public void onActivityCompleted() {
		// empty default implementation
	}

	public void onActivityStarted() {
		// empty default implementation
	}

	public void onActivityScheduled() {
		// empty default implementation
	}

	public void onActivityMaintaining() {
		// empty default implementation
	}

}
