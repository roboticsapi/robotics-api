/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.state.OrState;

/**
 * TransactionCommands consist of multiple other {@link Commands} that are
 * executed within one realtime context.
 * 
 * The execution of the inner Commands has to be controlled by defining
 * appropriate {@link EventEffect}s that start and stop the respective Commands.
 */
public abstract class TransactionCommand extends Command {

	public class ConditionalCommand {
		private final BooleanSensor condition;
		private final Command command;

		public ConditionalCommand(BooleanSensor condition, Command command) {
			super();
			this.condition = condition;
			this.command = command;
		}

		public BooleanSensor getCondition() {
			return condition;
		}

		public Command getCommand() {
			return command;
		}
	}

	private final List<Command> commands = new Vector<Command>();
	private final List<ConditionalCommand> startCommands = new Vector<ConditionalCommand>();

	protected TransactionCommand(final String name, RoboticsRuntime runtime) {
		super(name);
		setRuntime(runtime);
		addStateValidator(new StateValidator() {
			@Override
			public boolean validateState(State event) throws RoboticsException {
				// allow events allowed in children
				for (Command child : getCommandsInTransaction()) {
					if (child.validateState(event)) {
						return true;
					}
				}
				return false;
			}
		});
	}

	protected TransactionCommand(final String name, RoboticsRuntime runtime, Command... commands)
			throws RoboticsException {
		this(name, runtime);
		for (Command command : commands) {
			addCommand(command);
		}
	}

	protected TransactionCommand(RoboticsRuntime runtime) {
		this("Transaction", runtime);
	}

	protected TransactionCommand(RoboticsRuntime runtime, Command... commands) throws RoboticsException {
		this("Transaction", runtime, commands);
	}

	/**
	 * Adds a {@link Command} to this TransactionCommand's inner Commands. The
	 * specified Command is not automatically started.
	 * 
	 * @param command the Command to add to this TransactionCommand
	 * @throws RoboticsException thrown if Command could not be added
	 */
	public void addCommand(Command command) throws RoboticsException {
		if (getRuntime() == null) {
			setRuntime(command.getRuntime());
		}
		if (getRuntime() != command.getRuntime()) {
			throw new RoboticsException("Invalid runtime for command.");
		}
		if (!commands.contains(command)) {
			commands.add(command);
		}
	}

	/**
	 * Adds a {@link Command} to this TransactionCommand's inner Commands. The
	 * specified Command will be automatically started when the TransactionCommand
	 * is started.
	 * 
	 * @param command the Command to add
	 * @throws RoboticsException thrown if Command could not be added
	 */
	public void addStartCommand(Command command) throws RoboticsException {
		addStartCommand(null, command);
	}

	/**
	 * Adds a {@link Command} to this TransactionCommand's inner Commands. The
	 * specified Command will be automatically started when the TransactionCommand
	 * is started and at the same time, the given {@link BooleanSensor} measures the
	 * value 'true'.
	 * 
	 * @param condition the BooleanSensor guarding the Command's start
	 * @param command   the Command to add
	 * @throws RoboticsException thrown if Command could not be added
	 */
	public void addStartCommand(BooleanSensor condition, Command command) throws RoboticsException {
		if (isSealed()) {
			throw new RoboticsException("Command is already sealed");
		}

		if (!commands.contains(command)) {
			addCommand(command);
		}
		startCommands.add(new ConditionalCommand(condition, command));
	}

	/**
	 * Retrieves all commands contained in this TransactionCommand.
	 * 
	 * @return List of all commands
	 */
	public List<Command> getCommandsInTransaction() {
		return Collections.unmodifiableList(commands);
	}

	/**
	 * Retrieves all commands contained in this TransactionCommand that are
	 * automatically started.
	 * 
	 * @return List of the start commands
	 */
	public List<ConditionalCommand> getStartCommands() {
		return Collections.unmodifiableList(startCommands);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.core.Command#setCommandHandle(org.roboticsapi.core.
	 * runtime.CommandHandle)
	 */
	@Override
	public void setCommandHandle(final CommandHandle handle) {
		super.setCommandHandle(handle);
		for (final Command cmd : getCommandsInTransaction()) {
			cmd.setCommandHandle(handle);
		}
	}

	// @Override
	// public List<Observer<?>> getObservers() {
	// List<Observer<?>> monitors = new Vector<Observer<?>>(
	// super.getObservers());
	//
	// for (Command c : getCommandsInTransaction()) {
	// monitors.addAll(c.getObservers());
	// }
	//
	// return monitors;
	// }

	@Override
	protected Set<CommandRealtimeException> collectInnerExceptions() {

		final Set<CommandRealtimeException> errors = new HashSet<CommandRealtimeException>();

		for (Command c : getCommandsInTransaction()) {
			errors.addAll(c.getExceptions());
		}

		return errors;
	}

	@Override
	protected void handleUnhandledInnerExceptions() throws RoboticsException {
		for (Command c : getCommandsInTransaction()) {
			c.seal();
		}

		super.handleUnhandledInnerExceptions();
	}

	@Override
	protected void overrideWatchdogTimeoutInternal(double watchdogTimeout) throws RoboticsException {
		for (Command c : commands) {
			c.overrideWatchdogTimeout(watchdogTimeout);
		}
	}

	@Override
	protected void relaxWatchdogTimeoutInternal(double watchdogTimeout) throws RoboticsException {
		for (Command c : commands) {
			c.relaxWatchdogTimeout(watchdogTimeout);
		}

	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("TransactionCommand<");

		for (Command c : getCommandsInTransaction()) {
			sb.append(c.toString() + ",");
		}

		sb.deleteCharAt(sb.length() - 1);

		sb.append(">");

		return sb.toString();
	}

	@Override
	protected void handleCompletion() throws RoboticsException {
		OrState anyactive = new OrState();
		for (Command cmd : getCommandsInTransaction()) {
			anyactive.addState(cmd.getActiveState());
		}
		addDoneStateCondition(anyactive.not());
	}
}
