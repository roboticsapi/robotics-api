/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.runtime.CommandRealtimeException;

public abstract class ActivityRealtimeException extends CommandRealtimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActivityRealtimeException(String message) {
		super(message);
	}

	public ActivityRealtimeException(String message, Command command) {
		super(message, command);
	}

}
