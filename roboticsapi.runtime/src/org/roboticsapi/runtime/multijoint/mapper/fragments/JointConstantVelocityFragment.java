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

public class JointConstantVelocityFragment extends NetFragment implements JointMotionFragment {

	private final DataflowOutPort result;
	private final double[] start;
	private final double[] vel;
	private final double starttime;

	public JointConstantVelocityFragment(double[] startpos, double[] vel, double starttime, DataflowOutPort time)
			throws MappingException, RPIException {
		super("Constant velocity");
		this.start = startpos;
		this.vel = vel;
		this.starttime = starttime;

		OutPort[] ports = new OutPort[startpos.length];

		// calculate time
		DoubleAdd t = add(new DoubleAdd());
		t.setFirst(-starttime);
		connect(time, addInPort(new DoubleDataflow(), true, t.getInSecond()));

		// calculate position for each axis
		for (int i = 0; i < startpos.length; i++) {
			// v*t
			DoubleMultiply dpos = add(new DoubleMultiply());
			dpos.setFirst(vel[i]);
			dpos.getInSecond().connectTo(t.getOutValue());
			// s + v*t
			DoubleAdd pos = add(new DoubleAdd());
			pos.setFirst(startpos[i]);
			pos.getInSecond().connectTo(dpos.getOutValue());
			ports[i] = pos.getOutValue();
		}
		result = addOutPort(new JointsDataflow(startpos.length, null), false, ports);
	}

	@Override
	public DataflowOutPort getResultPort() {
		return result;
	}

	@Override
	public double[] getJointPositionsAt(double time) {
		double[] ret = new double[start.length];
		for (int i = 0; i < start.length; i++) {
			ret[i] = start[i] + (time - starttime) * vel[i];
		}
		return ret;
	}

	@Override
	public double[] getJointVelocitiesAt(double time) {
		double[] ret = new double[start.length];
		for (int i = 0; i < start.length; i++) {
			ret[i] = vel[i];
		}
		return ret;
	}

	@Override
	public double[] getJointAccelerationsAt(double time) {
		double[] ret = new double[start.length];
		for (int i = 0; i < start.length; i++) {
			ret[i] = 0;
		}
		return ret;
	}
}
