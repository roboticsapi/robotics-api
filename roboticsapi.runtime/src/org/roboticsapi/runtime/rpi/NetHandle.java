/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.rpi;

/**
 * A handle to a net
 */
public interface NetHandle {

	/**
	 * Starts execution of the net
	 * 
	 * @return true if succeeded
	 * @throws RPIException
	 */
	boolean start() throws RPIException;

	/**
	 * Schedules a net after the completion of another net
	 * 
	 * @param previousNet net to schedule this net after
	 * @return true if successful
	 * @throws RPIException
	 */
	boolean scheduleAfter(NetHandle previousNet) throws RPIException;

	/**
	 * Aborts the net
	 * 
	 * @return true if succeeded
	 * @throws RPIException
	 */
	boolean abort() throws RPIException;

	/**
	 * Cancels the net
	 * 
	 * @return true if succeeded
	 * @throws RPIException
	 */
	boolean cancel() throws RPIException;

	/**
	 * Unloads the net
	 * 
	 * @throws RPIException
	 */
	void unload() throws RPIException;

	/**
	 * Waits for the net to complete execution
	 * 
	 * @throws RPIException
	 */
	void waitComplete() throws RPIException;

	/**
	 * Retrieves the net status
	 * 
	 * @return current net status
	 * @throws RPIException
	 */
	NetStatus getStatus() throws RPIException;

	/**
	 * Adds a net status listener to the net
	 * 
	 * @param listener status listener for the net
	 * @throws RPIException
	 */
	void addStatusListener(NetStatusListener listener) throws RPIException;

	/**
	 * Suspends transmission of Netcomm values to RCC (use to atomically update
	 * multiple values)
	 */
	void suspendUpdate();

	/**
	 * Resumes transmission of Netcomm values to RCC (use to atomically update
	 * multiple values)
	 */
	void resumeUpdate();

	/**
	 * Retrieves the name of the net
	 * 
	 * @return name of the net
	 */
	String getName();
}
