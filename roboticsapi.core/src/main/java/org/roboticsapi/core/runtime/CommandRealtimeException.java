/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.runtime;

import org.roboticsapi.core.CommandRuntimeException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;

public class CommandRealtimeException extends CommandRuntimeException {
	private static final long serialVersionUID = 1L;
	private RealtimeBoolean exceptionState;

	public CommandRealtimeException(String message) {
		super(message);
	}

	public RealtimeBoolean getExceptionState() {
		if (exceptionState == null) {
			exceptionState = RealtimeBoolean.createFromException(this);
		}

		return exceptionState;
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		// create no stack trace; it will be filled in later
		return this;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CommandRealtimeException)) {
			return false;
		}

		if (!(obj.getClass().equals(getClass()))) {
			return false;
		}

		if (((CommandRealtimeException) obj).getCommand() == null || getCommand() == null) {
			return true;
		} else {
			return ((CommandRealtimeException) obj).getCommand().equals(getCommand());
		}

	}
}
