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
import org.roboticsapi.core.world.Quaternion;
import org.roboticsapi.core.world.Rotation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Twist;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetwist.RealtimeTwist;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;
import org.roboticsapi.framework.cartesianmotion.action.ExecutableCartesianMotionPlan;

public class CartesianBezierPlan implements ExecutableCartesianMotionPlan {

	private final Transformation startpos;
	private final Transformation destpos;
	private final Twist startvel;
	private final Twist destvel;
	private final double starttime;
	private final double duration;
	private final Frame baseFrame;

	public CartesianBezierPlan(Frame base, Transformation startpos, Twist startvel, Transformation destpos,
			Twist destvel, double starttime, double duration) {
		this.baseFrame = base;
		this.startpos = startpos;
		this.destpos = destpos;
		this.startvel = startvel;
		this.destvel = destvel;
		this.starttime = starttime;
		this.duration = duration;

	}

	@Override
	public Transformation getTransformationAt(double time) {
		double t = (time - starttime) / duration;
		if (t < 0) {
			return startpos;
		}

		double x = calculateBezier(t, startpos.getTranslation().getX(), destpos.getTranslation().getX(),
				startvel.getTransVel().getX() * duration, destvel.getTransVel().getX() * duration);
		double y = calculateBezier(t, startpos.getTranslation().getY(), destpos.getTranslation().getY(),
				startvel.getTransVel().getY() * duration, destvel.getTransVel().getY() * duration);
		double z = calculateBezier(t, startpos.getTranslation().getZ(), destpos.getTranslation().getZ(),
				startvel.getTransVel().getZ() * duration, destvel.getTransVel().getZ() * duration);
		Vector trans = new Vector(x, y, z);

		Rotation rot = calculateBezier(t, startpos.getRotation(), destpos.getRotation(),
				startvel.getRotVel().scale(duration), destvel.getRotVel().scale(duration));
		return new Transformation(rot, trans);
	}

	@Override
	public Twist getTwistAt(double time) {
		Transformation pos = getTransformationAt(time), next = getTransformationAt(time + 0.01);
		Vector transVel = next.getTranslation().sub(pos.getTranslation()).scale(100);
		Rotation rot = pos.getRotation().invert().multiply(next.getRotation());
		Vector rotVel = pos.getRotation().apply(rot.getAxis().scale(rot.getAngle() * 100));
		return new Twist(transVel, rotVel);
	}

