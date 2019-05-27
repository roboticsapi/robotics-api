/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper.fragments;

import org.roboticsapi.runtime.core.primitives.DoubleBezier;
import org.roboticsapi.runtime.core.primitives.Interval;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class JointBezierFragment extends NetFragment implements JointMotionFragment {

	private final DataflowOutPort result;
	private final double[] startpos;
	private final double[] startvel;
	private final double[] destpos;
	private final double[] destvel;
	private final double starttime;
	private final double duration;

	public JointBezierFragment(double[] startpos, double[] startvel, double[] destpos, double[] destvel,
			double starttime, double duration, DataflowOutPort time) throws MappingException, RPIException {
		super("Bezier");
		this.startpos = startpos;
		this.startvel = startvel;
		this.destpos = destpos;
		this.destvel = destvel;
		this.starttime = starttime;
		this.duration = duration;

		OutPort[] ports = new OutPort[startpos.length];

		// calculate time (normalized)
		Interval t = add(new Interval(starttime, starttime + duration));

		connect(time, addInPort(new DoubleDataflow(), true, t.getInValue()));

		// calculate position for each axis
		for (int i = 0; i < startpos.length; i++) {
			// normalize to [0,1]
			DoubleBezier bezier = add(
					new DoubleBezier(startpos[i], destpos[i], startvel[i] * duration, destvel[i] * duration));
			bezier.getInValue().connectTo(t.getOutValue());
			ports[i] = bezier.getOutValue();
		}
		result = addOutPort(new JointsDataflow(startpos.length, null), false, ports);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.roboticsapi.runtime.softrobot.robot.mapper.fragments.JointMotionFragment
	 * #getResultPort()
	 */
	@Override
	public DataflowOutPort getResultPort() {
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.roboticsapi.runtime.softrobot.robot.mapper.fragments.JointMotionFragment
	 * #getJointPositionsAt(double)
	 */
	@Override
	public double[] getJointPositionsAt(double time) {
		double[] ret = new double[startpos.length];
		double t = (time - starttime) / duration;
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = calculateBezier(t, startpos[i], destpos[i], startvel[i] * duration, destvel[i] * duration);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.roboticsapi.runtime.softrobot.robot.mapper.fragments.JointMotionFragment
	 * #getJointVelocitiesAt(double)
	 */
	@Override
	public double[] getJointVelocitiesAt(double time) {
		double[] ret = new double[startpos.length];
		double t = (time - starttime) / duration;
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = calculateBezierVel(t, startpos[i], destpos[i], startvel[i] * duration, destvel[i] * duration)
					/ duration;
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.roboticsapi.runtime.softrobot.robot.mapper.fragments.JointMotionFragment
	 * #getJointAccelerationsAt(double)
	 */
	@Override
	public double[] getJointAccelerationsAt(double time) {
		double[] ret = new double[startpos.length];
		double t = (time - starttime) / duration;
		for (int i = 0; i < startpos.length; i++) {
			ret[i] = calculateBezierAcc(t, startpos[i], destpos[i], startvel[i] * duration, destvel[i] * duration)
					/ duration / duration;
		}
		return ret;
	}

	private double calculateBezier(double time, double from, double to, double fromvel, double tovel) {
		double control1 = from + fromvel / 3;
		double control2 = to - tovel / 3;
		double a = from;
		double b = -3 * from + 3 * control1;
		double c = 3 * from - 6 * control1 + 3 * control2;
		double d = -from + 3 * control1 - 3 * control2 + to;
		double t = time;
		return a + t * (b + t * (c + d * t));
	}

	private double calculateBezierVel(double time, double from, double to, double fromvel, double tovel) {
		double control1 = from + fromvel / 3;
		double control2 = to - tovel / 3;
		double b = -3 * from + 3 * control1;
		double c = 3 * from - 6 * control1 + 3 * control2;
		double d = -from + 3 * control1 - 3 * control2 + to;
		double t = time;
		return b + t * (2 * c + 3 * d * t);
	}

	private double calculateBezierAcc(double time, double from, double to, double fromvel, double tovel) {
		double control1 = from + fromvel / 3;
		double control2 = to - tovel / 3;
		double c = 3 * from - 6 * control1 + 3 * control2;
		double d = -from + 3 * control1 - 3 * control2 + to;
		double t = time;
		return 2 * c + 6 * d * t;
	}

}
