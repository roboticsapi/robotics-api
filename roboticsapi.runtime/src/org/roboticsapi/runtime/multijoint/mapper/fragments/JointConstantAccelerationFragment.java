/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper.fragments;

import org.roboticsapi.runtime.core.primitives.DoubleAdd;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class JointConstantAccelerationFragment extends NetFragment implements JointMotionFragment {

	private final DataflowOutPort result;
	private final double[] startpos;
	private final double[] startvel;
	private final double[] acc;
	private final double starttime;

	public JointConstantAccelerationFragment(double[] startpos, double[] startvel, double[] acc, double starttime,
			DataflowOutPort time) throws MappingException, RPIException {
		super("Constant Acceleration");
		this.startpos = startpos;
		this.startvel = startvel;
		this.acc = acc;
		this.starttime = starttime;

		OutPort[] ports = new OutPort[startpos.length];

		// calculate time
		DoubleAdd t = add(new DoubleAdd());
		t.setFirst(-starttime);
		connect(time, addInPort(new DoubleDataflow(), true, t.getInSecond()));

		DoubleMultiply tSquare = add(new DoubleMultiply());
		tSquare.getInFirst().connectTo(t.getOutValue());
		tSquare.getInSecond().connectTo(t.getOutValue());

		// calculate position for each axis
		for (int i = 0; i < startpos.length; i++) {
			// v*t
			DoubleMultiply consS = add(new DoubleMultiply());
			consS.setFirst(startvel[i]);
			consS.getInSecond().connectTo(t.getOutValue());

			// a/2*t*t
			DoubleMultiply accS = add(new DoubleMultiply());
			accS.setFirst(acc[i] / 2);
			accS.getInSecond().connectTo(tSquare.getOutValue());

			// v*t + a/2*t*t
			DoubleAdd dpos = add(new DoubleAdd());
			dpos.getInFirst().connectTo(consS.getOutValue());
			dpos.getInSecond().connectTo(accS.getOutValue());

			// s + v*t + a/2*t*t
			DoubleAdd result = add(new DoubleAdd());
			result.setFirst(startpos[i]);
			result.getInSecond().connectTo(dpos.getOutValue());
			ports[i] = result.getOutValue();
		}
		result = addOutPort(new JointsDataflow(startpos.length, null), false, ports);
	}

	@Override
	public DataflowOutPort getResultPort() {
		return result;
	}

	@Override
	public double[] getJointPositionsAt(double time) {
		double[] ret = new double[startpos.length];
		double t = time - starttime;
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = startpos[i] + t * startvel[i] + t * t * acc[i] / 2;
		}
		return ret;
	}

	@Override
	public double[] getJointVelocitiesAt(double time) {
		double[] ret = new double[startpos.length];
		double t = time - starttime;
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = startvel[i] + acc[i] * t;
		}
		return ret;
	}

	@Override
	public double[] getJointAccelerationsAt(double time) {
		double[] ret = new double[startpos.length];
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = acc[i];
		}
		return ret;
	}
}
