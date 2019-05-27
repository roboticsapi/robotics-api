/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core;

import org.roboticsapi.core.exception.RoboticsException;

/**
 * An exception stating that given parameters are invalid
 */
public class InvalidParametersException extends RoboticsException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an invalid parameters exception
	 * 
	 * @param message error message
	 */
	public InvalidParametersException(final String message) {
		super("Invalid device parameters: " + message);
	}
}
