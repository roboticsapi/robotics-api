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

public class CartesianConstantJerkPlan implements ExecutableCartesianMotionPlan {

	private final Transformation startpos;
	private final Twist startvel;
	private final Twist startacc;
	private final Twist jerk;
	private final double starttime;
	private final Frame baseFrame;

	public CartesianConstantJerkPlan(Frame baseFrame, Transformation startpos, Twist startvel, Twist startacc,
			Twist jerk, double starttime) {
		this.baseFrame = baseFrame;
		this.startpos = startpos;
		this.startvel = startvel;
		this.startacc = startacc;
		this.jerk = jerk;
		this.starttime = starttime;
	}

	@Override
	public Transformation getTransformationAt(double time) {
		double t = time - starttime;
		if (t < 0) {
			return startpos;
		}

		Rotation rot = new Rotation(jerk.getRotVel().normalize(), jerk.getRotVel().getLength() * t * t * t / 6)
				.multiply(new Rotation(startacc.getRotVel().normalize(), startacc.getRotVel().getLength() * t * t / 2))
				.multiply(new Rotation(startvel.getRotVel().normalize(), startvel.getRotVel().getLength() * t)) //
				.multiply(startpos.getRotation());
		Vector trans = jerk.getTransVel().scale(t * t * t / 6).add(startacc.getTransVel().scale(t * t / 2))
				.add(startvel.getTransVel().scale(t)).add(startpos.getTranslation());
		return new Transformation(rot, trans);
	}

	@Override
	public Twist getTwistAt(double time) {
		double t = time - starttime;
		if (t < 0) {
			return startvel;
		}

		return new Twist(
				startvel.getTransVel().add(startacc.getTransVel().scale(t).add(jerk.getTransVel().scale(t * t / 2))),
				startvel.getRotVel().add(startacc.getRotVel().scale(t)).add(jerk.getRotVel().scale(t * t / 2)));
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
	public RealtimeTwist getTwistSensorAt(RealtimeDouble time) {
		RealtimeDouble t = time.add(-starttime);

		return RealtimeTwist.createFromLinearAngular(
				RealtimeVector.createFromConstant(startvel.getTransVel())
						.add(RealtimeVector.createFromConstant(startacc.getTransVel()).scale(t)
								.add(RealtimeVector.createFromConstant(jerk.getTransVel())
										.scale(t.multiply(t).multiply(0.5)))),
				RealtimeVector.createFromConstant(startvel.getRotVel())
						.add(RealtimeVector.createFromConstant(startacc.getRotVel()).scale(t))
						.add(RealtimeVector.createFromConstant(jerk.getRotVel()).scale(t.multiply(t).multiply(0.5))));
	}

	@Override
	public RealtimeTransformation getTransformationSensorAt(RealtimeDouble time) {
		RealtimeDouble t = time.add(-starttime);

		RealtimeRotation rot = RealtimeRotation
				.createFromAxisAngle(jerk.getRotVel().normalize(),
						t.multiply(t).multiply(t).multiply(jerk.getRotVel().getLength() / 6))
				.multiply(RealtimeRotation.createFromAxisAngle(startacc.getRotVel().normalize(),
						t.multiply(t).multiply(startacc.getRotVel().getLength() / 2)))

				.multiply(RealtimeRotation.createFromAxisAngle(startvel.getRotVel().normalize(),
						t.multiply(startvel.getRotVel().getLength())))
				.multiply(startpos.getRotation());
		RealtimeVector trans = RealtimeVector.createFromConstant(jerk.getTransVel())
				.scale(t.multiply(t).multiply(t).divide(6))
				.add(RealtimeVector.createFromConstant(startacc.getTransVel()).scale(t.multiply(t).divide(2)))
				.add(RealtimeVector.createFromConstant(startvel.getTransVel()).scale(t)).add(startpos.getTranslation());
		return RealtimeTransformation.createFromVectorRotation(trans, rot);
	}

	@Override
	public RealtimeBoolean getStateSensorAt(RealtimeDouble time, RealtimeBoolean state) {
		return RealtimeBoolean.FALSE;
	}

	@Override
	public Double getTimeForTransformation(Transformation t) {
		// TODO! Solve x^3 equation

		return null;

		// x = x0 + v0 * t + 1/2 a * t^2
		// -> t = (-v0 +/- sqrt(v0^2 - 2*a*(x0 - x))) / a

		// // calculate t based on translation in x
		// double term = Math.sqrt(startvel.getTransVel().getX() *
		// startvel.getTransVel().getX()
		// + 2 * jerk.getTransVel().getX() * (startpos.getTranslation().getX() -
		// t.getTranslation().getX()));
		// double t_x_1 = (-startvel.getTransVel().getX() + term) /
		// jerk.getTransVel().getX();
		// double t_x_2 = (-startvel.getTransVel().getX() - term) /
		// jerk.getTransVel().getX();
		//
		// double time = Math.min(t_x_1, t_x_2);
		//
		// // check validity
		// if (time < starttime) {
		// time = Math.max(t_x_1, t_x_2);
		// if (time < starttime) {
		// return null;
		// }
		// }
		//
		// // check if result complies to all other values
		// double constantFactor = time * time / 2d;
		//
		// double y = startpos.getTranslation().getY() + startvel.getTransVel().getY() *
		// time
		// + jerk.getTransVel().getY() * constantFactor;
		// if (Math.abs(y - t.getTranslation().getY()) > 0.001) {
		// return null;
		// }
		//
		// double z = startpos.getTranslation().getZ() + startvel.getTransVel().getZ() *
		// time
		// + jerk.getTransVel().getZ() * constantFactor;
		// if (Math.abs(z - t.getTranslation().getZ()) > 0.001) {
		// return null;
		// }
		//
		// double angle = startpos.getRotation().getAngle() +
		// startvel.getRotVel().getLength() * time
		// + jerk.getRotVel().getLength() * constantFactor;
		// if (Math.abs(angle - t.getRotation().getAngle()) > Math.toRadians(0.01)) {
		// return null;
		// }
		//
		// // check that rotary acceleration axis matches given goal rotation axis
		// if (jerk.getRotVel().getLength() < 0.001 ||
		// t.getRotation().getAxis().getLength() < 0.001) {
		// return null;
		// }
		// if (jerk.getRotVel().cross(t.getRotation().getAxis()).getLength() > 0.001) {
		// return null;
		// }
		//
		// // if everything is okay, return result
		// return time;
	}

	@Override
	public Double getTimeForTwist(Twist t) {
		return null;
	}
}
