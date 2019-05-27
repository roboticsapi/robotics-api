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
import org.roboticsapi.runtime.world.netcomm.ReadRotationFromNet;
import org.roboticsapi.runtime.world.types.RPIRotation;
import org.roboticsapi.world.Rotation;

public class RotationDataflow extends DataflowType {

	public RotationDataflow() {
		super(1);
	}

	@Override
	public ValueReader[] createValueReaders(String uniqueKeyPrefix, List<OutPort> valuePorts, NetFragment fragment)
			throws RPIException, MappingException {
		ReadRotationFromNet reader = fragment.add(new ReadRotationFromNet(uniqueKeyPrefix + "_RotationDataflow"));

		fragment.connect(valuePorts.get(0), reader.getInValue());

		return new ValueReader[] { new ValueReader(reader) {

			@Override
			public Object interpretValue(String value) {
				RPIRotation rotation = new RPIRotation(value);
				return new Rotation(rotation.getA().get(), rotation.getB().get(), rotation.getC().get());
			}
		} };
	}

}