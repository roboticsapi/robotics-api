/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.controlcore.dioprotocol;

public class DIOStatement {
	private String tag;
	private DIOCommand command;

	public DIOStatement() {
		this("", null);
	}

	public DIOStatement(String tag, DIOCommand command) {
		this.setTag(tag);
		this.setCommand(command);
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public DIOCommand getCommand() {
		return command;
	}

	public void setCommand(DIOCommand command) {
		this.command = command;
	}

	@Override
	public String toString() {
		return tag + "=" + command.toString();
	}
}
