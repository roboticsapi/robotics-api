/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.commandfilter;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandFilter;
import org.roboticsapi.core.WaitCommand;

public abstract class WaitCommandFilter implements CommandFilter {
	@Override
	public final boolean filter(Command command) {
		return command instanceof WaitCommand && filterWaitCommand((WaitCommand) command);
	}

	protected abstract boolean filterWaitCommand(WaitCommand command);

	@Override
	public final void process(Command command) {
		if (command instanceof WaitCommand) {
			processWaitCommand((WaitCommand) command);
		}
	}

	protected abstract void processWaitCommand(WaitCommand command);
}