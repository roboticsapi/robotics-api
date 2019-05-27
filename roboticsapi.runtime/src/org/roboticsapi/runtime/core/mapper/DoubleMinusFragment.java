/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.runtime.core.primitives.DoubleAdd;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;

public class DoubleMinusFragment extends NetFragment {

	private final DataflowOutPort resultPort;

	public DoubleMinusFragment(DataflowOutPort minuend, DataflowOutPort subtrahend) throws MappingException {
		super("Minus");
		DoubleMultiply multiply = add(new DoubleMultiply());

		multiply.setSecond(-1d);

		connect(subtrahend, multiply.getInFirst(), new DoubleDataflow());

		DoubleAdd add = add(new DoubleAdd());

		connect(minuend, add.getInFirst(), new DoubleDataflow());
		connect(multiply.getOutValue(), add.getInSecond());

		resultPort = addOutPort(new DoubleDataflow(), true, add.getOutValue());
	}

	public DataflowOutPort getResultPort() {
		return resultPort;
	}

}
