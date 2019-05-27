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

public class CartesianConstantAccelerationFragment extends NetFragment implements CartesianMotionFragment {
	private final DataflowOutPort port;
	private final Transformation startpos;
	private final Twist startvel;
	private final Twist acc;
	private final double starttime;

	public CartesianConstantAccelerationFragment(Transformation startpos, Twist startvel, Twist acc, double starttime,
			DataflowOutPort time) throws MappingException, RPIException {
		super("Constant Velocity");
		this.startpos = startpos;
		this.startvel = startvel;
		this.acc = acc;
		this.starttime = starttime;

		// calculate time
		DoubleAdd t = add(new DoubleAdd());
		t.setFirst(-starttime);
		connect(time, addInPort(new DoubleDataflow(), true, t.getInSecond()));

		DoubleMultiply tSquare = add(new DoubleMultiply());
		tSquare.getInFirst().connectTo(t.getOutValue());
		tSquare.getInSecond().connectTo(t.getOutValue());

		// start position
		VectorFromXYZ pos = add(new VectorFromXYZ(startpos.getTranslation().getX(), startpos.getTranslation().getY(),
				startpos.getTranslation().getZ()));
		RotationFromABC rot = add(new RotationFromABC(startpos.getRotation().getA(), startpos.getRotation().getB(),
				startpos.getRotation().getC()));

		// translational velocity
		VectorFromXYZ transvel = add(new VectorFromXYZ(startvel.getTransVel().getX(), startvel.getTransVel().getY(),
				startvel.getTransVel().getZ()));
		VectorScale posVel = add(new VectorScale());
		posVel.getInValue().connectTo(transvel.getOutValue());
		posVel.getInFactor().connectTo(t.getOutValue());

		// rotational velocity (expressed in startpos frame)
		Vector rotAxis = startvel.getRotVel().normalize();
		RotationFromAxisAngle rotVel = add(new RotationFromAxisAngle());
		rotVel.setX(rotAxis.getX());
		rotVel.setY(rotAxis.getY());
		rotVel.setZ(rotAxis.getZ());
		DoubleMultiply rotAngle = add(new DoubleMultiply());
		rotAngle.getInFirst().connectTo(t.getOutValue());
		rotAngle.setSecond(startvel.getRotVel().getLength());
		rotVel.getInAngle().connectTo(rotAngle.getOutValue());

		// translational acceleration
		VectorFromXYZ transacc = add(new VectorFromXYZ(acc.getTransVel().getX() / 2, acc.getTransVel().getY() / 2,
				acc.getTransVel().getZ() / 2));
		VectorScale posAcc = add(new VectorScale());
		posAcc.getInValue().connectTo(transacc.getOutValue());
		posAcc.getInFactor().connectTo(tSquare.getOutValue());

		// rotational acceleration (expressed in startpos frame)
		Vector accAxis = acc.getRotVel().normalize();
		RotationFromAxisAngle rotAcc = add(new RotationFromAxisAngle());
		rotAcc.setX(accAxis.getX());
		rotAcc.setY(accAxis.getY());
		rotAcc.setZ(accAxis.getZ());
		DoubleMultiply accAngle = add(new DoubleMultiply());
		accAngle.getInFirst().connectTo(tSquare.getOutValue());
		accAngle.setSecond(acc.getRotVel().getLength() / 2);
		rotAcc.getInAngle().connectTo(accAngle.getOutValue());

		// add & combine
		VectorAdd consVelPos = add(new VectorAdd());
		consVelPos.getInFirst().connectTo(pos.getOutValue());
		consVelPos.getInSecond().connectTo(posVel.getOutValue());

		VectorAdd resultPos = add(new VectorAdd());
		resultPos.getInFirst().connectTo(consVelPos.getOutValue());
		resultPos.getInSecond().connectTo(posAcc.getOutValue());

		RotationTransform consVelRot = add(new RotationTransform());
		consVelRot.getInFirst().connectTo(rotVel.getOutValue());
		consVelRot.getInSecond().connectTo(rot.getOutValue());

		RotationTransform resultRot = add(new RotationTransform());
		resultRot.getInFirst().connectTo(rotAcc.getOutValue());
		resultRot.getInSecond().connectTo(consVelRot.getOutValue());

		FrameFromPosRot result = add(new FrameFromPosRot());
		result.getInPos().connectTo(resultPos.getOutValue());
		result.getInRot().connectTo(resultRot.getOutValue());

		port = addOutPort(new TransformationDataflow(), false, result.getOutValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.roboticsapi.runtime.softrobot.robot.mapper.fragments.FrameMotionFragment
	 * #getResultPort()
	 */
	@Override
	public DataflowOutPort getResultPort() {
		return port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.roboticsapi.runtime.softrobot.robot.mapper.fragments.FrameMotionFragment
	 * #getPositionAt(double)
	 */
	@Override
	public Transformation getTransformationAt(double time) {
		double t = time - starttime;
		if (t < 0) {
			return startpos;
		}

		Rotation rot = new Rotation(acc.getRotVel().normalize(), acc.getRotVel().getLength() * t * t / 2)
				.multiply(new Rotation(startvel.getRotVel().normalize(), startvel.getRotVel().getLength() * t))
				.multiply(startpos.getRotation());
		Vector trans = acc.getTransVel().scale(t * t / 2)
				.add(startvel.getTransVel().scale(t).add(startpos.getTranslation()));
		return new Transformation(rot, trans);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.roboticsapi.runtime.softrobot.robot.mapper.fragments.FrameMotionFragment
	 * #getTwistAt(double)
	 */
	@Override
	public Twist getTwistAt(double time) {
		double t = time - starttime;
		if (t < 0) {
			return startvel;
		}

		return new Twist(startvel.getTransVel().add(acc.getTransVel().scale(t)),
				startvel.getRotVel().add(acc.getRotVel().scale(t)));
	}

}
