/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.CommandStatusListener;

public class MockCommandHandleImpl implements CommandHandle {
	@Override
	public boolean start() throws CommandException {
		return false;
	}

	@Override
	public boolean scheduleAfter(CommandHandle executeAfter) throws CommandException {
		return false;
	}

	@Override
	public boolean abort() throws CommandException {
		return false;
	}

	@Override
	public boolean cancel() throws CommandException {
		return false;
	}

	@Override
	public void waitComplete() throws CommandException {

	}

	@Override
	public CommandStatus getStatus() throws CommandException {
		return null;
	}

	@Override
	public void addStatusListener(CommandStatusListener listener) throws CommandException {

	}

	@Override
	public void throwException(CommandException ex) {

	}

	@Override
	public CommandException getOccurredException() {
		return null;
	}

	@Override
	public void startThread(Runnable runnable) {

	}
}
