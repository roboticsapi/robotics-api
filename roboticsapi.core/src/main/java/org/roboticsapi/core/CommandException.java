/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

/**
 * 
 */
package org.roboticsapi.core;

import org.roboticsapi.core.exception.RoboticsException;

/**
 * An exception that occured in the RoboticsRuntime layer.
 */
public class CommandException extends RoboticsException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new instance of CommandException
	 * 
	 * @param message The exception message
	 */
	public CommandException(final String message) {
		super(message);
	}

	public CommandException(final String message, final Throwable innerException) {
		super(message, innerException);
	}

}
