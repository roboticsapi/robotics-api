/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping;

/**
 * An exception stating that an error occured during command mapping
 */
public class MappingException extends Exception // TODO: correct it
{
	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a mapping exception
	 * 
	 * @param message error message
	 */
	public MappingException(final String message) {
		super(message);
	}

	public MappingException(Exception innerException) {
		super(innerException);
	}

	public MappingException(final String message, Exception innerException) {
		super(message, innerException);
	}

}
