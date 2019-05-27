/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.runtime.core.primitives.DoubleConditional;
import org.roboticsapi.runtime.core.primitives.DoubleGreater;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.RPIException;

public class DoubleAbsoluteFragment extends NetFragment {

	private final DataflowOutPort outAbsoluteValue;

	public DoubleAbsoluteFragment(DataflowOutPort value) throws MappingException {
		super("Double Absolute");

		DoubleMultiply inverse = add(new DoubleMultiply());
		inverse.setFirst(-1d);
		connect(value, addInPort(new DoubleDataflow(), true, inverse.getInSecond()));

		DoubleGreater greaterZero = add(new DoubleGreater());
		greaterZero.setSecond(0d);
		connect(value, addInPort(new DoubleDataflow(), true, greaterZero.getInFirst()));

		DoubleConditional cond = add(new DoubleConditional());
		try {
			cond.getInCondition().connectTo(greaterZero.getOutValue());
			connect(value, addInPort(new DoubleDataflow(), true, cond.getInTrue()));
			cond.getInFalse().connectTo(inverse.getOutValue());
		} catch (RPIException e) {
			throw new MappingException(e);
		}

		outAbsoluteValue = addOutPort(new DoubleDataflow(), true, cond.getOutValue());

	}

	public DataflowOutPort getOutAbsoluteValue() {
		return outAbsoluteValue;
	}

}
