/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper.fragments;

import org.roboticsapi.runtime.core.primitives.DoubleAdd;
import org.roboticsapi.runtime.core.primitives.DoubleMultiply;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameFromPosRot;
import org.roboticsapi.runtime.world.primitives.RotationFromABC;
import org.roboticsapi.runtime.world.primitives.RotationFromAxisAngle;
import org.roboticsapi.runtime.world.primitives.RotationTransform;
import org.roboticsapi.runtime.world.primitives.VectorAdd;
import org.roboticsapi.runtime.world.primitives.VectorFromXYZ;
import org.roboticsapi.runtime.world.primitives.VectorScale;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;

public class CartesianConstantVelocityFragment extends NetFragment implements CartesianMotionFragment {
	private final DataflowOutPort port;
	private final Transformation startpos;
	private final Twist vel;
	private final double starttime;

	public CartesianConstantVelocityFragment(Transformation startpos, Twist vel, double starttime, DataflowOutPort time)
			throws MappingException, RPIException {
		super("Constant Velocity");
		this.startpos = startpos;
		this.vel = vel;
		this.starttime = starttime;

		// calculate time
		DoubleAdd t = add(new DoubleAdd());
		t.setFirst(-starttime);
		connect(time, addInPort(new DoubleDataflow(), true, t.getInSecond()));

		// start position
		VectorFromXYZ pos = add(new VectorFromXYZ(startpos.getTranslation().getX(), startpos.getTranslation().getY(),
				startpos.getTranslation().getZ()));
		RotationFromABC rot = add(new RotationFromABC(startpos.getRotation().getA(), startpos.getRotation().getB(),
				startpos.getRotation().getC()));

		// translational velocity
		VectorFromXYZ transvel = add(
				new VectorFromXYZ(vel.getTransVel().getX(), vel.getTransVel().getY(), vel.getTransVel().getZ()));
		VectorScale posAdd = add(new VectorScale());
		posAdd.getInValue().connectTo(transvel.getOutValue());
		posAdd.getInFactor().connectTo(t.getOutValue());

		// rotational velocity (expressed in startpos frame)
		Vector rotAxis = vel.getRotVel().normalize();
		RotationFromAxisAngle rotAdd = add(new RotationFromAxisAngle());
		rotAdd.setX(rotAxis.getX());
		rotAdd.setY(rotAxis.getY());
		rotAdd.setZ(rotAxis.getZ());
		DoubleMultiply rotAngle = add(new DoubleMultiply());
		rotAngle.getInFirst().connectTo(t.getOutValue());
		rotAngle.setSecond(vel.getRotVel().getLength());
		rotAdd.getInAngle().connectTo(rotAngle.getOutValue());

		// add & combine
		VectorAdd resultPos = add(new VectorAdd());
		resultPos.getInFirst().connectTo(pos.getOutValue());
		resultPos.getInSecond().connectTo(posAdd.getOutValue());

		RotationTransform resultRot = add(new RotationTransform());
		resultRot.getInFirst().connectTo(rotAdd.getOutValue());
		resultRot.getInSecond().connectTo(rot.getOutValue());

		FrameFromPosRot result = add(new FrameFromPosRot());
		result.getInPos().connectTo(resultPos.getOutValue());
		result.getInRot().connectTo(resultRot.getOutValue());

		port = addOutPort(new TransformationDataflow(), false, result.getOutValue());
	}

	@Override
	public DataflowOutPort getResultPort() {
		return port;
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
}
