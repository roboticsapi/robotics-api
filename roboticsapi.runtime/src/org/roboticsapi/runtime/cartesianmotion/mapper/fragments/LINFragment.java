/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper.fragments;

import org.roboticsapi.cartesianmotion.parameter.CartesianParameters;
import org.roboticsapi.core.actuator.OverrideParameter;
import org.roboticsapi.runtime.core.primitives.Clock;
import org.roboticsapi.runtime.core.primitives.DoubleGreater;
import org.roboticsapi.runtime.core.primitives.Interval;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;

public class LINFragment extends CancellableMotionFragment implements CartesianMotionFragment {

	private final DataflowOutPort resultPort;
	private final DataflowOutPort progressPort;
	private final CartesianMotionFragment completeMotion;

	// private final double duration;
	// private final Frame base;

	public LINFragment(ActionMappingContext ports, CartesianParameters p, Frame base, Transformation from,
			Transformation to, Frame motionCenter, OverrideParameter ovp, double accelTime, double constantTime)
			throws RPIException, MappingException {
		super("LIN", ports, 1d / accelTime);

		// TODO: implement base and duration functionality
		// this.base = base;
		// this.duration = constantTime + 2 * accelTime;

		Transformation delta = to.multiply(from.invert());
		Rotation rotDelta = delta.getRotation();
		Vector rotAxis = rotDelta.getAxis();

		Twist fromVel, consVel;
		Twist accelAcc, decelAcc;
		Transformation consPos, decelPos;

		consVel = new Twist( //
				delta.getTranslation().scale(1 / (accelTime + constantTime)), //
				rotAxis.scale(rotDelta.getAngle() / (accelTime + constantTime)));
		fromVel = new Twist(0, 0, 0, 0, 0, 0);
		accelAcc = new Twist( //
				consVel.getTransVel().scale(1 / accelTime), //
				consVel.getRotVel().scale(1 / accelTime));
		decelAcc = new Twist( //
				consVel.getTransVel().scale(-1 / accelTime), //
				consVel.getRotVel().scale(-1 / accelTime));
		consPos = new Transformation(
				from.getRotation()
						.multiply(new Rotation(accelAcc.getRotVel().normalize(),
								accelAcc.getRotVel().getLength() * accelTime * accelTime / 2)), //
				from.getTranslation().add(accelAcc.getTransVel().scale(accelTime * accelTime / 2)));
		decelPos = new Transformation(
				consPos.getRotation().multiply(
						new Rotation(consVel.getRotVel().normalize(), consVel.getRotVel().getLength() * constantTime)), //
				consPos.getTranslation().add(consVel.getTransVel().scale(constantTime)));

		final Clock clock = add(new Clock(1.0));

		DataflowOutPort overridePort = getInterpolatedOverridePort();
		connect(overridePort, addInPort(new DoubleDataflow(), true, clock.getInIncrement()));
		DataflowOutPort time = addOutPort(new DoubleDataflow(), false, clock.getOutValue());

		CartesianConstantAccelerationFragment accel = add(
				new CartesianConstantAccelerationFragment(from, fromVel, accelAcc, 0, time));

		CartesianConstantVelocityFragment constant = add(
				new CartesianConstantVelocityFragment(consPos, consVel, accelTime, time));

		CartesianConstantAccelerationFragment decel = add(
				new CartesianConstantAccelerationFragment(decelPos, consVel, decelAcc, accelTime + constantTime, time));

		CartesianConstantPositionFragment hold = add(new CartesianConstantPositionFragment(to));

		// completed logic
		DoubleGreater completed = add(new DoubleGreater(0d, accelTime + constantTime + accelTime));
		completed.getInFirst().connectTo(clock.getOutValue());

		// progress
		Interval progress = add(new Interval(0.0, accelTime + constantTime + accelTime));
		progress.getInValue().connectTo(clock.getOutValue());

		CartesianMotionFragment decelHold = add(
				new CartesianConditionalFragment(decel, hold, constantTime + accelTime, accelTime, time));
		CartesianMotionFragment constantDecelHold = add(
				new CartesianConditionalFragment(constant, decelHold, accelTime, constantTime, time));
		completeMotion = add(new CartesianConditionalFragment(accel, constantDecelHold, 0, accelTime, time));
		resultPort = reinterpret(completeMotion.getResultPort(), new RelationDataflow(base, motionCenter));
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
	public Transformation getTransformationAt(double time) {
		return completeMotion.getTransformationAt(time);
	}

	@Override
	public Twist getTwistAt(double time) {
		return completeMotion.getTwistAt(time);
	}

}
