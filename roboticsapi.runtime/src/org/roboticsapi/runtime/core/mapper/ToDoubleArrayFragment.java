/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import java.util.List;

import org.roboticsapi.runtime.core.primitives.DoubleArray;
import org.roboticsapi.runtime.core.primitives.DoubleArraySet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ComposedDataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.DoubleArrayDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.OutPort;

public class ToDoubleArrayFragment extends ToArrayFragment {

	public ToDoubleArrayFragment(ComposedDataflowOutPort port) throws MappingException {
		super(port);
	}

	public ToDoubleArrayFragment(List<DataflowOutPort> ports) throws MappingException {
		super(ports);
	}

	public ToDoubleArrayFragment(DataflowOutPort... sourcePorts) throws MappingException {
		super(sourcePorts);
	}

	@Override
	protected DataflowType getArrayDataflowType() {
		return new DoubleArrayDataflow(getArraySize());
	}

	@Override
	protected OutPort createAndAddArraySetPrimitive(NetFragment parent, int index, int arraylength, OutPort valuePort,
			OutPort arrayPort) throws MappingException {
		DoubleArraySet set = parent.add(new DoubleArraySet());

		set.setSize(arraylength);
		set.setIndex(index);

		parent.connect(valuePort, set.getInValue());
		parent.connect(arrayPort, set.getInArray());

		return set.getOutArray();
	}

	@Override
	protected OutPort createAndAddArrayPrimitive(NetFragment parent, int length) {
		DoubleArray array = parent.add(new DoubleArray(length));

		return array.getOutArray();
	}

}
