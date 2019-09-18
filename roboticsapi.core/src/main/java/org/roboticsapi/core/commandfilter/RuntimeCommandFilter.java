/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.commandfilter;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandFilter;
import org.roboticsapi.core.RuntimeCommand;

public abstract class RuntimeCommandFilter implements CommandFilter {
	@Override
	public final boolean filter(Command command) {
		return command instanceof RuntimeCommand && filterRuntimeCommand((RuntimeCommand) command);
	}

	protected abstract boolean filterRuntimeCommand(RuntimeCommand command);

	@Override
	public final void process(Command command) {
		if (command instanceof RuntimeCommand) {
			processRuntimeCommand((RuntimeCommand) command);
		}
	}

	protected abstract void processRuntimeCommand(RuntimeCommand command);
}