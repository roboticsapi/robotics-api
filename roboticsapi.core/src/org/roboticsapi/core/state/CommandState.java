/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.state;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.State;
import org.roboticsapi.core.util.HashCodeUtil;

/**
 * An event triggered by a Command.
 */
public abstract class CommandState extends State {
	private Command command;

	/**
	 * Sets the command of the event
	 * 
	 * @param command command to apply event to, or null to apply to any command
	 * @return the event
	 */
	public State setCommand(final Command command) {
		this.command = command;
		return this;
	}

	public Command getCommand() {
		return command;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj)
				&& (command == null || ((CommandState) obj).command == null || command == ((CommandState) obj).command);
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(super.hashCode(), command);
	}

	@Override
	public boolean subclassEquals(State other) {
		return super.subclassEquals(other) && ((CommandState) other).command == command;
	}

	@Override
	public String toString() {
		return super.toString() + "<" + command + ">";
	}
}
