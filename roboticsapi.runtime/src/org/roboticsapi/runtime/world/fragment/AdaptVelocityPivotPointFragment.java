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
import org.roboticsapi.runtime.world.primitives.VectorAdd;
import org.roboticsapi.runtime.world.primitives.VectorCross;
import org.roboticsapi.runtime.world.primitives.VectorRotate;
import org.roboticsapi.world.Point;

public class AdaptVelocityPivotPointFragment extends NetFragment {

	private final DataflowOutPort pivotAdaptedVelocityPort;

	public AdaptVelocityPivotPointFragment(String name, DataflowOutPort velocityPort,
			DataflowOutPort relativePoseOldToNewPort, DataflowOutPort transformationOldPivotToOrientation,
			Point newPivotPoint) throws MappingException {
		super(name);

		try {

			TwistToVelocities twist = add(new TwistToVelocities());

			connect(velocityPort, addInPort(new VelocityDataflow(null, null, null, null), true, twist.getInValue()));

			VectorCross cross = add(new VectorCross());

			cross.getInFirst().connectTo(twist.getOutRotVel());

			FrameToPosRot transDiffSplitter = add(new FrameToPosRot());
			FrameToPosRot rotSplitter = add(new FrameToPosRot());
			VectorRotate rotate = add(new VectorRotate());

			rotate.getInRot().connectTo(rotSplitter.getOutRotation());
			rotate.getInValue().connectTo(transDiffSplitter.getOutPosition());

			cross.getInSecond().connectTo(rotate.getOutValue());

			connect(relativePoseOldToNewPort,
					addInPort(new RelationDataflow(null, null), true, transDiffSplitter.getInValue()));

			connect(transformationOldPivotToOrientation,
					addInPort(new RelationDataflow(null, null), true, rotSplitter.getInValue()));

			VectorAdd vectorAdd = add(new VectorAdd());

			vectorAdd.getInFirst().connectTo(twist.getOutTransVel());
			vectorAdd.getInSecond().connectTo(cross.getOutValue());

			TwistFromVelocities twistCombiner = add(new TwistFromVelocities());

			twistCombiner.getInTransVel().connectTo(vectorAdd.getOutValue());
			twistCombiner.getInRotVel().connectTo(twist.getOutRotVel());

			VelocityDataflow flow = (VelocityDataflow) velocityPort.getType();

			pivotAdaptedVelocityPort = addOutPort(new VelocityDataflow(flow.getMovingFrame(), flow.getReferenceFrame(),
					newPivotPoint, flow.getOrientation()), true, twistCombiner.getOutValue());

		} catch (RPIException e) {
			throw new MappingException(e);
		}
	}

	public DataflowOutPort getPivotAdaptedVelocityPort() {
		return pivotAdaptedVelocityPort;
	}

}
