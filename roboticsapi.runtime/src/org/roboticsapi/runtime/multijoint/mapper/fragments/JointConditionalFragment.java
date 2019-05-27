/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper.fragments;

import org.roboticsapi.runtime.core.primitives.DoubleConditional;
import org.roboticsapi.runtime.core.primitives.Interval;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class JointConditionalFragment extends NetFragment implements JointMotionFragment {

	private final DataflowOutPort result;
	private final JointMotionFragment main;
	private final JointMotionFragment other;
	private final double start;
	private final double duration;

	public JointConditionalFragment(JointMotionFragment main, JointMotionFragment other, double start, double duration,
			DataflowOutPort time) throws MappingException, RPIException {
		super("JointJoiner");
		this.main = main;
		this.other = other;
		this.start = start;
		this.duration = duration;

		int count = main.getResultPort().getType().getPortCount();
		InPort[] first = new InPort[count], second = new InPort[count];
		OutPort[] ports = new OutPort[count];
		Interval progress = add(new Interval(start, start + duration));
		connect(time, addInPort(new DoubleDataflow(), true, progress.getInValue()));
		for (int i = 0; i < count; i++) {
			DoubleConditional cond = add(new DoubleConditional());
			cond.getInCondition().connectTo(progress.getOutActive());
			first[i] = cond.getInTrue();
			second[i] = cond.getInFalse();
			ports[i] = cond.getOutValue();
		}

		connect(main.getResultPort(), addInPort(main.getResultPort().getType(), false, first));
		connect(other.getResultPort(), addInPort(other.getResultPort().getType(), false, second));
		result = addOutPort(main.getResultPort().getType(), false, ports);
	}

	@Override
	public DataflowOutPort getResultPort() {
		return result;
	}

	@Override
	public double[] getJointPositionsAt(double time) {
		if (time >= start && time < start + duration) {
			return main.getJointPositionsAt(time);
		} else {
			return other.getJointPositionsAt(time);
		}
	}

	@Override
	public double[] getJointVelocitiesAt(double time) {
		if (time >= start && time < start + duration) {
			return main.getJointVelocitiesAt(time);
		} else {
			return other.getJointVelocitiesAt(time);
		}
	}

	@Override
	public double[] getJointAccelerationsAt(double time) {
		if (time >= start && time < start + duration) {
			return main.getJointAccelerationsAt(time);
		} else {
			return other.getJointAccelerationsAt(time);
		}
	}
}
