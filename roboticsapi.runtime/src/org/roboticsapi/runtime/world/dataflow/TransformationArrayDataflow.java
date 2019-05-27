/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.dataflow;

import java.util.List;

import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.UnimplementedMappingException;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

/**
 * A data flow type transmitting a transformation array
 */
public class TransformationArrayDataflow extends DataflowType {

	private final int size;

	public TransformationArrayDataflow(int size) {
		super(1);
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	@Override
	public boolean providesValueFor(DataflowType other) {
		if (!super.providesValueFor(other)) {
			return false;
		}
		if (other instanceof TransformationArrayDataflow) {
			final TransformationArrayDataflow o = (TransformationArrayDataflow) other;
			if (o.getSize() != getSize()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ValueReader[] createValueReaders(String uniqueKeyPrefix, List<OutPort> valuePorts, NetFragment fragment)
			throws RPIException, MappingException {
		// TODO implement createValueReaders
		throw new UnimplementedMappingException();
	}
}
