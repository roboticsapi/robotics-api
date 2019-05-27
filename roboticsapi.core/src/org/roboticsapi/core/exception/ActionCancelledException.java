/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.exception;

import org.roboticsapi.core.Action;
import org.roboticsapi.core.ActionRealtimeException;

public class ActionCancelledException extends ActionRealtimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActionCancelledException(Action action) {
		super(action, "Action execution has been cancelled");
	}

}
