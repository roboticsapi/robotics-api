/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.runtime;

import org.roboticsapi.core.CommandRuntimeException;

public class CommandJavaExecutorException extends CommandRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 698804492109101269L;

	public CommandJavaExecutorException(String message) {
		super(message);
	}

	public CommandJavaExecutorException(String message, Throwable innerException) {
		super(message, innerException);
	}

}
