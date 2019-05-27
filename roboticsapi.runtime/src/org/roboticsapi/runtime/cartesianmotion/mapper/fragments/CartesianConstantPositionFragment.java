/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.cartesianmotion.mapper.fragments;

import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameFromPosRot;
import org.roboticsapi.runtime.world.primitives.RotationFromABC;
import org.roboticsapi.runtime.world.primitives.VectorFromXYZ;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;

public class CartesianConstantPositionFragment extends NetFragment implements CartesianMotionFragment {
	private final DataflowOutPort port;
	private final Transformation startpos;

	public CartesianConstantPositionFragment(Transformation startpos) throws MappingException, RPIException {
		super("Constant Position");
		this.startpos = startpos;

		// start position
		VectorFromXYZ pos = add(new VectorFromXYZ(startpos.getTranslation().getX(), startpos.getTranslation().getY(),
				startpos.getTranslation().getZ()));
		RotationFromABC rot = add(new RotationFromABC(startpos.getRotation().getA(), startpos.getRotation().getB(),
				startpos.getRotation().getC()));

		// add & combine
		FrameFromPosRot result = add(new FrameFromPosRot());
		result.getInPos().connectTo(pos.getOutValue());
		result.getInRot().connectTo(rot.getOutValue());

		port = addOutPort(new TransformationDataflow(), false, result.getOutValue());
	}

	@Override
	public DataflowOutPort getResultPort() {
		return port;
	}

	@Override
	public Transformation getTransformationAt(double time) {
		return startpos;
	}

	@Override
	public Twist getTwistAt(double time) {
		return new Twist(0, 0, 0, 0, 0, 0);
	}
}
