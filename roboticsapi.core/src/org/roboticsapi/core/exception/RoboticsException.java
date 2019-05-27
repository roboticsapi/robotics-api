/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.exception;

/**
 * An exception that occurred somewhere in the robotics layer
 */
public class RoboticsException extends Exception {
	private static final long serialVersionUID = 1L;

	public RoboticsException(final String message) {
		super(message);
	}

	public RoboticsException(final String message, final Throwable innerException) {
		super(message, innerException);
	}

	public RoboticsException(final Throwable innerException) {
		super(innerException);
	}
}
