/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import java.util.List;

import org.roboticsapi.runtime.core.netcomm.ReadDoubleFromNet;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

/**
 * A data flow type transmitting one double value
 */
public class DoubleDataflow extends DataflowType {

	public DoubleDataflow() {
		super(1);
	}

	@Override
	public ValueReader[] createValueReaders(String uniqueKeyPrefix, List<OutPort> value, NetFragment fragment)
			throws RPIException {
		ReadDoubleFromNet out = fragment.add(new ReadDoubleFromNet(uniqueKeyPrefix + "_DoubleDataflow"));

		out.getInValue().connectTo(value.get(0));

		return new ValueReader[] { new ValueReader(out) {

			@Override
			public Object interpretValue(String value) {
				return Double.parseDouble(value);
			}
		} };
	}

}
