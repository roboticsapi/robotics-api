/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.mockclass;

import java.util.List;
import java.util.Set;

import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandHandleOperation;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.CommandResultListener;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.CommandStatusListener;

public class MockCommandHandleImpl implements CommandHandle {

	@Override
	public boolean start() throws CommandException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CommandHandleOperation getStartOperation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean scheduleAfter(CommandResult executeAfter) throws CommandException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scheduleWhen(CommandResult executeWhen) throws CommandException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean abort() throws CommandException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CommandHandleOperation getAbortOperation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean cancel() throws CommandException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CommandHandleOperation getCancelOperation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void waitComplete() throws CommandException {
		// TODO Auto-generated method stub

	}

	@Override
	public CommandStatus getStatus() throws CommandException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addStatusListener(CommandStatusListener listener) throws CommandException {
		// TODO Auto-generated method stub

	}

	@Override
	public void throwException(CommandException ex) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<CommandException> getOccurredExceptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startThread(Runnable runnable) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<CommandResult> getPossibleCommandResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CommandResult> getFinalCommandResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<CommandResult> getActiveCommandResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unload() throws CommandException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addCommandResultListener(CommandResultListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeCommandResultListener(CommandResultListener listener) {
		// TODO Auto-generated method stub

	}
}
