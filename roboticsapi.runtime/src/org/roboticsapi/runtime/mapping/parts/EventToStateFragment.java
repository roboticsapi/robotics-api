/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.runtime.core.primitives.Trigger;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.EventDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;

public class EventToStateFragment extends NetFragment {

	private DataflowOutPort statePort = null;

	public EventToStateFragment(DataflowOutPort active, DataflowOutPort enteredEventPort) throws MappingException {
		super("Event->State");

		if (active == null || enteredEventPort == null) {
			throw new MappingException("Given OutPorts may not be null");
		}

		Trigger entering = new Trigger();
		add(entering);

		connect(enteredEventPort, addInPort(new EventDataflow(), true, entering.getInOn()));

		connect(active, addInPort(new StateDataflow(), true, entering.getInActive()));

		statePort = addOutPort(new StateDataflow(), true, entering.getOutActive());
	}

	public DataflowOutPort getStatePort() {
		return statePort;
	}

}
