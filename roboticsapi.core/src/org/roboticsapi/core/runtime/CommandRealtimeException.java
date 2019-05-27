/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.runtime;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandRuntimeException;
import org.roboticsapi.core.State;
import org.roboticsapi.core.state.ExceptionState;

public class CommandRealtimeException extends CommandRuntimeException {

	private static final long serialVersionUID = 1L;
	private ExceptionState exceptionState;

	public CommandRealtimeException(String message) {
		super(message);
	}

	public CommandRealtimeException(String message, Command command) {
		super(message, command);

	}

	public State getExceptionState() {
		if (exceptionState == null) {
			exceptionState = new ExceptionState(this);
		}

		return exceptionState;
	}

	// TODO: Currently not exactly solved version, because two
	// CommandRealtimeException instances with different Commands have the same
	// hashcode.
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// This branch is redundant because of the following branch
		// if (!(obj instanceof CommandRealtimeException)) {
		// return false;
		// }

		// "For any non-null reference value x, x.equals(null) should return false."
		// (Source: Java API - Class Object:
		// http://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#equals(java.lang.Object)
		if ((obj == null) || !(obj.getClass().equals(getClass()))) {
			return false;
		}

		if (((CommandRealtimeException) obj).getCommand() == null || getCommand() == null) {
			return true;
		} else {
			return ((CommandRealtimeException) obj).getCommand().equals(getCommand());
		}

	}
}
