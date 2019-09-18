/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

/**
 *
 */
package org.roboticsapi.core.exception;

public class EntityException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public EntityException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param innerException
	 */
	public EntityException(String message, Throwable innerException) {
		super(message, innerException);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param innerException
	 */
	public EntityException(Throwable innerException) {
		super(innerException);
		// TODO Auto-generated constructor stub
	}

}
