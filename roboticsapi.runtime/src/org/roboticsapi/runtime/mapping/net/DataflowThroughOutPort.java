/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import java.util.List;

import org.roboticsapi.runtime.rpi.OutPort;

/**
 * A data flow port that does not belong to RPI ports, but forwards data
 * arriving at another in port
 */
public class DataflowThroughOutPort extends DataflowOutPort {

	/** corresponding in port */
	private final DataflowThroughInPort inPort;

	/**
	 * Creates a new data flow through port
	 * 
	 * @param source in port data is forwarded from
	 */
	public DataflowThroughOutPort(final boolean required, final DataflowThroughInPort source) {
		this(required, source, source.getType());
	}

	/**
	 * Creates a new data flow through port (used to change data flow type of data)
	 * 
	 * @param source in port data is forwarded from
	 * @param type   type of the port
	 */
	public DataflowThroughOutPort(final boolean required, final DataflowThroughInPort source, final DataflowType type) {
		super(required, type);
		inPort = source;
	}

	@Override
	public String toString() {
		return (getInPort().getSource() != null ? "[C]" : "[U]") + " Out<" + getType() + "> (" + getParentFragment()
				+ ")";
	}

	/**
	 * Retrieves the in port data is forwarded from
	 * 
	 * @return in port data is forwarded from
	 */
	public DataflowThroughInPort getInPort() {
		return inPort;
	}

	@Override
	public List<OutPort> getPorts() {
		if (!inPort.isConnected()) {
			return null;
		}
		return inPort.getSourcePorts();
	}

	@Override
	public List<OutPort> getPorts(DataflowType type) {
		return type.formatPorts(getPorts(), getType());
	}
}
