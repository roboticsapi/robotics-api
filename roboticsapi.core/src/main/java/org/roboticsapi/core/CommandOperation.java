/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.Arrays;

import org.roboticsapi.core.exception.RoboticsException;

public abstract class CommandOperation {

	private final Command command;

	public CommandOperation(Command command) {
		this.command = command;
	}

	public Command getCommand() {
		return command;
	}

	public boolean execute() throws RoboticsException {
		getCommand().load();
		return getCommand().getRuntime().executeOperations(Arrays.asList(this.getHandleOperation())) != null;
	}

	public abstract CommandHandleOperation getHandleOperation();
}
