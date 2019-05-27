/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.mapper;

import java.util.ArrayList;
import java.util.List;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.multijoint.action.HoldJointPosition;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.ComposedDataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BaseActionMapperResult;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.rpi.RPIException;

public class HoldJointPositionMapper implements ActionMapper<SoftRobotRuntime, HoldJointPosition> {

	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, HoldJointPosition action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {
		NetFragment fragment = new NetFragment("HoldJointPosition");

		SensorMappingContext context = new SensorMappingContext();
		List<DataflowOutPort> axisTargets = new ArrayList<DataflowOutPort>();
		for (DoubleSensor sensor : action.getSensors()) {

			SensorMapperResult<Double> mappedSensor = runtime.getMapperRegistry().mapSensor(runtime, sensor, fragment,
					context);

			axisTargets.add(mappedSensor.getSensorPort());
		}

		ComposedDataflowOutPort outComposed = fragment
				.addOutPort(new ComposedDataflowOutPort(true, DataflowOutPort.toArray(axisTargets)));

		DataflowOutPort outJoints = fragment.reinterpret(outComposed, new JointsDataflow(axisTargets.size(), null));

		return new BaseActionMapperResult(action, fragment, new JointPositionActionResult(outJoints), ports.cancelPort);
	}
}
