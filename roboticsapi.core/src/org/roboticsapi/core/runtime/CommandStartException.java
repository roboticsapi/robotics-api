/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.runtime;

import org.roboticsapi.core.CommandException;

/**
 * An exception notifying about problems starting the Command
 */
public class CommandStartException extends CommandException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new CommandStartException
	 * 
	 * @param message error message
	 */
	public CommandStartException(final String message) {
		super(message);
	}

}
