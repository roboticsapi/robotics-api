/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import org.roboticsapi.core.exception.RoboticsException;

public class ActivitySchedulingException extends RoboticsException {

	/**
	 *
	 */
	private static final long serialVersionUID = 742766587656467106L;

	public ActivitySchedulingException(Throwable innerException) {
		super(innerException);
	}

	public ActivitySchedulingException(String message, Throwable innerException) {
		super(message, innerException);
	}

	public ActivitySchedulingException(String message) {
		super(message);
	}
}
