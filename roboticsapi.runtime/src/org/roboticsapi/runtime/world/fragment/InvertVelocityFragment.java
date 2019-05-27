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
import org.roboticsapi.runtime.world.primitives.VectorScale;

public class InvertVelocityFragment extends NetFragment {

	private final DataflowOutPort invertedVelocityPort;

	public InvertVelocityFragment(String name, DataflowOutPort velocityPort) throws MappingException {
		super(name);

		try {

			TwistToVelocities twist = add(new TwistToVelocities());

			connect(velocityPort, addInPort(new VelocityDataflow(null, null, null, null), true, twist.getInValue()));

			VectorScale scaleTrans = add(new VectorScale(-1d));

			scaleTrans.getInValue().connectTo(twist.getOutTransVel());

			VectorScale scaleRot = add(new VectorScale(-1d));

			scaleRot.getInValue().connectTo(twist.getOutRotVel());

			TwistFromVelocities resultTwist = add(new TwistFromVelocities());

			resultTwist.getInTransVel().connectTo(scaleTrans.getOutValue());
			resultTwist.getInRotVel().connectTo(scaleRot.getOutValue());

			VelocityDataflow oldFlow = (VelocityDataflow) velocityPort.getType();

			invertedVelocityPort = addOutPort(new VelocityDataflow(oldFlow.getReferenceFrame(),
					oldFlow.getMovingFrame(), oldFlow.getPivotPoint(), oldFlow.getOrientation()), true,
					resultTwist.getOutValue());

		} catch (RPIException e) {
			throw new MappingException(e);
		}

	}

	public DataflowOutPort getInvertedVelocityPort() {
		return invertedVelocityPort;
	}

}
