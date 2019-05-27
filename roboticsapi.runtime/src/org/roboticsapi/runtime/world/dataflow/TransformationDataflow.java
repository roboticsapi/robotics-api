/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.dataflow;

import java.util.List;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.UnimplementedMappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.world.sensor.TransformationSensor;

public class TransformationDataflow extends DataflowType {

	public TransformationDataflow() {
		super(1);
	}

	public static TransformationSensor createTransformationSensor(DataflowOutPort port, RoboticsRuntime runtime)
			throws RPIException {
		if (!(port.getType() instanceof TransformationDataflow)) {
			throw new RPIException("Can only create TransformationSensor on ports of type TransformationDataflow");
		}
		return new TransformationDataflowSensor(port, runtime);
	}

	@Override
	public ValueReader[] createValueReaders(String uniqueKeyPrefix, List<OutPort> valuePorts, NetFragment fragment)
			throws RPIException, MappingException {
		// TODO implement createValueReaders
		throw new UnimplementedMappingException();
	}
}