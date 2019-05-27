/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.exception;

/**
 * An exception that occurred if a command can not be executed due to invalid or
 * missing configuration parameters.
 */
public class UnsupportedActionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1324028095197023208L;

	/**
	 * Constructor
	 * 
	 * @param message The message
	 */
	public UnsupportedActionException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message        The message
	 * @param innerException An inner exception
	 */
	public UnsupportedActionException(String message, Exception innerException) {
		super(message, innerException);
	}

	/**
	 * Constructor
	 * 
	 * @param innerException An inner exception
	 */
	public UnsupportedActionException(Exception innerException) {
		super(innerException);
	}

}
