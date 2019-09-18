/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import java.util.List;
import java.util.Set;

/**
 * Handle to a Command that has been loaded by a RoboticsRuntime successfully.
 */
public interface CommandHandle {

	/**
	 * Starts execution of the command
	 *
	 * @return true if succeeded
	 * @throws CommandException
	 */
	boolean start() throws CommandException;

	CommandHandleOperation getStartOperation();

	/**
	 * Schedules the execution of the command after the given other command
	 *
	 * @param executeAfter command result to execute this command after
	 * @return true if succeeded
	 * @throws CommandException
	 */
	boolean scheduleAfter(CommandResult executeAfter) throws CommandException;

	/**
	 * Schedules the execution of the command when a command result occurs
	 *
	 * @param executeWhen command result to trigger the execution of this command
	 * @return true if succeeded
	 * @throws CommandException
	 */
	boolean scheduleWhen(CommandResult executeWhen) throws CommandException;

	/**
	 * Aborts the net
	 *
	 * @return true if succeeded
	 * @throws CommandException
	 */
	boolean abort() throws CommandException;

	CommandHandleOperation getAbortOperation();

	/**
	 * Cancels the net asynchronously (!)
	 *
	 * @return true if succeeded
	 * @throws CommandException
	 */
	boolean cancel() throws CommandException;

	CommandHandleOperation getCancelOperation();

	/**
	 * Waits for the net to complete execution
	 *
	 * @throws CommandException
	 */
	void waitComplete() throws CommandException;

	/**
	 * Retrieves the net status
	 *
	 * @return current net status
	 * @throws CommandException
	 */
	CommandStatus getStatus() throws CommandException;

	/**
	 * Adds a net status listener to the net
	 *
	 * @param listener status listener for the net
	 * @throws CommandException
	 */
	void addStatusListener(CommandStatusListener listener) throws CommandException;

	/**
	 * Sets an exception to be thrown when the command is completed
	 *
	 * @param ex exception to throw
	 */
	void throwException(CommandException ex);

	/**
	 * Gets the exception that has occurred during runtime of the command, if any.
	 *
	 * @return The exception that has occurred.
	 */
	List<CommandException> getOccurredExceptions();

	/**
	 * Starts a thread as an effect of an event handler
	 *
	 * @param runnable thread to start
	 */
	void startThread(Runnable runnable);

	/**
	 * Returns the command results that are still possible
	 *
	 * @return list of command results that can still be reached
	 */
	List<CommandResult> getPossibleCommandResults();

	/**
	 * Returns the final command results
	 *
	 * @return list of command results that are active in the end, or null if
	 *         Command is still running
	 */
	List<CommandResult> getFinalCommandResults();

	/**
	 * Returns the command results that are active at the moment
	 *
	 * @return list of command results that are currently active
	 */
	Set<CommandResult> getActiveCommandResults();

	/**
	 * Adds a listener for the status of {@link CommandResult}s
	 *
	 * @param listener listener to add
	 */
	void addCommandResultListener(CommandResultListener listener);

	/**
	 * Removes a listener for the status of {@link CommandResult}s
	 *
	 * @param listener listener to remove
	 */
	void removeCommandResultListener(CommandResultListener listener);

	void unload() throws CommandException;

}
