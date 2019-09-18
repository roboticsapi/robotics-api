/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import org.roboticsapi.core.exception.RoboticsException;

/**
 * An exception stating that an error occured during command mapping
 */
public class MappingException extends RoboticsException {
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
