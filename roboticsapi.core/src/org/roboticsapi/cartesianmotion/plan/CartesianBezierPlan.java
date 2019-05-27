/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.plan;

import org.roboticsapi.cartesianmotion.action.ExecutableCartesianMotionPlan;
import org.roboticsapi.core.State;
import org.roboticsapi.core.sensor.BooleanSensor;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Quaternion;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.RotationSensor;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VectorSensor;

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

		Quaternion qFrom = from.getQuaternion(), qTo = to.getQuaternion(), qC1 = control1.getQuaternion(),
				qC2 = control2.getQuaternion();
		double x = calculateBezierByControlPoints(time, qFrom.getX(), qTo.getX(), qC1.getX(), qC2.getX());
		double y = calculateBezierByControlPoints(time, qFrom.getY(), qTo.getY(), qC1.getY(), qC2.getY());
		double z = calculateBezierByControlPoints(time, qFrom.getZ(), qTo.getZ(), qC1.getZ(), qC2.getZ());
		double w = calculateBezierByControlPoints(time, qFrom.getW(), qTo.getW(), qC1.getW(), qC2.getW());
		return new Rotation(new Quaternion(x, y, z, w));
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

	private RotationSensor calculateBezier(DoubleSensor time, Rotation from, Rotation to, Vector fromvel,
			Vector tovel) {
		Rotation control1 = from
				.multiply(new Rotation(from.invert().apply(fromvel.normalize()), fromvel.getLength() / 3));
		Rotation control2 = to.multiply(new Rotation(to.invert().apply(tovel.normalize()), -tovel.getLength() / 3));

		Quaternion qFrom = from.getQuaternion(), qTo = to.getQuaternion(), qC1 = control1.getQuaternion(),
				qC2 = control2.getQuaternion();
		DoubleSensor x = calculateBezierByControlPoints(time, qFrom.getX(), qTo.getX(), qC1.getX(), qC2.getX());
		DoubleSensor y = calculateBezierByControlPoints(time, qFrom.getY(), qTo.getY(), qC1.getY(), qC2.getY());
		DoubleSensor z = calculateBezierByControlPoints(time, qFrom.getZ(), qTo.getZ(), qC1.getZ(), qC2.getZ());
		DoubleSensor w = calculateBezierByControlPoints(time, qFrom.getW(), qTo.getW(), qC1.getW(), qC2.getW());
		DoubleSensor len = x.multiply(x).add(y.multiply(y)).add(z.multiply(z)).add(w.multiply(w)).sqrt();
		return RotationSensor.fromQuaternion(x.divide(len), y.divide(len), z.divide(len), w.divide(len));
	}

	private DoubleSensor calculateBezier(DoubleSensor t, double from, double to, double fromvel, double tovel) {
		double control1 = from + fromvel / 3;
		double control2 = to - tovel / 3;
		double a = from;
		double b = -3 * from + 3 * control1;
		double c = 3 * from - 6 * control1 + 3 * control2;
		double d = -from + 3 * control1 - 3 * control2 + to;
		return t.multiply(d).add(c).multiply(t).add(b).multiply(t).add(a);
	}

	private DoubleSensor calculateBezierByControlPoints(DoubleSensor t, double from, double to, double control1,
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
	public Double getTimeAtFirstOccurence(State state) {
		return null;
	}

	@Override
	public Frame getBaseFrame() {
		return baseFrame;
	}

	@Override
	public TransformationSensor getTransformationSensorAt(DoubleSensor time) {
		DoubleSensor t = time.add(-starttime).divide(duration);

		DoubleSensor x = calculateBezier(t, startpos.getTranslation().getX(), destpos.getTranslation().getX(),
				startvel.getTransVel().getX() * duration, destvel.getTransVel().getX() * duration);
		DoubleSensor y = calculateBezier(t, startpos.getTranslation().getY(), destpos.getTranslation().getY(),
				startvel.getTransVel().getY() * duration, destvel.getTransVel().getY() * duration);
		DoubleSensor z = calculateBezier(t, startpos.getTranslation().getZ(), destpos.getTranslation().getZ(),
				startvel.getTransVel().getZ() * duration, destvel.getTransVel().getZ() * duration);
		VectorSensor trans = VectorSensor.fromComponents(x, y, z);

		RotationSensor rot = calculateBezier(t, startpos.getRotation(), destpos.getRotation(),
				startvel.getRotVel().scale(duration), destvel.getRotVel().scale(duration));
		return TransformationSensor.fromComponents(trans, rot);
	}

	@Override
	public BooleanSensor getStateSensorAt(DoubleSensor time, State state) {
		return BooleanSensor.fromValue(false);
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
