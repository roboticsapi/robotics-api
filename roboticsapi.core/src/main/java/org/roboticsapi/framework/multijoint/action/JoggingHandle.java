/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.action;

import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandStatusListener;

public abstract class JoggingHandle {

	private CommandHandle commandHandle;
	protected CommandException abortException = null;
	protected boolean valid = true;

	public void setCommandHandle(CommandHandle handle) {
		commandHandle = handle;
	}

	public CommandException getAbortException() {
		return abortException;
	}

	public void addCommandStatusListener(CommandStatusListener listener) throws CommandException {
		if (commandHandle != null) {
			commandHandle.addStatusListener(listener);
		}
	}

	protected void onJoggingAborted(CommandException abortException) {
		this.abortException = abortException;
		invalidate();
	}

	public void invalidate() {
		valid = false;
	}
}
