/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.rpi.InPort;

public class ComposedDataflowInPort extends DataflowInPort {
	private final List<DataflowInPort> ports = new ArrayList<DataflowInPort>();

	public void addInPort(DataflowInPort port) {
		ports.add(port);
		((ComposedDataflow) getType()).addDataflow(port.getType());
	}

	public ComposedDataflowInPort(boolean required, DataflowInPort... ports) {
		this(required, new ComposedDataflow(), ports);
	}

	private ComposedDataflowInPort(boolean required, ComposedDataflow type, DataflowInPort... ports) {
		super(required, type);
		for (DataflowInPort in : ports) {
			this.ports.add(in);
			type.addDataflow(in.getType());
		}
	}

	@Override
	public void connectTo(DataflowOutPort source) throws MappingException {
		super.connectTo(source);
		for (DataflowInPort port : ports) {
			if (source.getType().providesValueFor(port.getType())) {
				port.connectTo(source);
			}
		}
	}

	@Override
	public void connectInNet() throws MappingException {
		for (DataflowInPort port : ports) {
			port.connectInNet();
		}
	}

	@Override
	public List<InPort> getPorts() {
		List<InPort> ret = new ArrayList<InPort>();
		for (DataflowInPort port : ports) {
			ret.addAll(port.getPorts());
		}
		return ret;
	}
}
