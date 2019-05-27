/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.runtime;

import org.roboticsapi.core.CommandRuntimeException;

public class CommandSensorListenerException extends CommandRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5737025017104287715L;

	public CommandSensorListenerException(String message) {
		super(message);
	}

	public CommandSensorListenerException(String message, Throwable innerException) {
		super(message, innerException);
	}

}
