/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rcc;

import java.util.Map;

import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandResult;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.facet.runtime.rpi.NetHandle;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.mapping.BasicCommandHandle;
import org.roboticsapi.facet.runtime.rpi.mapping.RpiWaitCommand;

public class RccWaitCommand extends RpiWaitCommand {

	public RccWaitCommand(String name, RccRuntime runtime, double duration) {
		super(name, runtime, duration);
	}

	public RccWaitCommand(String name, RccRuntime runtime) {
		super(name, runtime);
	}

	public RccWaitCommand(String name, RccRuntime runtime, RealtimeBoolean waitFor) {
		super(name, runtime, waitFor);
	}

	@Override
	protected BasicCommandHandle createCommandHandle(NetHandle handle, Map<CommandResult, NetcommValue> resultMap)
			throws CommandException {
		return new BasicCommandHandle(this, handle, resultMap);
	}
}