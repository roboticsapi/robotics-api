/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.dataflow;

import java.util.List;

import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.netcomm.ReadVectorFromNet;
import org.roboticsapi.runtime.world.types.RPIVector;
import org.roboticsapi.world.Vector;

public class VectorDataflow extends DataflowType {

	public VectorDataflow() {
		super(1);
	}

	@Override
	public ValueReader[] createValueReaders(String uniqueKeyPrefix, List<OutPort> valuePorts, NetFragment fragment)
			throws RPIException, MappingException {
		ReadVectorFromNet reader = fragment.add(new ReadVectorFromNet(uniqueKeyPrefix + "_VectorDataflow"));

		fragment.connect(valuePorts.get(0), reader.getInValue());

		return new ValueReader[] { new ValueReader(reader) {

			@Override
			public Object interpretValue(String value) {
				RPIVector vector = new RPIVector(value);
				return new Vector(vector.getX().get(), vector.getY().get(), vector.getZ().get());
			}
		} };
	}

}