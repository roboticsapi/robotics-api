/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;

public class DoubleScaleFragment extends NetFragment {

	private final DataflowOutPort scaledValue;

	public DoubleScaleFragment(DataflowOutPort toScale, DataflowOutPort scalingFactor) throws MappingException {
		super("Double scale");

		DoubleMultiply multiply = add(new DoubleMultiply());

		connect(toScale, addInPort(new DoubleDataflow(), true, multiply.getInFirst()));

		connect(scalingFactor, addInPort(new DoubleDataflow(), true, multiply.getInSecond()));

		scaledValue = addOutPort(new DoubleDataflow(), true, multiply.getOutValue());
	}

	public DoubleScaleFragment(DataflowOutPort toScale, double scalingFactor) throws MappingException {
		super("Double scale");
		DoubleMultiply multiply = add(new DoubleMultiply(0d, scalingFactor));

		connect(toScale, addInPort(new DoubleDataflow(), true, multiply.getInFirst()));

		scaledValue = addOutPort(new DoubleDataflow(), true, multiply.getOutValue());
	}

	public DoubleScaleFragment(double toScale, DataflowOutPort scalingFactor) throws MappingException {
		super("Double scale");
		DoubleMultiply multiply = add(new DoubleMultiply(toScale, 0d));

		connect(scalingFactor, addInPort(new DoubleDataflow(), true, multiply.getInSecond()));

		scaledValue = addOutPort(new DoubleDataflow(), true, multiply.getOutValue());
	}

	public DataflowOutPort getOutScaledValue() {
		return scaledValue;
	}

}
