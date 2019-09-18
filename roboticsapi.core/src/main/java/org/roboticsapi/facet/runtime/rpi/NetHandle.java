/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

import java.util.List;

/**
 * A handle to a net
 */
public interface NetHandle {

	/**
	 * Starts execution of the net
	 *
	 * @return true if succeeded
	 * @throws RpiException
	 */
	boolean start() throws RpiException;

	/**
	 * Schedules a net after the completion of other nets
	 *
	 * @param results net results to schedule this net after
	 * @return true if successful
	 * @throws RpiException
	 */
	boolean scheduleAfter(List<NetResult> results) throws RpiException;

	/**
	 * Schedules a net when certain results occur
	 *
	 * @param results net results to schedule this net after
	 * @return true if successful
	 * @throws RpiException
	 */
	boolean scheduleWhen(List<NetResult> results) throws RpiException;

	/**
	 * Aborts the net
	 *
	 * @return true if succeeded
	 * @throws RpiException
	 */
	boolean abort() throws RpiException;

	/**
	 * Cancels the net
	 *
	 * @return true if succeeded
	 * @throws RpiException
	 */
	boolean cancel() throws RpiException;

	/**
	 * Unloads the net
	 *
	 * @throws RpiException
	 */
	void unload() throws RpiException;

	/**
	 * Waits for the net to complete execution
	 *
	 * @throws RpiException
	 */
	void waitComplete() throws RpiException;

	/**
	 * Retrieves the net status
	 *
	 * @return current net status
	 * @throws RpiException
	 */
	NetStatus getStatus() throws RpiException;

	/**
	 * Adds a net status listener to the net
	 *
	 * @param listener status listener for the net
	 * @throws RpiException
	 */
	void addStatusListener(NetStatusListener listener) throws RpiException;

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

	NetResult createResult(NetcommValue condition);
}
