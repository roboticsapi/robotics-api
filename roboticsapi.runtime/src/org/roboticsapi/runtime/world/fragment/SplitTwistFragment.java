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
import org.roboticsapi.runtime.world.dataflow.VectorDataflow;
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.primitives.TwistToVelocities;
import org.roboticsapi.runtime.world.primitives.VectorToXYZ;

public class SplitTwistFragment extends NetFragment {

	private final DataflowOutPort outTransVel;
	private final DataflowOutPort outRotVel;
	private final DataflowOutPort outX;
	private final DataflowOutPort outY;
	private final DataflowOutPort outZ;
	private final DataflowOutPort outRx;
	private final DataflowOutPort outRy;
	private final DataflowOutPort outRz;

	public SplitTwistFragment(DataflowOutPort twistPort) throws MappingException {
		super("Split Twist");
		if (!(twistPort.getType() instanceof VelocityDataflow)) {
			throw new MappingException("Port must be of type VelocityDataflow");
		}

		TwistToVelocities toVels = add(new TwistToVelocities());
		DataflowInPort inTwist = addInPort(twistPort.getType(), true, toVels.getInValue());
		connect(twistPort, inTwist);

		VectorToXYZ toXYZ = add(new VectorToXYZ());
		VectorToXYZ toRxyz = add(new VectorToXYZ());
		try {
			toXYZ.getInValue().connectTo(toVels.getOutTransVel());
			toRxyz.getInValue().connectTo(toVels.getOutRotVel());
		} catch (RPIException e) {
			throw new MappingException(e);
		}

		outTransVel = addOutPort(new VectorDataflow(), false, toVels.getOutTransVel());
		outRotVel = addOutPort(new RotationDataflow(), false, toVels.getOutRotVel());
		outX = addOutPort(new DoubleDataflow(), false, toXYZ.getOutX());
		outY = addOutPort(new DoubleDataflow(), false, toXYZ.getOutY());
		outZ = addOutPort(new DoubleDataflow(), false, toXYZ.getOutZ());
		outRx = addOutPort(new DoubleDataflow(), false, toRxyz.getOutX());
		outRy = addOutPort(new DoubleDataflow(), false, toRxyz.getOutY());
		outRz = addOutPort(new DoubleDataflow(), false, toRxyz.getOutZ());
	}

	public DataflowOutPort getOutTransVel() {
		return outTransVel;
	}

	public DataflowOutPort getOutRotVel() {
		return outRotVel;
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

	public DataflowOutPort getOutRx() {
		return outRx;
	}

	public DataflowOutPort getOutRy() {
		return outRy;
	}

	public DataflowOutPort getOutRz() {
		return outRz;
	}

}
