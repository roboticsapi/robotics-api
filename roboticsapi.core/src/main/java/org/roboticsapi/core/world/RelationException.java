/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import org.roboticsapi.core.exception.RoboticsException;

public class RelationException extends RoboticsException {

	private static final long serialVersionUID = 3736892213681251256L;

	public RelationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public RelationException(String message, Exception innerException) {
		super(message, innerException);
		// TODO Auto-generated constructor stub
	}

	public RelationException(Exception innerException) {
		super(innerException);
		// TODO Auto-generated constructor stub
	}

}
