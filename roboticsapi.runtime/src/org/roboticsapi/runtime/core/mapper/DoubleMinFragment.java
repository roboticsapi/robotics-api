/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import java.util.List;

import org.roboticsapi.runtime.core.primitives.DoubleConditional;
import org.roboticsapi.runtime.core.primitives.DoubleGreater;
import org.roboticsapi.runtime.core.primitives.DoubleValue;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.RPIException;

public class DoubleMinFragment extends NetFragment {

	private final DataflowOutPort outMin;

	public DoubleMinFragment(double param, List<DataflowOutPort> values) throws MappingException {
		this(param, values.toArray(new DataflowOutPort[values.size()]));
	}

	public DoubleMinFragment(List<DataflowOutPort> values) throws MappingException {
		this(values.toArray(new DataflowOutPort[values.size()]));
	}

	public DoubleMinFragment(double param, DataflowOutPort... values) throws MappingException {
		super("Double Minimum");

		if (values == null || values.length == 0) {
			outMin = addOutPort(new DoubleDataflow(), true, add(new DoubleValue(param)).getOutValue());
		} else {
			DataflowOutPort out = createTupleMinimumCalculator(this, values[0], null, param);

			if (values.length > 1) {
				for (int i = 1; i < values.length; i++) {
					out = createTupleMinimumCalculator(this, out, values[i], null);
				}
			}

			outMin = out;
		}
	}

	public DoubleMinFragment(DataflowOutPort... values) throws MappingException {
		super("Double Minimum");

		if (values == null || values.length == 0) {
			throw new IllegalArgumentException("At least one port must be given");
		} else if (values.length == 1) {
			outMin = values[0];
		} else {

			DataflowOutPort out = createTupleMinimumCalculator(this, values[0], values[1], null);

			for (int i = 2; i < values.length; i++) {
				out = createTupleMinimumCalculator(this, out, values[i], null);
			}

			outMin = out;
		}
	}

	private DataflowOutPort createTupleMinimumCalculator(NetFragment fragment, DataflowOutPort value1,
			DataflowOutPort value2, Double param) throws MappingException {
		DoubleGreater g = fragment.add(new DoubleGreater());
		DoubleConditional c = fragment.add(new DoubleConditional());

		fragment.connect(value1, fragment.addInPort(new DoubleDataflow(), true, g.getInFirst()));
		fragment.connect(value1, fragment.addInPort(new DoubleDataflow(), true, c.getInFalse()));

		if (value2 != null) {
			fragment.connect(value2, fragment.addInPort(new DoubleDataflow(), true, g.getInSecond()));
			fragment.connect(value2, fragment.addInPort(new DoubleDataflow(), true, c.getInTrue()));
		} else {
			g.setSecond(param);
			c.setTrue(param);
		}

		try {
			c.getInCondition().connectTo(g.getOutValue());
		} catch (RPIException e) {
			throw new MappingException(e);
		}

		return fragment.addOutPort(new DoubleDataflow(), true, c.getOutValue());

	}

	public DataflowOutPort getOutMin() {
		return outMin;
	}

}
