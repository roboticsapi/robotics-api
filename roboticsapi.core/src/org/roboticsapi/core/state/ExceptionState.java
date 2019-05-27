/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.state;

import org.roboticsapi.core.State;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.util.HashCodeUtil;

public class ExceptionState extends State {
	private final CommandRealtimeException exception;

	public ExceptionState(CommandRealtimeException exception) {
		this.exception = exception;
	}

	public CommandRealtimeException getException() {
		return exception;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && ((ExceptionState) obj).getException().equals(getException());
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(super.hashCode(), getException());
	}
}