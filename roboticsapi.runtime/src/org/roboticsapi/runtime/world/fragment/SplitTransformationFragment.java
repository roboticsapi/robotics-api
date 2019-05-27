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
import org.roboticsapi.runtime.world.dataflow.RotationDataflow;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.dataflow.VectorDataflow;
import org.roboticsapi.runtime.world.primitives.FrameToPosRot;
import org.roboticsapi.runtime.world.primitives.RotationToABC;
import org.roboticsapi.runtime.world.primitives.VectorToXYZ;

public class SplitTransformationFragment extends NetFragment {

	private final DataflowOutPort outPosition;
	private final DataflowOutPort outRotation;
	private final DataflowOutPort outX;
	private final DataflowOutPort outY;
	private final DataflowOutPort outZ;
	private final DataflowOutPort outA;
	private final DataflowOutPort outB;
	private final DataflowOutPort outC;

	public SplitTransformationFragment(DataflowOutPort transformationPort) throws MappingException {
		super("Split Transformation");
		if (!(transformationPort.getType() instanceof TransformationDataflow)) {
			throw new MappingException("Port must be of type TransformationDataflow");
		}

		FrameToPosRot toPosRot = add(new FrameToPosRot());
		DataflowInPort inPosRot = addInPort(new TransformationDataflow(), true, toPosRot.getInValue());
		connect(transformationPort, inPosRot);

		VectorToXYZ toXYZ = add(new VectorToXYZ());
		RotationToABC toABC = add(new RotationToABC());
		try {
			toXYZ.getInValue().connectTo(toPosRot.getOutPosition());
			toABC.getInValue().connectTo(toPosRot.getOutRotation());
		} catch (RPIException e) {
			throw new MappingException(e);
		}

		outPosition = addOutPort(new VectorDataflow(), false, toPosRot.getOutPosition());
		outRotation = addOutPort(new RotationDataflow(), false, toPosRot.getOutRotation());
		outX = addOutPort(new DoubleDataflow(), false, toXYZ.getOutX());
		outY = addOutPort(new DoubleDataflow(), false, toXYZ.getOutY());
		outZ = addOutPort(new DoubleDataflow(), false, toXYZ.getOutZ());
		outA = addOutPort(new DoubleDataflow(), false, toABC.getOutA());
		outB = addOutPort(new DoubleDataflow(), false, toABC.getOutB());
		outC = addOutPort(new DoubleDataflow(), false, toABC.getOutC());
	}

	public DataflowOutPort getOutPosition() {
		return outPosition;
	}

	public DataflowOutPort getOutRotation() {
		return outRotation;
	}

	public DataflowOutPort getOutX() {
		return outX;
	}

	public DataflowOutPort getOutY() {
		return outY;
	}

	public DataflowOutPort getOutZ() {
		return outZ;
	}

	public DataflowOutPort getOutA() {
		return outA;
	}

	public DataflowOutPort getOutB() {
		return outB;
	}

	public DataflowOutPort getOutC() {
		return outC;
	}

}
