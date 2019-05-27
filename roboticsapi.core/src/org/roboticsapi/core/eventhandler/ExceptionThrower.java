/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.eventhandler;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.EventEffect;
import org.roboticsapi.core.runtime.CommandRealtimeException;

public class ExceptionThrower extends EventEffect {

	private CommandRealtimeException thrownException;

	public ExceptionThrower(Command command, CommandRealtimeException thrownException) {
		super(command);
		this.setThrownException(thrownException);
	}

	public ExceptionThrower(CommandRealtimeException thrownException) {
		this.setThrownException(thrownException);
	}

	public CommandRealtimeException getThrownException() {
		return thrownException;
	}

	public void setThrownException(CommandRealtimeException thrownException) {
		this.thrownException = thrownException;
		thrownException.setCommand(getContext());
	}

	@Override
	public void setContext(Command context) {
		super.setContext(context);

		if (thrownException != null) {
			thrownException.setCommand(context);
		}
	}

}
