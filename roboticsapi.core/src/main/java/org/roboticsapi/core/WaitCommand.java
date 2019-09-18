/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.HashSet;
import java.util.Set;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.runtime.CommandRealtimeException;

public abstract class WaitCommand extends Command {

	private RealtimeBoolean completionState;

	/**
	 * Creates an (infinite) wait command
	 *
	 * @param runtime robotics runtime to use
	 */
	protected WaitCommand(final RoboticsRuntime runtime) {
		this("Wait", runtime);
	}

	/**
	 * Creates an (infinite) wait command
	 *
	 * @param name    name of the command
	 * @param runtime robotics runtime to use
	 */
	protected WaitCommand(String name, RoboticsRuntime runtime) {
		super(name, runtime);

		commandResults.put(getCancelState(), cancelResult = new CommandResult("Cancelled", this, null, true, true));
	}

	/**
	 * Creates a wait command
	 *
	 * @param runtime  robotics runtime to use
	 * @param duration duration to wait
	 */
	protected WaitCommand(final RoboticsRuntime runtime, final double duration) {
		this("Wait " + duration + "s", runtime, duration);
	}

	/**
	 * Creates a wait command
	 *
	 * @param runtime robotics runtime to use
	 * @param waitFor State to wait for
	 */
	protected WaitCommand(final RoboticsRuntime runtime, final RealtimeBoolean waitFor) {
		this("Wait for " + waitFor, runtime, waitFor);
	}

	/**
	 * Creates a wait command
	 *
	 * @param runtime  robotics runtime to use
	 * @param duration duration to wait
	 */
	protected WaitCommand(String name, final RoboticsRuntime runtime, final double duration) {
		this(name, runtime);
		completionState = getCommandExecutionTime().greater(duration);

		commandResults.put(completionState, completionResult = new CommandResult("Completed", this, null, false, true));
	}

	/**
	 * Creates a wait command
	 *
	 * @param runtime robotics runtime to use
	 * @param waitFor State to wait for
	 */
	protected WaitCommand(String name, final RoboticsRuntime runtime, final RealtimeBoolean waitFor) {
		this(name, runtime);
		completionState = waitFor;

		commandResults.put(completionState, completionResult = new CommandResult("Completed", this, null, false, true));
	}

	@Override
	public CommandHandle load() throws RoboticsException {
		if (!getRuntime().isPresent()) {
			throw new RoboticsException(
					"The runtime '" + getRuntime() + "' is not present (" + getRuntime().getState() + ")");
		}

		return super.load();
	}

	public RealtimeBoolean getWaitCompletedState() {
		return completionState;
	}

	@Override
	protected Set<CommandRealtimeException> collectInnerExceptions() {
		return new HashSet<CommandRealtimeException>();
	}

	@Override
	protected void overrideWatchdogTimeoutInternal(double watchdogTimeout) {
	}

	@Override
	protected void relaxWatchdogTimeoutInternal(double watchdogTimeout) {
	}

	@Override
	public String toString() {
		return "WaitCommand<" + completionState + ">";
	}

	private CommandResult completionResult = null;
	private CommandResult cancelResult = null;

	public CommandResult getCompletionResult() {
		return completionResult;
	}

	public CommandResult getCancelResult() {
		return cancelResult;
	}
}
