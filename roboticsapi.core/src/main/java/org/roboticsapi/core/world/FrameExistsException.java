/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

public class FrameExistsException extends FrameException {

	private static final long serialVersionUID = 1L;

	public FrameExistsException(String message) {
		super(message);
	}

	public FrameExistsException(String message, Exception innerException) {
		super(message, innerException);
	}

	public FrameExistsException(Exception innerException) {
		super(innerException);
	}

}
