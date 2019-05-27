/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.roboticsapi.runtime.rpi.OutPort;

/**
 * A (high-level) data flow output port representing one or more RPI ports
 */
public class DataflowOutPort extends DataflowPort {
	/** RPI ports represented by this port */
	protected List<OutPort> ports = new ArrayList<OutPort>();

	/**
	 * Retrieves the RPI ports represented by this port
	 * 
	 * @return read-only list of RPI ports
	 */
	public List<OutPort> getPorts() {
		return Collections.unmodifiableList(ports);
	}

	/**
	 * Retrieves the RPI ports for this port in a given dataflow type layout
	 * 
	 * @param type dataflow type to provide ports for
	 * @return list of rpi ports as required for the given type
	 */
	public List<OutPort> getPorts(DataflowType type) {

		// TODO: Is this sufficient in all cases?
		if (getType() == type) {
			return getPorts();
		}

		return type.formatPorts(getPorts(), getType());
	}

	/**
	 * Creates a new data flow out port
	 * 
	 * @param type  type of the data flow
	 * @param ports RPI ports represented by this data flow
	 */
	public DataflowOutPort(final DataflowType type, final OutPort... ports) {
		this(false, type, ports);
	}

	/**
	 * Creates a new data flow out port
	 * 
	 * @param required data flow is required for operation of the primitive
	 * @param type     type of the data flow
	 * @param ports    RPI ports represented by this data flow
	 */
	public DataflowOutPort(final boolean required, final DataflowType type, final OutPort... ports) {
		setType(type);
		setRequired(required);
		for (final OutPort out : ports) {
			this.ports.add(out);
		}
	}

	@Override
	public String toString() {
		return "[C] Out<" + getType() + "> (" + getParentFragment() + ")";
	}

	public static DataflowOutPort[] toArray(List<DataflowOutPort> ports) {
		return ports.toArray(new DataflowOutPort[ports.size()]);
	}

}
