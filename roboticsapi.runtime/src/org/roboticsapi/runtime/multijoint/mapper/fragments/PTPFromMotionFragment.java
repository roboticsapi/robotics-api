/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper.fragments;

import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.runtime.core.primitives.Clock;
import org.roboticsapi.runtime.core.primitives.DoubleGreater;
import org.roboticsapi.runtime.core.primitives.Interval;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.rpi.RPIException;

public class PTPFromMotionFragment extends CancellableMotionFragment implements JointMotionFragment {

	private final DataflowOutPort resultPort;
	private final DataflowOutPort progressPort;
	private final JointMotionFragment completeMotion;
	// private final double duration;
	private final double startTime;

	public PTPFromMotionFragment(ActionMappingContext ports, JointDeviceParameters p, OverrideParameter ovp,
			double[] from, double[] to, double[] realStart, double[] realStartVel, double accelTime,
			double constantTime, double realAccelTime) throws RPIException, MappingException {
		super("PTP", ports, 1d / accelTime);

		// TODO: implement duration functionality
		// this.duration = constantTime + 2 * accelTime;
		startTime = accelTime - realAccelTime;

		double[] fromVel = new double[from.length], consVel = new double[from.length];
		double[] accelAcc = new double[from.length], decelAcc = new double[from.length];
		double[] consPos = new double[from.length], decelPos = new double[from.length];
		for (int i = 0; i < from.length; i++) {
			fromVel[i] = 0;
			consVel[i] = (to[i] - from[i]) / (accelTime + constantTime);
			accelAcc[i] = consVel[i] / accelTime;
			decelAcc[i] = -consVel[i] / accelTime;
			consPos[i] = from[i] + accelAcc[i] * accelTime * accelTime / 2;
			decelPos[i] = consPos[i] + consVel[i] * constantTime;
		}

		final Clock clock = add(new Clock(1.0));

		clock.getInIncrement().setDebug(10);

		DataflowOutPort overridePort = getInterpolatedOverridePort();

		connect(overridePort, addInPort(new DoubleDataflow(), true, clock.getInIncrement()));
		DataflowOutPort time = addOutPort(new DoubleDataflow(), false, clock.getOutValue());

		JointBezierFragment accel = add(
				new JointBezierFragment(realStart, realStartVel, consPos, consVel, 0, realAccelTime, time));

		JointConstantVelocityFragment constant = add(
				new JointConstantVelocityFragment(consPos, consVel, realAccelTime, time));

		JointConstantAccelerationFragment decel = add(
				new JointConstantAccelerationFragment(decelPos, consVel, decelAcc, realAccelTime + constantTime, time));

		JointConstantPositionFragment hold = add(new JointConstantPositionFragment(to));

		// completed logic
		DoubleGreater completed = add(new DoubleGreater(0d, realAccelTime + constantTime + accelTime));
		completed.getInFirst().connectTo(clock.getOutValue());

		// progress
		Interval progress = add(new Interval(realAccelTime - accelTime, realAccelTime + constantTime + accelTime));
		progress.getInValue().connectTo(clock.getOutValue());

		JointMotionFragment decelHold = add(
				new JointConditionalFragment(decel, hold, realAccelTime + constantTime, accelTime, time));
		JointMotionFragment constantDecelHold = add(
				new JointConditionalFragment(constant, decelHold, realAccelTime, constantTime, time));
		completeMotion = add(new JointConditionalFragment(accel, constantDecelHold, 0, realAccelTime, time));
		resultPort = completeMotion.getResultPort();
		setMotionCompletedPort(addOutPort(new StateDataflow(), true, completed.getOutValue()));
		progressPort = addOutPort(new DoubleDataflow(), true, progress.getOutValue());
	}

	@Override
	public DataflowOutPort getResultPort() {
		return resultPort;
	}

	public DataflowOutPort getProgressPort() {
		return progressPort;
	}

	@Override
	public double[] getJointPositionsAt(double time) {
		return completeMotion.getJointPositionsAt(time - startTime);
	}

	@Override
	public double[] getJointVelocitiesAt(double time) {
		return completeMotion.getJointVelocitiesAt(time - startTime);
	}

	@Override
	public double[] getJointAccelerationsAt(double time) {
		return completeMotion.getJointAccelerationsAt(time - startTime);
	}

}
