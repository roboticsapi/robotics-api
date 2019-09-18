/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.activity.runtime;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class SleepActivity extends FromCommandActivity {
	private static final int FOREVER = -1;

	public SleepActivity(RoboticsRuntime runtime) throws RoboticsException {
		this("Sleep forever", runtime, FOREVER);
	}

	public SleepActivity(RoboticsRuntime runtime, RealtimeBoolean waitFor) throws RoboticsException {
		this("Sleep until " + waitFor, runtime, waitFor);
	}

	public SleepActivity(RoboticsRuntime runtime, double seconds) throws RoboticsException {
		this("Sleep for " + seconds + "s", runtime, seconds);
	}

	public SleepActivity(String name, RoboticsRuntime runtime) throws RoboticsException {
		this(name, runtime, FOREVER);
	}

	public SleepActivity(String name, RoboticsRuntime runtime, double seconds) throws RoboticsException {
		this(name, runtime, seconds, null, new ArrayList<Device>());
	}

	public SleepActivity(String name, RoboticsRuntime runtime, RealtimeBoolean waitFor) throws RoboticsException {
		this(name, runtime, FOREVER, waitFor, new ArrayList<Device>());
	}

	public SleepActivity(String name, RoboticsRuntime runtime, double seconds, RealtimeBoolean waitFor,
			List<Device> devices) throws RoboticsException {
		super(name, () -> createCommand(name, runtime, seconds, waitFor), devices.toArray(new Device[0]));
	}

	private static Command createCommand(String name, RoboticsRuntime runtime, double seconds, RealtimeBoolean waitFor)
			throws RoboticsException {
		if (seconds == FOREVER) {
			if (waitFor != null) {
				return runtime.createWaitCommand(name, waitFor);
			} else {
				return runtime.createWaitCommand(name);
			}
		} else {
			return runtime.createWaitCommand(name, seconds);
		}
	}

}
