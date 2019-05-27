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
import org.roboticsapi.multijoint.action.JointSuperposition;
import org.roboticsapi.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.DoubleAdd;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowInPort;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActionMapper;
import org.roboticsapi.runtime.mapping.parts.ActionMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActionMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.WrappedActionMapperResult;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

@SuppressWarnings("rawtypes")
public class JointSuperpositionMapper implements ActionMapper<SoftRobotRuntime, JointSuperposition> {
	@Override
	public ActionMapperResult map(SoftRobotRuntime runtime, JointSuperposition action, DeviceParameterBag parameters,
			ActionMappingContext ports) throws MappingException, RPIException {

		JointDeviceParameters jointDeviceParameters = parameters.get(JointDeviceParameters.class);
		if (jointDeviceParameters == null) {
			throw new MappingException("No JointDeviceParameters given");
		}
		// map inner action
		ActionMapperResult innerActionMapperResult = runtime.getMapperRegistry().mapAction(runtime,
				action.getInnerAction(), parameters, ports.cancelPort, ports.overridePort, ports.actionPlans);
		NetFragment fragment = new NetFragment(
				"JointSuperposition<" + innerActionMapperResult.getNetFragment().getName() + ">");
		fragment.add(innerActionMapperResult.getNetFragment());

		// map all superposing sensors
		SensorMappingContext context = new SensorMappingContext();
		List<InPort> superpositionIns = new ArrayList<InPort>();
		List<OutPort> superpositionOuts = new ArrayList<OutPort>();
		for (int i = 0; i < jointDeviceParameters.getJointCount(); i++) {
			DoubleSensor superposition = action.getSuperposition(i);

			// if no superposition defined for this joint, add zero
			if (superposition == null) {
				// add superposition values to joint dataflow
				DoubleAdd add = fragment.add(new DoubleAdd(0d, 0d));
				superpositionIns.add(add.getInFirst());
				superpositionOuts.add(add.getOutValue());
			}
			// otherwise, add superposition value
			else {
				SensorMapperResult<Double> sensorMapperResult = runtime.getMapperRegistry().mapSensor(runtime,
						superposition, fragment, context);

				// add superposition values to joint dataflow
				DoubleAdd add = fragment.add(new DoubleAdd());
				superpositionIns.add(add.getInFirst());
				DataflowInPort inSecond = fragment.addInPort(new DoubleDataflow(), true, add.getInSecond());
				fragment.connect(sensorMapperResult.getSensorPort(), inSecond);

				superpositionOuts.add(add.getOutValue());
			}
		}

		// get joint dataflow from inner action result
		DataflowOutPort originalOut = innerActionMapperResult.getActionResult().getOutPort();
		JointsDataflow jointsOutDataflow = new JointsDataflow(jointDeviceParameters.getJointCount(), null);
		DataflowOutPort jointsOut = fragment.addConverterLink(originalOut, jointsOutDataflow);

		// create composed in port for all superpositions and connect it to
		// incoming composed joint dataflow
		DataflowInPort superpositionInPort = fragment.addInPort(jointsOutDataflow, true,
				superpositionIns.toArray(new InPort[superpositionIns.size()]));
		fragment.connect(jointsOut, superpositionInPort);

		DataflowOutPort superpositionOut = fragment.addOutPort(jointsOutDataflow, true,
				superpositionOuts.toArray(new OutPort[superpositionOuts.size()]));

		return new WrappedActionMapperResult(action, action.getInnerAction(), fragment,
				new JointPositionActionResult(superpositionOut), innerActionMapperResult);
	}
}
