/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

/**
 * A data flow composed of various other data flows
 */
public class ComposedDataflow extends DataflowType {

	private final List<DataflowType> children = new ArrayList<DataflowType>();

	public ComposedDataflow() {
		super(0);
	}

	public void addDataflow(DataflowType dataflow) {
		children.add(dataflow);
	}

	public List<DataflowType> getChildren() {
		return children;
	}

	@Override
	public boolean providesValueFor(DataflowType other) {
		for (DataflowType child : children) {
			if (child.providesValueFor(other)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean acceptsValueFrom(DataflowType other) {
		for (DataflowType child : children) {
			if (other.providesValueFor(child)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getPortCount() {
		int ret = 0;
		for (DataflowType child : children) {
			ret += child.getPortCount();
		}
		return ret;
	}

	@Override
	protected <T> List<T> formatPorts(List<T> ports, DataflowType type) {
		// concatenate the ports for all our children
		List<T> ret = new ArrayList<T>();
		for (DataflowType child : children) {

			List<T> childports = type.getPorts(ports, child);
			if (childports != null) {
				ret.addAll(childports);
			} else {
				for (int i = 0; i < child.getPortCount(); i++) {
					ret.add(null);
				}
			}
		}
		return ret;
	}

	@Override
	protected <T> List<T> getPorts(List<T> ports, DataflowType requestedPorts) {
		if (ports.size() != getPortCount()) {
			return null;
		}

		// is our dataflow type requested?
		if (this == requestedPorts) {
			return ports;
		}

		// check if any of the children can provide the requested ports
		List<T> ret = new ArrayList<T>();
		for (int i = 0; i < requestedPorts.getPortCount(); i++) {
			ret.add(null);
		}

		int from = 0;
		for (DataflowType child : children) {
			List<T> childPorts = ports.subList(from, from + child.getPortCount());
			List<T> foundPorts = requestedPorts.formatPorts(childPorts, child);
			if (foundPorts != null) {
				for (int i = 0; i < requestedPorts.getPortCount(); i++) {
					if (foundPorts.get(i) != null) {
						if (ret.get(i) != null) {
							throw new RuntimeException("Ambiguous ports in ComposedDataflow: " + ret.get(i).toString());
						}
						ret.set(i, foundPorts.get(i));
					}
				}
			}
			from += child.getPortCount();
		}
		return ret;
	}

	@Override
	public ValueReader[] createValueReaders(String uniqueKeyPrefix, List<OutPort> value, NetFragment fragment)
			throws RPIException, MappingException {
		List<ValueReader> readers = new ArrayList<DataflowType.ValueReader>();

		int index = 0;
		for (DataflowType c : getChildren()) {
			OutPort[] childPorts = new OutPort[c.getPortCount()];

			for (int i = 0; i < c.getPortCount(); i++) {
				childPorts[i] = value.get(index);

				index++;
			}

			ValueReader[] createdValueReaders = c.createValueReaders(uniqueKeyPrefix + index, Arrays.asList(childPorts),
					fragment);

			for (ValueReader r : createdValueReaders) {
				readers.add(r);
			}
		}

		return readers.toArray(new ValueReader[readers.size()]);
	}
}
