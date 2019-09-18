/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.roboticsapi.core.runtime.CommandRealtimeException;

public class UnhandledErrorsException extends CommandRealtimeException {
	private static final long serialVersionUID = 5649436175847907702L;
	List<CommandRuntimeException> innerExceptions = new ArrayList<CommandRuntimeException>();

	public UnhandledErrorsException() {
		super("Unhandled errors");
	}

	protected void addInnerException(CommandRuntimeException e) {
		innerExceptions.add(e);
	}

	public List<CommandRuntimeException> getInnerExceptions() {
		return innerExceptions;
	}

	@Override
	public synchronized Throwable getCause() {
		if (!innerExceptions.isEmpty()) {
			return innerExceptions.get(0);
		}
		return null;
	}

	@Override
	public String getMessage() {
		if (innerExceptions.isEmpty()) {
			return super.getMessage();
		} else {
			return Arrays.toString(innerExceptions.toArray());
		}
	}

}
