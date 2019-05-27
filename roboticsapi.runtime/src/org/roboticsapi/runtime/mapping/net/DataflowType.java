/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import java.util.List;
import java.util.Vector;

import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

/**
 * A high-level data flow type consisting of one or multiple RPI ports
 */
public abstract class DataflowType {

	private int portCount = 0;

	public DataflowType(int portCount) {
		this.portCount = portCount;
	}

	/**
	 * Checks whether this data flow type is compatible to another data flow type
	 * (i.e. can provide a value understood by another data type)
	 *
	 * @param other other data flow type
	 * @return true if the data flow types are compatible
	 */
	public boolean providesValueFor(final DataflowType other) {
		return other.acceptsValueFrom(this);
	}

	/**
	 * Checks whether a port with this type accepts data from a port with the given
	 * other type
	 *
	 * @param other other data flow type
	 * @return true if the port accepts values of the given type
	 */
	protected boolean acceptsValueFrom(final DataflowType other) {
		// make sure the other data flow is of the same type (class)
		if (this.getClass().isAssignableFrom(other.getClass())) {
			return true;
		}

		return false;
	}

	/**
	 * Connects the RPI ports of two data flow ports of this type
	 *
	 * @param from data flow source
	 * @param to   data flow destination
	 * @throws MappingException
	 */
	public void connectInNet(final DataflowOutPort from, final DataflowInPort to) throws MappingException {
		// connect all corresponding ports
		final List<OutPort> outs = from.getPorts(to.getType());
		final List<InPort> ins = to.getPorts(to.getType());
		if (outs == null && to.isRequired()) {
			throw new MappingException("Required port " + to + " is not connected.");
		}
		if (outs == null) {
			return;
		}
		for (int i = 0; i < outs.size(); i++) {
			if (outs.get(i) != null && ins.get(i) != null) {
				try {
					ins.get(i).connectTo(outs.get(i));
				} catch (RPIException e) {
					throw new MappingException(e.getMessage());
				}
			}
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	/**
	 * Retrieves the number of RPI ports for this dataflow port
	 *
	 * @return
	 */
	public int getPortCount() {
		return portCount;
	}

	/**
	 * Formats the list of ports of a given dataflow type layout into the layout of
	 * this dataflow type
	 *
	 * @param <T>   type of port (InPort / OutPort)
	 * @param ports ports in "type" layout
	 * @param type  layout for the ports
	 * @return ports in "this" layout
	 */
	protected <T> List<T> formatPorts(List<T> ports, DataflowType type) {
		if (ports == null || ports.size() != type.getPortCount()) {
			return null;
		}
		List<T> ret = type.getPorts(ports, this);
		if (ret == null) {
			ret = new Vector<T>();
			for (int i = 0; i < getPortCount(); i++) {
				ret.add(null);
			}
		}
		return ret;
	}

	/**
	 * Retrieves the (subset of) ports required for a target dataflow type
	 *
	 * @param <T>            type of port (InPort / OutPort)
	 * @param ports          list of ports in "this" port layout
	 * @param requestedPorts port layout to return the ports in
	 * @return ports in "requestedPorts" layout
	 */
	protected <T> List<T> getPorts(List<T> ports, DataflowType requestedPorts) {
		if (this.providesValueFor(requestedPorts)) {
			return ports;
		}
		return null;
	}

	/**
	 * Augments a NetcommPrimitive by the ability to correctly parse received String
	 * values.
	 *
	 */
	public abstract class ValueReader {
		private final NetcommPrimitive primitive;

		public ValueReader(NetcommPrimitive primitive) {
			this.primitive = primitive;

		}

		public NetcommPrimitive getPrimitive() {
			return primitive;
		}

		/**
		 * Parses a value received by the associated NetcommPrimitive and converts it to
		 * a Java type.
		 *
		 * @param value the value to parse
		 * @return Java representation of the value
		 */
		public abstract Object interpretValue(String value);
	}

	/**
	 * Creates {@link ValueReaders} that can be used to read data from the given
	 * list of OutPorts to a Robotics API context.
	 *
	 * @param uniqueKeyPrefix an unique identifier that can be used as key for
	 *                        netcomm primitives
	 * @param valuePorts      list of OutPorts providing the values to read, is
	 *                        expected to match the DataflowType's semantics both in
	 *                        count and provided data
	 * @param fragment        to which all required primitives should be added
	 * @return list of ValueReaders that read data from all provided ports
	 * @throws RPIException     if ValueReader creation fails on net level
	 * @throws MappingException if ValueReader creation fails on dataflow level
	 */
	public abstract ValueReader[] createValueReaders(String uniqueKeyPrefix, List<OutPort> valuePorts,
			NetFragment fragment) throws RPIException, MappingException;
}
