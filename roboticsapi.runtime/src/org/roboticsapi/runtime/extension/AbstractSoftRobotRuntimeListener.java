/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.runtime.SoftRobotRuntime;

public abstract class AbstractSoftRobotRuntimeListener implements RoboticsObjectListener {

	@Override
	public void onAvailable(RoboticsObject runtime) {
		if (runtime instanceof SoftRobotRuntime) {
			onRuntimeAvailable((SoftRobotRuntime) runtime);
		}
	}

	protected abstract void onRuntimeAvailable(SoftRobotRuntime runtime);

	@Override
	public void onUnavailable(RoboticsObject runtime) {
		if (runtime instanceof SoftRobotRuntime) {
			onRuntimeUnavailable((SoftRobotRuntime) runtime);
		}
	}

	protected abstract void onRuntimeUnavailable(SoftRobotRuntime runtime);

}
