/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.eventhandler;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.TargetedEventHandler;

/**
 * An event handler stopping a given command
 */
public class CommandStopper extends TargetedEventHandler {

	public CommandStopper() {
		super();
	}

	/**
	 * Creates a new command stopper (event handler stopping a command once an event
	 * occurs)
	 * 
	 * @param event   event handled
	 * @param command command to stop
	 */
	public CommandStopper(final Command command) {
		super(null, command);
	}
}
