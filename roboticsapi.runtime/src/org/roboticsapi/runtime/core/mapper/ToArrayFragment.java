/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import java.util.List;

import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.OutPort;

public abstract class ToArrayFragment extends NetFragment {

	private final DataflowOutPort arrayOutPort;
	private final int arraySize;

	public ToArrayFragment(List<DataflowOutPort> ports) throws MappingException {
		this(ports.toArray(new DataflowOutPort[ports.size()]));
	}

	public ToArrayFragment(DataflowOutPort... sourcePorts) throws MappingException {
		super("ToArrayFragment");

		if (sourcePorts == null || sourcePorts.length == 0) {
			throw new MappingException("At least one source port must be given");
		}

		int totalPorts = 0;
		for (DataflowOutPort port : sourcePorts) {
			totalPorts += port.getPorts().size();
		}

		OutPort arrayOut = createAndAddArrayPrimitive(this, totalPorts);
		DataflowType type = null;
		int i = 0;
		for (DataflowOutPort port : sourcePorts) {
			if (type == null) {
				type = port.getType();
			} else {
				if (!port.getType().providesValueFor(type)) {
					if (type.providesValueFor(port.getType())) {
						type = port.getType();
					} else {
						throw new MappingException(
								"Port dataflow type " + port.getType() + " does not provide values for " + type);
					}
				}
			}

			for (OutPort outport : port.getPorts()) {

				arrayOut = createAndAddArraySetPrimitive(this, i++, totalPorts, outport, arrayOut);
			}

		}

		arraySize = totalPorts;
		arrayOutPort = addOutPort(getArrayDataflowType(), true, arrayOut);
	}

	public DataflowOutPort getArrayOutPort() {
		return arrayOutPort;
	}

	public int getArraySize() {
		return arraySize;
	}

	protected abstract DataflowType getArrayDataflowType();

	protected abstract OutPort createAndAddArraySetPrimitive(NetFragment parent, int index, int arraylength,
			OutPort valuePort, OutPort arrayPort) throws MappingException;

	protected abstract OutPort createAndAddArrayPrimitive(NetFragment parent, int length);

}
