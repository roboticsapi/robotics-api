/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.runtime.core.primitives.BooleanPre;
import org.roboticsapi.runtime.core.primitives.Trigger;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;

public class DelayedStateActivationFragment extends NetFragment {

	private DataflowOutPort statePort;

	public DelayedStateActivationFragment(DataflowOutPort signalPort) throws MappingException {
		super("DelayedStateActivation");

		if (signalPort == null) {
			throw new MappingException("Given OutPorts may not be null");
		}

		Trigger trig = add(new Trigger());
		BooleanPre pre = add(new BooleanPre());

		connect(signalPort, addInPort(signalPort.getType(), true, pre.getInValue()));

		connect(pre.getOutValue(), trig.getInOn());

		DataflowOutPort statePort = addOutPort(new StateDataflow(), true, trig.getOutActive());
		setStatePort(statePort);
	}

	private void setStatePort(DataflowOutPort eventPort) {
		this.statePort = eventPort;
	}

	public DataflowOutPort getStatePort() {
		return statePort;
	}
}
