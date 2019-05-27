/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import java.util.List;
import java.util.Vector;

import org.roboticsapi.runtime.rpi.OutPort;

public class ComposedDataflowOutPort extends DataflowOutPort {
	private final List<DataflowOutPort> ports = new Vector<DataflowOutPort>();

	public ComposedDataflowOutPort(boolean required, DataflowOutPort... ports) {
		this(required, new ComposedDataflow(), ports);
	}

	public void addDataflow(DataflowOutPort port) {
		this.ports.add(port);
		((ComposedDataflow) getType()).addDataflow(port.getType());
	}

	private ComposedDataflowOutPort(boolean required, ComposedDataflow type, DataflowOutPort... ports) {
		super(required, type);
		for (DataflowOutPort port : ports) {
			this.ports.add(port);
			type.addDataflow(port.getType());
		}
	}

	@Override
	public List<OutPort> getPorts() {
		List<OutPort> ret = new Vector<OutPort>();
		for (DataflowOutPort port : ports) {
			ret.addAll(port.getPorts());
		}
		return ret;
	}

	public List<DataflowOutPort> getDataflowPorts() {
		return ports;
	}
}
