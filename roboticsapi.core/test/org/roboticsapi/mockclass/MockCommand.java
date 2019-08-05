/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import java.util.HashSet;
import java.util.Set;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;

public class MockCommand extends Command {
	public MockCommand() {
		this("MockCommand");
	}

	public MockCommand(String name) {
		super(name);
	}

	@Override
	public void setRuntime(final RoboticsRuntime runtime) {
		super.setRuntime(runtime);
	}

	@Override
	protected Set<CommandRealtimeException> collectInnerExceptions() {
		return new HashSet<CommandRealtimeException>();
	}

	@Override
	protected void handleCompletion() throws RoboticsException {
	}

	@Override
	protected void overrideWatchdogTimeoutInternal(double watchdogTimeout) throws RoboticsException {
	}

	@Override
	protected void relaxWatchdogTimeoutInternal(double watchdogTimeout) throws RoboticsException {
	}
}
