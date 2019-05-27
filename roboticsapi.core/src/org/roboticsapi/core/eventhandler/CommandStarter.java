/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.eventhandler;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.TargetedEventHandler;
import org.roboticsapi.core.TransactionCommand;

/**
 * An event handler starting another command
 */
public class CommandStarter extends TargetedEventHandler {

	/**
	 * Creates a new command starter (event handler starting another command when an
	 * event occurs)
	 * 
	 * @param event   event handled
	 * @param command command to start
	 */
	public CommandStarter(final Command command) {
		super(null, command);
	}

	@Override
	protected void validate() {
		super.validate();

		if (getContext() == null) {
			return;
		}

		if (!(getContext() instanceof TransactionCommand)) {
			throw new IllegalArgumentException("CommandStarter can only be used in TransactionCommands");
		}

		if (getContext() == getTarget()) {
			throw new IllegalArgumentException("CommandStarter can only start a different Command");
		}

		if (!((TransactionCommand) getContext()).getCommandsInTransaction().contains(getTarget())) {
			throw new IllegalArgumentException("CommandStarter can only start another Command in the transaction.");
		}
	}
}
