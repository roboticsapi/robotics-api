/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper.fragments;

import org.roboticsapi.runtime.core.primitives.DoubleValue;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class JointConstantPositionFragment extends NetFragment implements JointMotionFragment {

	private final DataflowOutPort result;
	private final double[] pos;

	public JointConstantPositionFragment(double[] startpos) throws MappingException, RPIException {
		super("Constant velocity");
		this.pos = startpos;

		OutPort[] ports = new OutPort[startpos.length];

		// calculate position for each axis
		for (int i = 0; i < startpos.length; i++) {
			DoubleValue value = add(new DoubleValue(pos[i]));
			ports[i] = value.getOutValue();
		}
		result = addOutPort(new JointsDataflow(startpos.length, null), false, ports);
	}

	@Override
	public DataflowOutPort getResultPort() {
		return result;
	}

	@Override
	public double[] getJointPositionsAt(double time) {
		return pos;
	}

	@Override
	public double[] getJointVelocitiesAt(double time) {
		return new double[pos.length];
	}

	@Override
	public double[] getJointAccelerationsAt(double time) {
		return new double[pos.length];
	}
}
