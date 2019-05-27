/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.action;

import org.roboticsapi.core.exception.RoboticsException;

public class CartesianJoggingException extends RoboticsException {
	private static final long serialVersionUID = 1L;

	public CartesianJoggingException(Exception innerException) {
		super(innerException);
	}

	public CartesianJoggingException(String string, Exception innerException) {
		super(string, innerException);
	}

	public CartesianJoggingException(String string) {
		super(string);
	}
}
