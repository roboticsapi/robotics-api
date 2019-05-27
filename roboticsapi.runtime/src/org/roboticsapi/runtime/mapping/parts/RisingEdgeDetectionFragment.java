/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.parts;

import org.roboticsapi.runtime.core.primitives.BooleanPre;
import org.roboticsapi.runtime.core.primitives.EdgeDetection;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.EventDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class RisingEdgeDetectionFragment extends NetFragment {

	private DataflowOutPort eventPort;

	public RisingEdgeDetectionFragment(DataflowOutPort signalPort) throws MappingException {
		this(signalPort, false);
	}

	public RisingEdgeDetectionFragment(DataflowOutPort signalPort, boolean delayed) throws MappingException {
		super("EdgeDetection");

		if (signalPort == null) {
			throw new MappingException("Given OutPorts may not be null");
		}

		if (signalPort.getType() instanceof EventDataflow) {
			setEventPort(signalPort);
		} else {

			EdgeDetection ed = add(new EdgeDetection(true));
			connect(signalPort, addInPort(signalPort.getType(), true, ed.getInValue()));
			OutPort out = ed.getOutValue();

			// BooleanNot not = new BooleanNot();
			// add(not);
			//
			// BooleanPre pre = add(new BooleanPre());
			// BooleanAnd and = add(new BooleanAnd());
			//
			// addInPort(signalPort.getType(), true,
			// pre.getInValue()).connectTo(
			// signalPort);
			// addInPort(signalPort.getType(), true,
			// and.getInFirst()).connectTo(
			// signalPort);
			// OutPort out = and.getOutValue();

			try {
				// not.getInValue().connectTo(pre.getOutValue());
				// and.getInSecond().connectTo(not.getOutValue());

				if (delayed) {
					BooleanPre pre2 = add(new BooleanPre());
					pre2.getInValue().connectTo(out);
					out = pre2.getOutValue();
				}
			} catch (RPIException e) {
				throw new MappingException(e);
			}

			DataflowOutPort eventPort = addOutPort(new EventDataflow(), true, out);
			setEventPort(eventPort);
		}
	}

	private void setEventPort(DataflowOutPort eventPort) {
		this.eventPort = eventPort;
	}

	public DataflowOutPort getEventPort() {
		return eventPort;
	}
}
