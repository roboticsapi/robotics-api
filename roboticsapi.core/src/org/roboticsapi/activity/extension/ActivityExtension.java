/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity.extension;

import org.roboticsapi.activity.ActivityScheduler;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.extension.RoboticsObjectListener;

public class ActivityExtension implements RoboticsObjectListener {

	@Override
	public void onAvailable(RoboticsObject object) {
		if (object instanceof RoboticsRuntime) {
			((RoboticsRuntime) object).addCommandHook(ActivityScheduler.instance());
		}
	}

	@Override
	public void onUnavailable(RoboticsObject object) {
		if (object instanceof RoboticsRuntime) {
			((RoboticsRuntime) object).removeCommandHook(ActivityScheduler.instance());
		}
	}

}
