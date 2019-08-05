/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mockclass;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandStatus;
import org.roboticsapi.core.CommandStatusListener;
import org.roboticsapi.core.util.RAPILogger;

public class MockCommandHandleRAPILoggerImpl extends MockCommandHandleImpl {
	private final int WAIT_TIME = 1000; // 1s

	private boolean started = false;
	private boolean cancelled = false;
	private boolean completed = false;

	private Command command = null;
	private CommandException occuredException = null;

	private final List<CommandStatusListener> statusListener = new ArrayList<CommandStatusListener>();

	public MockCommandHandleRAPILoggerImpl(Command command) {
		this.command = command;
	}

	@Override
	public boolean start() throws CommandException {
		switch (getStatus()) {
		case READY:
			cancelled = false;
			started = true;
			completed = false;

			RAPILogger.getLogger().info("The Command " + command.getName() + " started.");

			break;
		case RUNNING:
			RAPILogger.getLogger().info("The Command " + command.getName() + " is already running.");

			break;
		case TERMINATED:
			RAPILogger.getLogger().info("The Command " + command.getName() + " has already completed");

			break;
		case ERROR:
			RAPILogger.getLogger()
					.info("The Command " + command.getName() + " can not start because an error occured.");
		}

		return started;
	}

	@Override
	public boolean scheduleAfter(CommandHandle executeAfter) throws CommandException {
		if (getStatus() == CommandStatus.TERMINATED) {
			return executeAfter.start();
		}

		RAPILogger.getLogger().info("The next Command can not start.");

		return false;
	}

	@Override
	public boolean abort() throws CommandException {
		started = false;
		completed = false;
		cancelled = true;

		RAPILogger.getLogger().info("The Command " + command.getName() + " aborted.");

		return cancelled;
	}

	@Override
	public boolean cancel() throws CommandException {
		RAPILogger.getLogger()
				.info("The Command " + command.getName() + " is going to cancel. This may take " + WAIT_TIME + "ms.");

		try {
			Thread.sleep(WAIT_TIME);
		} catch (InterruptedException e) {
		}

		started = false;
		completed = false;
		cancelled = true;

		RAPILogger.getLogger().info("The Command " + command.getName() + " cancelled.");

		return cancelled;
	}

	@Override
	public void waitComplete() throws CommandException {
		if (getStatus() == CommandStatus.RUNNING) {
			RAPILogger.getLogger().info(
					"The Command " + command.getName() + " is going to complete. This may take " + WAIT_TIME + "ms.");

			try {
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
			}

			started = false;
			completed = true;
			cancelled = false;

			RAPILogger.getLogger().info("The Command " + command.getName() + " completed.");
		} else {
			RAPILogger.getLogger().info("The Command " + command.getName() + " has already completed.");
		}
	}

	@Override
	public CommandStatus getStatus() throws CommandException {
		if (!cancelled) {
			if (!completed) {
				if (!started) {
					return CommandStatus.READY;
				} else {
					return CommandStatus.RUNNING;
				}
			} else {
				return CommandStatus.TERMINATED;
			}
		} else {
			return CommandStatus.ERROR;
		}
	}

	@Override
	public void addStatusListener(CommandStatusListener listener) throws CommandException {
		statusListener.add(listener);
	}

	@Override
	public void throwException(CommandException ex) {
		occuredException = ex;
	}

	@Override
	public CommandException getOccurredException() {
		return occuredException;
	}

	@Override
	public void startThread(Runnable runnable) {
		runnable.run();
	}
}
