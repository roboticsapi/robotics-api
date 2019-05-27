/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.runtime.core.primitives.BooleanNot;
import org.roboticsapi.runtime.core.primitives.IntConditional;
import org.roboticsapi.runtime.core.primitives.IntEquals;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.IntDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;

public class ErrorNumberSwitchFragment extends NetFragment {

	private DataflowOutPort errorPort;

	public ErrorNumberSwitchFragment(DataflowOutPort errorPort) {
		super("Error number switch");
		this.errorPort = errorPort;

	}

	public DataflowOutPort getCasePort(int nr) throws MappingException {
		if (errorPort == null) {
			throw new MappingException("No input, or default case already used.");
		}
		IntEquals check = add(new IntEquals(0, nr, 0));
		connect(errorPort, check.getInFirst(), new IntDataflow());
		IntConditional afterCheck = add(new IntConditional(0, 0));
		connect(errorPort, afterCheck.getInFalse(), new IntDataflow());
		connect(check.getOutValue(), afterCheck.getInCondition());
		errorPort = addOutPort(new IntDataflow(), false, afterCheck.getOutValue());
		return addOutPort(new StateDataflow(), false, check.getOutValue());
	}

	public DataflowOutPort getDefaultPort() throws MappingException {
		IntEquals noError = add(new IntEquals(0, 0, 0));
		connect(errorPort, noError.getInFirst(), new IntDataflow());
		BooleanNot error = add(new BooleanNot());
		connect(noError.getOutValue(), error.getInValue());
		errorPort = null;
		return addOutPort(new StateDataflow(), false, error.getOutValue());
	}

}
