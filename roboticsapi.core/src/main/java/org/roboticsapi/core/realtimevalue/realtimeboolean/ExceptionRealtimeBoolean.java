/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.realtimevalue.realtimeboolean;

import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.util.HashCodeUtil;

public class ExceptionRealtimeBoolean extends RealtimeBoolean {
	private final CommandRealtimeException exception;

	ExceptionRealtimeBoolean(CommandRealtimeException exception) {
		super(exception.getCommand());
		this.exception = exception;
	}

	public CommandRealtimeException getException() {
		return exception;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && ((ExceptionRealtimeBoolean) obj).getException().equals(getException());
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(super.hashCode(), getException());
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String toString() {
		return exception.toString();
	}
}