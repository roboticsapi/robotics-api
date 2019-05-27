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
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.primitives.TwistFromVelocities;
import org.roboticsapi.runtime.world.primitives.TwistToVelocities;
import org.roboticsapi.runtime.world.primitives.VectorAdd;

public class AddVelocitiesFragment extends NetFragment {

	private final DataflowOutPort addedVelocities;

	public AddVelocitiesFragment(String name, DataflowOutPort velocity1, DataflowOutPort velocity2)
			throws MappingException {
		super("Add velocities" + name != null ? "<" + name + ">" : "");

		VelocityDataflow flow1 = (VelocityDataflow) velocity1.getType();
		VelocityDataflow flow2 = (VelocityDataflow) velocity2.getType();

		if (flow1.getPivotPoint() != null && flow2.getPivotPoint() != null
				&& !flow1.getPivotPoint().isEqualPoint(flow2.getPivotPoint())) {
			throw new MappingException("Pivot point of both dataflows must match");
		}

		if (flow1.getOrientation() != null && flow2.getOrientation() != null
				&& !flow1.getOrientation().isEqualOrientation(flow2.getOrientation())) {
			throw new MappingException("Orientation of both dataflows must match");
		}

		if (flow1.getMovingFrame() != null && flow2.getReferenceFrame() != null
				&& !(flow1.getMovingFrame().equals(flow2.getReferenceFrame()))) {
			throw new MappingException(
					"First dataflow's moving frame must be equal to second dataflow's reference frame");
		}

		try {
			TwistToVelocities firstSplitter = add(new TwistToVelocities());
			TwistToVelocities secondSplitter = add(new TwistToVelocities());

			connect(velocity1,
					addInPort(new VelocityDataflow(null, null, null, null), true, firstSplitter.getInValue()));

			connect(velocity2,
					addInPort(new VelocityDataflow(null, null, null, null), true, secondSplitter.getInValue()));

			VectorAdd transAdd = add(new VectorAdd());

			transAdd.getInFirst().connectTo(firstSplitter.getOutTransVel());
			transAdd.getInSecond().connectTo(secondSplitter.getOutTransVel());

			VectorAdd rotAdd = add(new VectorAdd());

			rotAdd.getInFirst().connectTo(firstSplitter.getOutRotVel());
			rotAdd.getInSecond().connectTo(secondSplitter.getOutRotVel());

			TwistFromVelocities resultTwist = add(new TwistFromVelocities());

			resultTwist.getInTransVel().connectTo(transAdd.getOutValue());
			resultTwist.getInRotVel().connectTo(rotAdd.getOutValue());

			VelocityDataflow resultFlow = new VelocityDataflow(flow2.getMovingFrame(), flow1.getReferenceFrame(),
					flow1.getPivotPoint(), flow1.getOrientation());

			addedVelocities = addOutPort(resultFlow, true, resultTwist.getOutValue());
		} catch (RPIException e) {
			throw new MappingException(e);
		}
	}

	public DataflowOutPort getAddedVelocitiesPort() {
		return addedVelocities;
	}

}
