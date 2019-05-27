/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

public class PreviousActivityFailedException extends ActivitySchedulingException {
	private static final long serialVersionUID = -8273418708048267945L;

	public PreviousActivityFailedException(Throwable innerException) {
		super("Previous activity excution failed!", innerException);
	}
}
