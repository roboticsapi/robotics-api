/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.exception;

/**
 * An exception that can occur during initialization of {@code RoboticsObject}s.
 */
public class InitializationException extends RoboticsException {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -3219744616970769794L;

	/**
	 * Constructs a new {@code InitializationException}.
	 * 
	 * @param message the message
	 */
	public InitializationException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code InitializationException}.
	 * 
	 * @param message        the message
	 * @param innerException the inner exception
	 */
	public InitializationException(String message, Exception innerException) {
		super(message, innerException);
	}

}
