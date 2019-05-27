/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.exception;

/**
 * A CommunicationException occurs when network communication between the
 * Robotics API and a RoboticsRuntime has failed.
 */
public class CommunicationException extends RoboticsException {

	private static final long serialVersionUID = 1L;

	public CommunicationException(String message) {
		super(message);
	}

	public CommunicationException(String message, Exception innerException) {
		super(message, innerException);
	}

	public CommunicationException(Exception innerException) {
		super(innerException);
	}

}
