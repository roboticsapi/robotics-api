/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.runtime.core.primitives.IntConditional;
import org.roboticsapi.runtime.core.primitives.IntValue;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.IntDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.rpi.OutPort;

public class ErrorNumberCombineFragment extends NetFragment {

	int nextError = 0;
	OutPort resultPort = null;

	public ErrorNumberCombineFragment() {
		super("Error number combiner");
	}

	public int addCasePort(DataflowOutPort errorState) throws MappingException {
		IntConditional cond = add(new IntConditional(++nextError, 0));
		if (resultPort != null) {
			connect(resultPort, cond.getInFalse());
		}
		connect(errorState, cond.getInCondition(), new StateDataflow());
		resultPort = cond.getOutValue();
		return nextError;
	}

	public DataflowOutPort getNumberPort() throws MappingException {
		if (resultPort == null) {
			resultPort = add(new IntValue(0)).getOutValue();
		}
		return addOutPort(new IntDataflow(), false, resultPort);
	}

}
