/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import java.util.List;

import org.roboticsapi.runtime.core.netcomm.ReadIntFromNet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

/**
 * A data flow type transmitting one Integer value
 */
public class IntDataflow extends DataflowType {

	public IntDataflow() {
		super(1);
	}

	@Override
	public ValueReader[] createValueReaders(String uniqueKeyPrefix, List<OutPort> valuePorts, NetFragment fragment)
			throws RPIException, MappingException {
		ReadIntFromNet reader = fragment.add(new ReadIntFromNet(uniqueKeyPrefix + "_IntDataflow"));

		fragment.connect(valuePorts.get(0), reader.getInValue());

		return new ValueReader[] { new ValueReader(reader) {

			@Override
			public Object interpretValue(String value) {
				return Integer.parseInt(value);
			}
		} };
	}

}
