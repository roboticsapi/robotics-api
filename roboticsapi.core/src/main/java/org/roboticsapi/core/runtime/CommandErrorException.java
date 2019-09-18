/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.runtime;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandRuntimeException;

public class CommandErrorException extends CommandRuntimeException {
	private static final long serialVersionUID = -504756805841532317L;

	public CommandErrorException(String message, Command command) {
		super(message);
		setCommand(command);
	}

	public CommandErrorException(String message, Command command, Throwable cause) {
		super(message, cause);
		setCommand(command);
	}
}
