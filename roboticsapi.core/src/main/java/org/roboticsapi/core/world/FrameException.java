/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.exception.RoboticsException;

/**
 * Denotes a problem that occurred in the context of dealing with some
 * org.roboticsapi.core.world.Frame.
 */
public class FrameException extends RoboticsException {

	private static final long serialVersionUID = 1L;

	public FrameException(String message) {
		super(message);
	}

	public FrameException(String message, Exception innerException) {
		super(message, innerException);
	}

	public FrameException(Exception innerException) {
		super(innerException);
	}

}
