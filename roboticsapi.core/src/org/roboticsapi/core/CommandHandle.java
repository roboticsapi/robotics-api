/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

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

	/**
	 * Schedules the execution of the command after the given other command
	 * 
	 * @param executeAfter command to execute this command after
	 * @return true if succeeded
	 * @throws CommandException
	 */
	boolean scheduleAfter(CommandHandle executeAfter) throws CommandException;

	/**
	 * Aborts the net
	 * 
	 * @return true if succeeded
	 * @throws CommandException
	 */
	boolean abort() throws CommandException;

	/**
	 * Cancels the net asynchronously (!)
	 * 
	 * @return true if succeeded
	 * @throws CommandException
	 */
	boolean cancel() throws CommandException;

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
	CommandException getOccurredException();

	/**
	 * Starts a thread as an effect of an event handler
	 * 
	 * @param runnable thread to start
	 */
	void startThread(Runnable runnable);

}
