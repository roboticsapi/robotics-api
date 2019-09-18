/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.runtime.CommandRealtimeException;

public class CommandResult {
	public enum Status {
		POSSIBLE, ACTIVE, IMPOSSIBLE
	};

	private final Command command;
	private final CommandRealtimeException exception;
	private final boolean isCancelled;
	private final boolean isCompleted;
	private final String name;

	public CommandResult(String name, Command command, CommandRealtimeException exception, boolean isCancelled,
			boolean isCompleted) {
		this.name = name;
		this.command = command;
		this.exception = exception;
		this.isCancelled = isCancelled;
		this.isCompleted = isCompleted;
	}

	@Override
	public String toString() {
		return name;
	}

	public Command getCommand() {
		return command;
	}

	public CommandRealtimeException getException() {
		return exception;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public String getName() {
		return name;
	}

}
