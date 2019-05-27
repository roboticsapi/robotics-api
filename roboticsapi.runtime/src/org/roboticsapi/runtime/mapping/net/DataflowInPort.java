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

import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;

/**
 * A (high-level) data flow input port representing one or more RPI ports
 */
public class DataflowInPort extends DataflowPort {

	/** RPI ports represented by this port */
	protected List<InPort> ports = new ArrayList<InPort>();

	/** Out port this port is connected to */
	private DataflowOutPort source;

	/**
	 * Retrieves the RPI ports represented by this port
	 * 
	 * @return read-only list of RPI ports
	 */
	public List<InPort> getPorts() {
		if (ports == null) {
			return null;
		} else {
			return Collections.unmodifiableList(ports);
		}
	}

	/**
	 * Retrieves the RPI ports for this port in a given dataflow type layout
	 * 
	 * @param type dataflow type to provide ports for
	 * @return list of rpi ports as required for the given type
	 */
	public List<InPort> getPorts(DataflowType type) {
		return type.formatPorts(getPorts(), getType());
	}

	/**
	 * Connects the port to a data flow out port
	 * 
	 * @param source out port to connect to
	 * @throws MappingException
	 */
	protected void connectTo(final DataflowOutPort source) throws MappingException {
		if (this.source != null) {
			throw new MappingException("Dataflow is already connected");
		}
		if (source == null) {
			throw new MappingException("Port to connect to may not be null");
		}
		if (!source.getType().providesValueFor(getType())) {
			throw new MappingException("Connected ports are incompatible [" + source.getType() + "," + getType() + "]");
		}
		this.source = source;
	}

	public List<OutPort> getSourcePorts() {
		return source.getPorts(getType());
	}

	public boolean isConnected() {
		return source != null;
	}

	/**
	 * Connects the represented RPI ports
	 * 
	 * @throws MappingException
	 */
	protected void connectInNet() throws MappingException {
		// The data type knows how to connect the ports
		if (isRequired() && !isConnected()) {
			throw new MappingException("Required port is not connected: " + this.getType());
		}
		if (isConnected()) {
			source.getType().connectInNet(source, this);
		}
	}

	/**
	 * Retrieves the connected data flow out port
	 * 
	 * @return out ports connected to
	 */
	public DataflowOutPort getSource() {
		return source;
	}

	/**
	 * Creates a new data flow in port
	 * 
	 * @param type  type of data type
	 * @param ports RPI ports represented by this data flow
	 */
	public DataflowInPort(final DataflowType type, final InPort... ports) {
		this(true, type, ports);
	}

	/**
	 * Creates a new data flow in port
	 * 
	 * @param required data flow is required for operation of the primitive
	 * @param type     type of data type
	 * @param ports    RPI ports represented by this data flow
	 */
	public DataflowInPort(final boolean required, final DataflowType type, final InPort... ports) {
		setRequired(required);
		setType(type);
		for (final InPort in : ports) {
			this.ports.add(in);
		}
	}

	@Override
	public String toString() {
		return (getSource() != null ? "[C]" : "[U]") + " In<" + getType() + "> (" + getParentFragment() + ")";
	}
}
