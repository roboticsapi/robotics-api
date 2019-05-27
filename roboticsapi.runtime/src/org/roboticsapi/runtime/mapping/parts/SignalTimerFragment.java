/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.runtime.core.primitives.Clock;
import org.roboticsapi.runtime.core.primitives.DoubleAdd;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.core.primitives.DoubleSnapshot;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.EventDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class SignalTimerFragment extends NetFragment {

	private final DataflowOutPort signalTimePort;

	public SignalTimerFragment(DataflowOutPort signal, DataflowOutPort active) throws MappingException {
		super("Container<Signal Timer>");

		if (active == null || signal == null) {
			throw new MappingException("Given OutPorts may not be null");
		}

		NetFragment fragment = new NetFragment("Signal Timer");

		DataflowOutPort outResult = null;

		try {

			// Use a clock to count the time the signal was active
			Clock clock = fragment.add(new Clock(1000d));
			fragment.connect(signal, fragment.addInPort(new StateDataflow(), true, clock.getInActive()));
			OutPort clockOut = clock.getOutValue();

			// snapshot the clock start value each time it became active
			DoubleSnapshot startValue = fragment.add(new DoubleSnapshot());
			fragment.connect(active, fragment.addInPort(new StateDataflow(), true, startValue.getInActive()));
			RisingEdgeDetectionFragment risingEdgeDetectionFragment = new RisingEdgeDetectionFragment(signal);
			add(risingEdgeDetectionFragment);
			DataflowOutPort signalEdge = risingEdgeDetectionFragment.getEventPort();
			fragment.connect(signalEdge, fragment.addInPort(new EventDataflow(), true, startValue.getInSnapshot()));
			startValue.getInValue().connectTo(clockOut);
			OutPort startValueOut = startValue.getOutValue();

			// negate the snapshotted start value...
			DoubleMultiply negateStartValue = fragment.add(new DoubleMultiply());
			negateStartValue.setFirst(-1d);
			negateStartValue.getInSecond().connectTo(startValueOut);
			OutPort negatedStartValueOut = negateStartValue.getOutValue();

			// ...to subtract it from the clock start value
			DoubleAdd add = fragment.add(new DoubleAdd());
			add.getInFirst().connectTo(clockOut);
			add.getInSecond().connectTo(negatedStartValueOut);
			OutPort currentActiveTime = add.getOutValue();

			// this difference is our result
			outResult = fragment.addOutPort(new DoubleDataflow(), true, currentActiveTime);

		} catch (RPIException e) {
			throw new MappingException(e);
		}

		add(fragment);

		signalTimePort = outResult;

	}

	public DataflowOutPort getSignalTimePort() {
		return signalTimePort;
	}

}
