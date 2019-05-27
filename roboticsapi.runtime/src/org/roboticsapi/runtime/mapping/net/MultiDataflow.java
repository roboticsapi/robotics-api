/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import java.util.List;

import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.UnimplementedMappingException;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

@Deprecated
public class MultiDataflow extends DataflowType {

	public MultiDataflow(int portCount) {
		super(portCount);
	}

	@Override
	public boolean providesValueFor(DataflowType other) {
		if (!super.providesValueFor(other)) {
			return false;
		}
		if (getPortCount() != other.getPortCount()) {
			return false;
		}
		return true;
	}

	@Override
	public ValueReader[] createValueReaders(String uniqueKeyPrefix, List<OutPort> valuePorts, NetFragment fragment)
			throws RPIException, MappingException {
		throw new UnimplementedMappingException();
	}
}
