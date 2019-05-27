/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.rpi;

public class RPIException extends Exception {

	/**
	 * A serial version UID.
	 */
	private static final long serialVersionUID = 6070219135700641666L;

	public RPIException() {
	}

	public RPIException(final String message) {
		super(message);
	}

	public RPIException(final Throwable innerException) {
		super(innerException);
	}

	public RPIException(final String message, final Throwable innerException) {
		super(message, innerException);
	}

}
