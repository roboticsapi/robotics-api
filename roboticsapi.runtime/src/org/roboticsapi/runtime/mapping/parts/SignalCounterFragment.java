/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.runtime.core.primitives.DoubleAdd;
import org.roboticsapi.runtime.core.primitives.DoubleConditional;
import org.roboticsapi.runtime.core.primitives.DoublePre;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.EventDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.RPIException;

public class SignalCounterFragment extends NetFragment {

	private final DataflowOutPort signalCountPort;

	public SignalCounterFragment(DataflowOutPort signal) throws MappingException {
		super("Signal counter");

		if (signal == null) {
			throw new MappingException("Given OutPorts may not be null");
		}

		DoubleConditional cond = new DoubleConditional(1d, 0d);
		add(cond);

		connect(signal, addInPort(new EventDataflow(), true, cond.getInCondition()));

		DoubleAdd add = new DoubleAdd();
		add(add);

		DoublePre pre = add(new DoublePre());

		try {
			add.getInFirst().connectTo(cond.getOutValue());
			add.getInSecond().connectTo(pre.getOutValue());
		} catch (RPIException e) {
			throw new MappingException(e);
		}

		DataflowOutPort outValue = addOutPort(new DoubleDataflow(), true, add.getOutValue());

		connect(outValue, addInPort(new DoubleDataflow(), true, pre.getInValue()));

		signalCountPort = outValue;
	}

	public DataflowOutPort getSignalCountPort() {
		return signalCountPort;
	}

}
