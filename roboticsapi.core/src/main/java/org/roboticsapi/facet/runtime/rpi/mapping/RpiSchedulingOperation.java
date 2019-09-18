/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import java.util.List;

import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.CommandHandleOperation;
import org.roboticsapi.facet.runtime.rpi.NetHandle;

public class RpiSchedulingOperation extends CommandHandleOperation {

	private List<NetHandle> startHandles;
	private List<NetHandle> cancelHandles;
	private List<NetHandle> abortHandles;

	public RpiSchedulingOperation(CommandHandle handle, List<NetHandle> startHandles, List<NetHandle> cancelHandles,
			List<NetHandle> abortHandles) {
		super(handle);
		this.startHandles = startHandles;
		this.cancelHandles = cancelHandles;
		this.abortHandles = abortHandles;
	}

	public List<NetHandle> getStartHandles() {
		return startHandles;
	}

	public List<NetHandle> getCancelHandles() {
		return cancelHandles;
	}

	public List<NetHandle> getAbortHandles() {
		return abortHandles;
	}

}
