/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.mockclass;

import java.util.Set;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;

public class MockCommand extends Command {

	public MockCommand(String name, RoboticsRuntime runtime) {
		super(name, runtime);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Set<CommandRealtimeException> collectInnerExceptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CommandHandle createHandle() throws RoboticsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void overrideWatchdogTimeoutInternal(double watchdogTimeout) throws RoboticsException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void relaxWatchdogTimeoutInternal(double watchdogTimeout) throws RoboticsException {
		// TODO Auto-generated method stub

	}
}
