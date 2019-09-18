/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

/**
 *
 */
package org.roboticsapi.core.realtimevalue;

import org.roboticsapi.core.exception.RoboticsException;

/**
 * An exception that occurred during reading a RealtimeValue.
 */
public class RealtimeValueReadException extends RoboticsException {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -7262029987677198313L;

	/**
	 * Constructor.
	 *
	 * @param message The message
	 */
	public RealtimeValueReadException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message        The message
	 * @param innerException The inner exception
	 */
	public RealtimeValueReadException(String message, Exception innerException) {
		super(message, innerException);
	}

	/**
	 * Constructor.
	 *
	 * @param innerException The inner exception
	 */
	public RealtimeValueReadException(Exception innerException) {
		super(innerException);
	}

}
