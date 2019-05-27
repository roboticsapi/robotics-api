/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.activity;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.runtime.CommandErrorException;

public class ActivityErrorException extends CommandErrorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5550562335232385585L;
	private final RtActivity activity;

	public ActivityErrorException(String message, RtActivity activity) {
		super(message, null);
		this.activity = activity;
	}

	public ActivityErrorException(String message, RtActivity activity, Throwable cause) {
		super(message, null, cause);
		this.activity = activity;
	}

	@Override
	public Command getCommand() {
		return getActivity().getCommand();
	}

	public RtActivity getActivity() {
		return activity;
	}

}
