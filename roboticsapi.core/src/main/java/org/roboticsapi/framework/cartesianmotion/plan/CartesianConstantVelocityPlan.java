/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.cartesianmotion.plan;

import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;
import org.roboticsapi.framework.cartesianmotion.action.ExecutableCartesianMotionPlan;

public class CartesianConstantVelocityPlan implements ExecutableCartesianMotionPlan {

	private final Transformation startpos;
	private final Twist vel;
	private final double starttime;
	private final Frame baseFrame;

	public CartesianConstantVelocityPlan(Frame baseFrame, Transformation startpos, Twist vel, double starttime) {
		this.baseFrame = baseFrame;
		this.startpos = startpos;
		this.vel = vel;
		this.starttime = starttime;
	}

	@Override
	public Transformation getTransformationAt(double time) {
		double t = time - starttime;
		if (t < 0) {
			return startpos;
		}

		Rotation rot = new Rotation(vel.getRotVel().normalize(), vel.getRotVel().getLength() * t)
				.multiply(startpos.getRotation());
		Vector trans = vel.getTransVel().scale(t).add(startpos.getTranslation());
		return new Transformation(rot, trans);
	}

	@Override
	public Twist getTwistAt(double time) {
		double t = time - starttime;
		if (t < 0) {
			return new Twist();
		}

		return vel;
	}

	@Override
	public double getTotalTime() {
		return 0;
	}

	@Override
	public Double getTimeAtFirstOccurence(RealtimeBoolean state) {
		return null;
	}

	@Override
	public Frame getBaseFrame() {
		return baseFrame;
	}

	@Override
	public RealtimeTransformation getTransformationSensorAt(RealtimeDouble time) {
		RealtimeDouble t = time.add(-starttime);

		RealtimeRotation rot = RealtimeRotation
				.createFromAxisAngle(vel.getRotVel().normalize(), t.multiply(vel.getRotVel().getLength()))
				.multiply(startpos.getRotation());
		RealtimeVector trans = RealtimeVector.createFromConstant(vel.getTransVel()).scale(t)
				.add(startpos.getTranslation());
		return RealtimeTransformation.createFromVectorRotation(trans, rot);
	}

	@Override
	public RealtimeTwist getTwistSensorAt(RealtimeDouble time) {
		return RealtimeTwist.createFromConstant(vel);
	}

	@Override
	public RealtimeBoolean getStateSensorAt(RealtimeDouble time, RealtimeBoolean state) {
		return RealtimeBoolean.FALSE;
	}

	@Override
	public Double getTimeForTransformation(Transformation t) {
		// x = x0 + v * t -> t = (x - x0) / v

		// calculate time for x translation
		double t_x = (t.getTranslation().getX() - startpos.getTranslation().getX()) / vel.getTransVel().getX();

		// calculate time for y translation
		double t_y = (t.getTranslation().getY() - startpos.getTranslation().getY()) / vel.getTransVel().getY();

		if (Math.abs(t_y - t_x) > 0.001) {
			return null;
		}

		// calculate time for z translation
		double t_z = (t.getTranslation().getZ() - startpos.getTranslation().getZ()) / vel.getTransVel().getZ();

		if (Math.abs(t_z - t_x) > 0.001) {
			return null;
		}

		double angle = startpos.getRotation().getAngle() + vel.getRotVel().getLength() * t_x;
		if (Math.abs(angle - t.getRotation().getAngle()) > Math.toRadians(0.01)) {
			return null;
		}

		// check that rotary acceleration axis matches given goal rotation axis
		if (vel.getRotVel().getLength() < 0.001 || t.getRotation().getAxis().getLength() < 0.001) {
			return null;
		}
		if (vel.getRotVel().cross(t.getRotation().getAxis()).getLength() > 0.001) {
			return null;
		}

		return t_x;
	}

	@Override
	public Double getTimeForTwist(Twist t) {
		// TODO Auto-generated method stub
		return null;
	}
}
