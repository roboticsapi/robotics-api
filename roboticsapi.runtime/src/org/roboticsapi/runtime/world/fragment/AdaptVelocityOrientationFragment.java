/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.fragment;

import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.primitives.FrameToPosRot;
import org.roboticsapi.runtime.world.primitives.TwistFromVelocities;
import org.roboticsapi.runtime.world.primitives.TwistToVelocities;
import org.roboticsapi.runtime.world.primitives.VectorRotate;
import org.roboticsapi.world.Orientation;

public class AdaptVelocityOrientationFragment extends NetFragment {

	private final DataflowOutPort orientationAdaptedVelocityPort;

	public AdaptVelocityOrientationFragment(String name, DataflowOutPort velocityPort,
			DataflowOutPort relativePoseOldToNewPort, Orientation newOrientation) throws MappingException {
		super(name);

		try {

			TwistToVelocities twist = add(new TwistToVelocities());

			connect(velocityPort, addInPort(new VelocityDataflow(null, null, null, null), true, twist.getInValue()));

			FrameToPosRot rotSplitter = add(new FrameToPosRot());

			connect(relativePoseOldToNewPort,
					addInPort(new RelationDataflow(null, null), true, rotSplitter.getInValue()));

			VectorRotate rotateRot = add(new VectorRotate());
			VectorRotate rotateTrans = add(new VectorRotate());

			rotateRot.getInRot().connectTo(rotSplitter.getOutRotation());
			rotateTrans.getInRot().connectTo(rotSplitter.getOutRotation());

			rotateRot.getInValue().connectTo(twist.getOutRotVel());
			rotateTrans.getInValue().connectTo(twist.getOutTransVel());

			TwistFromVelocities twistCombiner = add(new TwistFromVelocities());

			twistCombiner.getInTransVel().connectTo(rotateTrans.getOutValue());
			twistCombiner.getInRotVel().connectTo(rotateRot.getOutValue());

			VelocityDataflow oldDataflow = (VelocityDataflow) velocityPort.getType();

			orientationAdaptedVelocityPort = addOutPort(new VelocityDataflow(oldDataflow.getMovingFrame(),
					oldDataflow.getReferenceFrame(), oldDataflow.getPivotPoint(), newOrientation), true,
					twistCombiner.getOutValue());

		} catch (RPIException e) {
			throw new MappingException(e);
		}
	}

	public DataflowOutPort getOrientationAdaptedVelocityPort() {
		return orientationAdaptedVelocityPort;
	}

}
