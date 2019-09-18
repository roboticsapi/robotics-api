/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

/**
 * An exception notifying about problems when running the Command
 */
public class CommandRuntimeException extends CommandException {
	private static final long serialVersionUID = 1L;
	protected Command command;

	/**
	 * Creates a CommandRuntimeException
	 * 
	 * @param message error message
	 */
	public CommandRuntimeException(final String message) {
		super(message);
	}

	public CommandRuntimeException(final String message, Throwable innerException) {
		super(message, innerException);
	}

	public CommandRuntimeException(final String message, Command command) {
		this(message);
		setCommand(command);
	}

	public CommandRuntimeException(final String message, Throwable innerException, Command command) {
		this(message, innerException);
		setCommand(command);
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;

	}
}
