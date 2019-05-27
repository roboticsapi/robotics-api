/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.runtime.mapping.net.DataflowOutPort;

public class CommandMappingPorts {
	public DataflowOutPort start;
	public DataflowOutPort stop;
	public DataflowOutPort cancel;
	public DataflowOutPort override;

	public CommandMappingPorts(DataflowOutPort start, DataflowOutPort stop, DataflowOutPort cancel,
			DataflowOutPort override) {
		this.start = start;
		this.stop = stop;
		this.cancel = cancel;
		this.override = override;
	}
}