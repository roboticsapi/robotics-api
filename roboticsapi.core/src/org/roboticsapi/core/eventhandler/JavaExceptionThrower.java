/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.eventhandler;

import org.roboticsapi.core.CommandRuntimeException;

/**
 * An event handler throwing an exception
 */
public class JavaExceptionThrower extends JavaEffect {
	private final CommandRuntimeException exception;

	public CommandRuntimeException getException() {
		return exception;
	}

	public JavaExceptionThrower(final CommandRuntimeException exception) {
		super();
		this.exception = exception;
	}

}
