/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result;

import org.roboticsapi.runtime.mapping.net.DataflowOutPort;

public class ActionResult {
	private final DataflowOutPort outPort;

	public ActionResult(DataflowOutPort outPort) {
		this.outPort = outPort;
	}

	public DataflowOutPort getOutPort() {
		return outPort;
	}
}
