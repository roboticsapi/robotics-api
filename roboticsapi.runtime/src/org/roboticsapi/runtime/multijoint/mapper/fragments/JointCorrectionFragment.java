/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper.fragments;

import org.roboticsapi.runtime.core.primitives.Clock;
import org.roboticsapi.runtime.core.primitives.DoubleAdd;
import org.roboticsapi.runtime.core.primitives.DoubleConditional;
import org.roboticsapi.runtime.core.primitives.DoubleGreater;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.core.primitives.DoubleSnapshot;
import org.roboticsapi.runtime.core.primitives.Interval;
import org.roboticsapi.runtime.core.primitives.Rampify;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.EventDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.RisingEdgeDetectionFragment;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class JointCorrectionFragment extends NetFragment {

	private final DataflowOutPort result;
	private final double[] start;

	public JointCorrectionFragment(double[] startpos, DoubleSensorMapperResult[] realPos, double resyncTime,
			double starttime, DataflowOutPort time, NetFragment innerFragment, DataflowOutPort innerResult)
			throws MappingException, RPIException {
		super("Joint position correction");
		this.start = startpos;

		OutPort[] ports = new OutPort[startpos.length];
		InPort[] inports1 = new InPort[startpos.length];
		InPort[] inports2 = new InPort[startpos.length];

		if (time == null) {
			Clock clock = add(new Clock());
			time = addOutPort(new DoubleDataflow(), true, clock.getOutValue());
		}

		// snapshot time
		DoubleGreater t = add(new DoubleGreater(0.0, starttime));
		connect(time, addInPort(new DoubleDataflow(), true, t.getInFirst()));
		RisingEdgeDetectionFragment snapshot = add(
				new RisingEdgeDetectionFragment(addOutPort(new StateDataflow(), true, t.getOutValue())));

		// resynchronize
		Interval interval = add(new Interval(starttime, starttime + resyncTime));
		connect(time, addInPort(new DoubleDataflow(), true, interval.getInValue()));

		Rampify ramp = add(new Rampify(0.0));
		connect(interval.getOutValue(), ramp.getInValue());

		DoubleAdd intervalMinusOne = add(new DoubleAdd(0.0, -1.0));
		connect(ramp.getOutValue(), intervalMinusOne.getInFirst());
		DoubleMultiply intervalInverted = add(new DoubleMultiply(0.0, -1.0));
		connect(intervalMinusOne.getOutValue(), intervalInverted.getInFirst());

		if (innerResult.getPorts().size() != startpos.length || !(innerFragment instanceof NetFragment)) {
			throw new MappingException("Inconsistent number of joints.");
		}
		add(innerFragment);

		// calculate position for each axis
		for (int i = 0; i < startpos.length; i++) {
			DoubleSnapshot ssp = add(new DoubleSnapshot());
			connect(snapshot.getEventPort(), addInPort(new EventDataflow(), true, ssp.getInSnapshot()));
			connect(realPos[i].getSensorPort(), addInPort(new DoubleDataflow(), true, ssp.getInValue()));

			DoubleAdd diff = add(new DoubleAdd(0.0, -start[i]));
			connect(ssp.getOutValue(), diff.getInFirst());

			DoubleMultiply delta = add(new DoubleMultiply());
			connect(diff.getOutValue(), delta.getInFirst());
			connect(intervalInverted.getOutValue(), delta.getInSecond());

			DoubleAdd add = add(new DoubleAdd());
			connect(delta.getOutValue(), add.getInFirst());

			DoubleConditional value = add(new DoubleConditional());
			connect(t.getOutValue(), value.getInCondition());
			connect(add.getOutValue(), value.getInTrue());

			ports[i] = value.getOutValue();
			inports1[i] = add.getInSecond();
			inports2[i] = value.getInFalse();
		}
		connect(innerResult, addInPort(innerResult.getType(), true, inports1));
		connect(innerResult, addInPort(innerResult.getType(), true, inports2));
		result = addOutPort(innerResult.getType(), false, ports);
	}

	public DataflowOutPort getResultPort() {
		return result;
	}

}