	private Rotation calculateBezier(double time, Rotation from, Rotation to, Vector fromvel, Vector tovel) {
		Rotation control1 = from
				.multiply(new Rotation(from.invert().apply(fromvel.normalize()), fromvel.getLength() / 3));
		Rotation control2 = to.multiply(new Rotation(to.invert().apply(tovel.normalize()), -tovel.getLength() / 3));

		Rotation fromInv = from.invert();
		Quaternion qFrom = fromInv.multiply(from).getQuaternion(), qTo = fromInv.multiply(to).getQuaternion(),
				qC1 = fromInv.multiply(control1).getQuaternion(), qC2 = fromInv.multiply(control2).getQuaternion();
		double x = calculateBezierByControlPoints(time, qFrom.getX(), qTo.getX(), qC1.getX(), qC2.getX());
		double y = calculateBezierByControlPoints(time, qFrom.getY(), qTo.getY(), qC1.getY(), qC2.getY());
		double z = calculateBezierByControlPoints(time, qFrom.getZ(), qTo.getZ(), qC1.getZ(), qC2.getZ());
		double w = calculateBezierByControlPoints(time, qFrom.getW(), qTo.getW(), qC1.getW(), qC2.getW());
		return from.multiply(new Rotation(new Quaternion(x, y, z, w)));
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

	private double calculateBezierByControlPoints(double time, double from, double to, double control1,
			double control2) {
		double a = from;
		double b = -3 * from + 3 * control1;
		double c = 3 * from - 6 * control1 + 3 * control2;
		double d = -from + 3 * control1 - 3 * control2 + to;
		double t = time;
		return a + t * (b + t * (c + d * t));
	}

	private RealtimeRotation calculateBezier(RealtimeDouble time, Rotation from, Rotation to, Vector fromvel,
			Vector tovel) {
		Rotation control1 = from
				.multiply(new Rotation(from.invert().apply(fromvel.normalize()), fromvel.getLength() / 3));
		Rotation control2 = to.multiply(new Rotation(to.invert().apply(tovel.normalize()), -tovel.getLength() / 3));
		Rotation fromInv = from.invert();
		Quaternion qFrom = fromInv.multiply(from).getQuaternion(), qTo = fromInv.multiply(to).getQuaternion(),
				qC1 = fromInv.multiply(control1).getQuaternion(), qC2 = fromInv.multiply(control2).getQuaternion();
		RealtimeDouble x = calculateBezierByControlPoints(time, qFrom.getX(), qTo.getX(), qC1.getX(), qC2.getX());
		RealtimeDouble y = calculateBezierByControlPoints(time, qFrom.getY(), qTo.getY(), qC1.getY(), qC2.getY());
		RealtimeDouble z = calculateBezierByControlPoints(time, qFrom.getZ(), qTo.getZ(), qC1.getZ(), qC2.getZ());
		RealtimeDouble w = calculateBezierByControlPoints(time, qFrom.getW(), qTo.getW(), qC1.getW(), qC2.getW());
		RealtimeDouble len = x.multiply(x).add(y.multiply(y)).add(z.multiply(z)).add(w.multiply(w)).sqrt();
		return RealtimeRotation.createFromConstant(from).multiply(
				RealtimeRotation.createFromQuaternion(x.divide(len), y.divide(len), z.divide(len), w.divide(len)));
	}

	private RealtimeDouble calculateBezier(RealtimeDouble t, double from, double to, double fromvel, double tovel) {
		double control1 = from + fromvel / 3;
		double control2 = to - tovel / 3;
		double a = from;
		double b = -3 * from + 3 * control1;
		double c = 3 * from - 6 * control1 + 3 * control2;
		double d = -from + 3 * control1 - 3 * control2 + to;
		return t.multiply(d).add(c).multiply(t).add(b).multiply(t).add(a);
	}

	private RealtimeDouble calculateBezierByControlPoints(RealtimeDouble t, double from, double to, double control1,
			double control2) {
		double a = from;
		double b = -3 * from + 3 * control1;
		double c = 3 * from - 6 * control1 + 3 * control2;
		double d = -from + 3 * control1 - 3 * control2 + to;
		return t.multiply(d).add(c).multiply(t).add(b).multiply(t).add(a);
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
		return getTransformationSensorAt(time).derive();
	}

	@Override
	public RealtimeTransformation getTransformationSensorAt(RealtimeDouble time) {
		RealtimeDouble t = time.add(-starttime).divide(duration);

		RealtimeDouble x = calculateBezier(t, startpos.getTranslation().getX(), destpos.getTranslation().getX(),
				startvel.getTransVel().getX() * duration, destvel.getTransVel().getX() * duration);
		RealtimeDouble y = calculateBezier(t, startpos.getTranslation().getY(), destpos.getTranslation().getY(),
				startvel.getTransVel().getY() * duration, destvel.getTransVel().getY() * duration);
		RealtimeDouble z = calculateBezier(t, startpos.getTranslation().getZ(), destpos.getTranslation().getZ(),
				startvel.getTransVel().getZ() * duration, destvel.getTransVel().getZ() * duration);
		RealtimeVector trans = RealtimeVector.createFromXYZ(x, y, z);

		RealtimeRotation rot = calculateBezier(t, startpos.getRotation(), destpos.getRotation(),
				startvel.getRotVel().scale(duration), destvel.getRotVel().scale(duration));
		return RealtimeTransformation.createFromVectorRotation(trans, rot);
	}

	@Override
	public RealtimeBoolean getStateSensorAt(RealtimeDouble time, RealtimeBoolean state) {
		return RealtimeBoolean.FALSE;
	}

	@Override
	public Double getTimeForTransformation(Transformation t) {
		// TODO implement for this plan
		return null;
	}

	@Override
	public Double getTimeForTwist(Twist t) {
		// TODO implement for this plan
		return null;
	}
}
