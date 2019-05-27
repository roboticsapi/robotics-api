/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.HashSet;
import java.util.Set;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.state.CommandState;

/**
 * Command that waits for a certain amount of time (or infinitely).
 */
public abstract class WaitCommand extends Command {

	private double duration = -1;

	/**
	 * State that becomes active when the wait time has expired.
	 *
	 */
	public class WaitCompleted extends CommandState {
	}

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
	protected WaitCommand(String name, final RoboticsRuntime runtime) {
		super(name);
		setRuntime(runtime);
	}

	/**
	 * Creates a wait command
	 *
	 * @param runtime  robotics runtime to use
	 * @param duration duration in seconds to wait; must not be negative
	 */
	protected WaitCommand(final RoboticsRuntime runtime, final double duration) {
		this(runtime);
		setDuration(duration);
	}

	/**
	 * Creates a wait command
	 *
	 * @param runtime  robotics runtime to use
	 * @param duration duration in seconds to wait; must not be negative
	 */
	protected WaitCommand(String name, final RoboticsRuntime runtime, final double duration) {
		this(name, runtime);
		setDuration(duration);
	}

	/**
	 * Returns a State that becomes active when this WaitCommand's wait time has
	 * expired.
	 *
	 * @return State indicating that wait time has expired
	 */
	public State getWaitCompletedState() {
		return new WaitCompleted().setCommand(this);
	}

	/**
	 * Retrieves the duration of the wait command
	 *
	 * @return duration in seconds to wait, with -1 indicating infinite wait
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * Sets the duration of the wait command
	 *
	 * @param duration duration in seconds to wait; must not be negative
	 */
	public void setDuration(final double duration) {
		if (duration < 0) {
			throw new IllegalArgumentException("Parameter duration must not be negative.");
		}

		this.duration = duration;
	}

	@Override
	protected Set<CommandRealtimeException> collectInnerExceptions() {
		// TODO: InterruptedError when cancelled?
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
		return "WaitCommand<" + getDuration() + "s>";
	}

	@Override
	protected void handleCompletion() throws RoboticsException {
		addDoneStateCondition(getWaitCompletedState().or(getCancelState()));
	}
}
