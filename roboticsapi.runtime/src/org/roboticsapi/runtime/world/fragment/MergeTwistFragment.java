/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.fragment;

import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.primitives.TwistFromVelocities;
import org.roboticsapi.runtime.world.primitives.VectorFromXYZ;

public class MergeTwistFragment extends NetFragment {

	private final DataflowOutPort outVelocity;

	public MergeTwistFragment(DataflowOutPort x, DataflowOutPort y, DataflowOutPort z, DataflowOutPort rx,
			DataflowOutPort ry, DataflowOutPort rz, DataflowType velocityType) throws MappingException {
		super("Merge Twist");

		VectorFromXYZ vector = add(new VectorFromXYZ());

		DataflowInPort xIn = addInPort(new DataflowInPort(true, new DoubleDataflow(), vector.getInX()));
		connect(x, xIn);
		DataflowInPort yIn = addInPort(new DataflowInPort(true, new DoubleDataflow(), vector.getInY()));
		connect(y, yIn);
		DataflowInPort zIn = addInPort(new DataflowInPort(true, new DoubleDataflow(), vector.getInZ()));
		connect(z, zIn);

		VectorFromXYZ rot = add(new VectorFromXYZ());

		DataflowInPort rxIn = addInPort(new DataflowInPort(true, new DoubleDataflow(), rot.getInX()));
		connect(rx, rxIn);
		DataflowInPort ryIn = addInPort(new DataflowInPort(true, new DoubleDataflow(), rot.getInY()));
		connect(ry, ryIn);
		DataflowInPort rzIn = addInPort(new DataflowInPort(true, new DoubleDataflow(), rot.getInZ()));
		connect(rz, rzIn);

		TwistFromVelocities twist = add(new TwistFromVelocities());

		try {
			twist.getInTransVel().connectTo(vector.getOutValue());
			twist.getInRotVel().connectTo(rot.getOutValue());
		} catch (RPIException e) {
			throw new MappingException(e);
		}

		outVelocity = addOutPort(velocityType, true, twist.getOutValue());

	}

	public DataflowOutPort getOutVelocity() {
		return outVelocity;
	}
}
