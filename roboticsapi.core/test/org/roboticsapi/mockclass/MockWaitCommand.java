/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.WaitCommand;

public class MockWaitCommand extends WaitCommand {
	public MockWaitCommand(RoboticsRuntime runtime) {
		super(runtime);
	}

	public MockWaitCommand(String name, final RoboticsRuntime runtime) {
		super(name, runtime);
	}

	public MockWaitCommand(final RoboticsRuntime runtime, final double duration) {
		super(runtime, duration);
	}

	public MockWaitCommand(String name, final RoboticsRuntime runtime, final double duration) {
		super(name, runtime, duration);
	}
}
