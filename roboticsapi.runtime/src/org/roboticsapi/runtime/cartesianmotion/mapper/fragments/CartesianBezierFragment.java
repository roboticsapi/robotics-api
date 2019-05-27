/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper.fragments;

import org.roboticsapi.runtime.core.primitives.DoubleBezier;
import org.roboticsapi.runtime.core.primitives.Interval;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameFromPosRot;
import org.roboticsapi.runtime.world.primitives.RotationBezier;
import org.roboticsapi.runtime.world.primitives.RotationFromABC;
import org.roboticsapi.runtime.world.primitives.VectorFromXYZ;
import org.roboticsapi.world.Quaternion;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;

public class CartesianBezierFragment extends NetFragment implements CartesianMotionFragment {
	private final DataflowOutPort port;
	private final Transformation startpos;
	private final Transformation destpos;
	private final Twist startvel;
	private final Twist destvel;
	private final double starttime;
	private final double duration;

	public CartesianBezierFragment(Transformation startpos, Twist startvel, Transformation destpos, Twist destvel,
			double starttime, double duration, DataflowOutPort time) throws MappingException, RPIException {
		super("Bezier");
		this.startpos = startpos;
		this.destpos = destpos;
		this.startvel = startvel;
		this.destvel = destvel;
		this.starttime = starttime;
		this.duration = duration;

		// calculate time (normalized)
		Interval t = add(new Interval(starttime, starttime + duration));
		connect(time, addInPort(new DoubleDataflow(), true, t.getInValue()));

		// calculate position for each axis
		DoubleBezier bezierX = add(new DoubleBezier(startpos.getTranslation().getX(), destpos.getTranslation().getX(),
				startvel.getTransVel().getX() * duration, destvel.getTransVel().getX() * duration));
		bezierX.getInValue().connectTo(t.getOutValue());
		DoubleBezier bezierY = add(new DoubleBezier(startpos.getTranslation().getY(), destpos.getTranslation().getY(),
				startvel.getTransVel().getY() * duration, destvel.getTransVel().getY() * duration));
		bezierY.getInValue().connectTo(t.getOutValue());
		DoubleBezier bezierZ = add(new DoubleBezier(startpos.getTranslation().getZ(), destpos.getTranslation().getZ(),
				startvel.getTransVel().getZ() * duration, destvel.getTransVel().getZ() * duration));
		bezierZ.getInValue().connectTo(t.getOutValue());
		VectorFromXYZ bezierPos = add(new VectorFromXYZ());
		bezierPos.getInX().connectTo(bezierX.getOutValue());
		bezierPos.getInY().connectTo(bezierY.getOutValue());
		bezierPos.getInZ().connectTo(bezierZ.getOutValue());

		RotationFromABC startRot = add(new RotationFromABC(startpos.getRotation().getA(), startpos.getRotation().getB(),
				startpos.getRotation().getC()));
		RotationFromABC destRot = add(new RotationFromABC(destpos.getRotation().getA(), destpos.getRotation().getB(),
				destpos.getRotation().getC()));
		VectorFromXYZ startVel = add(new VectorFromXYZ(startvel.getRotVel().getX() * duration,
				startvel.getRotVel().getY() * duration, startvel.getRotVel().getZ() * duration));
		VectorFromXYZ destVel = add(new VectorFromXYZ(destvel.getRotVel().getX() * duration,
				destvel.getRotVel().getY() * duration, destvel.getRotVel().getZ() * duration));
		RotationBezier bezierRot = add(new RotationBezier());
		bezierRot.getInValue().connectTo(t.getOutValue());
		bezierRot.getInFrom().connectTo(startRot.getOutValue());
		bezierRot.getInFromVel().connectTo(startVel.getOutValue());
		bezierRot.getInTo().connectTo(destRot.getOutValue());
		bezierRot.getInToVel().connectTo(destVel.getOutValue());

		FrameFromPosRot result = add(new FrameFromPosRot());
		result.getInPos().connectTo(bezierPos.getOutValue());
		result.getInRot().connectTo(bezierRot.getOutValue());

		port = addOutPort(new TransformationDataflow(), false, result.getOutValue());
	}

	@Override
	public DataflowOutPort getResultPort() {
		return port;
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
}
