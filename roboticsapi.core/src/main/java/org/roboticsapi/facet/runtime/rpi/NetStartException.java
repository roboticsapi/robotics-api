/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi;

/**
 * An exception notifying about problems starting the net
 */
public class NetStartException extends RpiException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new net start exception
	 * 
	 * @param message error message
	 */
	public NetStartException(final String message) {
		super(message);
	}

}
