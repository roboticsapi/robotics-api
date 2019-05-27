/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper.fragments;

import org.roboticsapi.runtime.core.primitives.Clock;
import org.roboticsapi.runtime.core.primitives.DoubleGreater;
import org.roboticsapi.runtime.core.primitives.Interval;
import org.roboticsapi.runtime.core.primitives.Rampify;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.EventDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.RisingEdgeDetectionFragment;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameConditional;
import org.roboticsapi.runtime.world.primitives.FrameFromPosRot;
import org.roboticsapi.runtime.world.primitives.FrameLerp;
import org.roboticsapi.runtime.world.primitives.FrameSnapshot;
import org.roboticsapi.runtime.world.primitives.FrameTransform;
import org.roboticsapi.world.Transformation;

public class CartesianCorrectionFragment extends NetFragment {

	private final DataflowOutPort result;
	private final Transformation start;

	public CartesianCorrectionFragment(Transformation startpos, DataflowOutPort realPos, double resyncTime,
			double starttime, DataflowOutPort time, DataflowOutPort innerResult) throws MappingException, RPIException {
		super("Cartesian position correction");
		this.start = startpos;

		OutPort outport;
		InPort inport1;
		InPort inport2;

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

		// calculate position for each axis
		FrameSnapshot ssp = add(new FrameSnapshot());
		connect(snapshot.getEventPort(), addInPort(new EventDataflow(), true, ssp.getInSnapshot()));
		connect(realPos, addInPort(new TransformationDataflow(), true, ssp.getInValue()));

		Transformation minusStart = start.invert();
		FrameFromPosRot expected = add(new FrameFromPosRot(minusStart.getTranslation().getX(),
				minusStart.getTranslation().getY(), minusStart.getTranslation().getZ(), minusStart.getRotation().getA(),
				minusStart.getRotation().getB(), minusStart.getRotation().getC()));

		FrameTransform diff = add(new FrameTransform());
		connect(ssp.getOutValue(), diff.getInFirst());
		connect(expected.getOutValue(), diff.getInSecond());

		FrameFromPosRot zero = add(new FrameFromPosRot(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));

		FrameLerp delta = add(new FrameLerp());
		connect(diff.getOutValue(), delta.getInFrom());
		connect(zero.getOutValue(), delta.getInTo());
		connect(ramp.getOutValue(), delta.getInAmount());

		FrameTransform add = add(new FrameTransform());
		connect(delta.getOutValue(), add.getInFirst());

		FrameConditional value = add(new FrameConditional());
		connect(t.getOutValue(), value.getInCondition());
		connect(add.getOutValue(), value.getInTrue());

		outport = value.getOutValue();
		inport1 = add.getInSecond();
		inport2 = value.getInFalse();

		connect(innerResult, addInPort(innerResult.getType(), true, inport1));
		connect(innerResult, addInPort(innerResult.getType(), true, inport2));
		result = addOutPort(innerResult.getType(), false, outport);
	}

	public DataflowOutPort getResultPort() {
		return result;
	}

}
