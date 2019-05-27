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
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.primitives.FrameFromPosRot;
import org.roboticsapi.runtime.world.primitives.RotationFromABC;
import org.roboticsapi.runtime.world.primitives.VectorFromXYZ;

public class MergeTransformationFragment extends NetFragment {

	private final DataflowOutPort outTransformation;

	public MergeTransformationFragment(DataflowOutPort x, DataflowOutPort y, DataflowOutPort z, DataflowOutPort a,
			DataflowOutPort b, DataflowOutPort c) throws MappingException {
		super("Merge Transformation");

		VectorFromXYZ vector = add(new VectorFromXYZ());

		DataflowInPort xIn = addInPort(new DataflowInPort(true, new DoubleDataflow(), vector.getInX()));
		connect(x, xIn);
		DataflowInPort yIn = addInPort(new DataflowInPort(true, new DoubleDataflow(), vector.getInY()));
		connect(y, yIn);
		DataflowInPort zIn = addInPort(new DataflowInPort(true, new DoubleDataflow(), vector.getInZ()));
		connect(z, zIn);

		RotationFromABC rotation = add(new RotationFromABC());
		DataflowInPort aIn = addInPort(new DataflowInPort(true, new DoubleDataflow(), rotation.getInA()));
		connect(a, aIn);
		DataflowInPort bIn = addInPort(new DataflowInPort(true, new DoubleDataflow(), rotation.getInB()));
		connect(b, bIn);
		DataflowInPort cIn = addInPort(new DataflowInPort(true, new DoubleDataflow(), rotation.getInC()));
		connect(c, cIn);

		FrameFromPosRot frame = add(new FrameFromPosRot());

		try {
			frame.getInPos().connectTo(vector.getOutValue());
			frame.getInRot().connectTo(rotation.getOutValue());
		} catch (RPIException e) {
			throw new MappingException(e);
		}

		outTransformation = addOutPort(new TransformationDataflow(), true, frame.getOutValue());

	}

	public DataflowOutPort getOutTransformation() {
		return outTransformation;
	}
}
