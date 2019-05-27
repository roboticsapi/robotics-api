/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.driver.mapper;

import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.multijoint.MultiJointDeviceDriver;
import org.roboticsapi.multijoint.parameter.JointDeviceParameters;
import org.roboticsapi.multijoint.parameter.JointParameters;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.primitives.BooleanAnd;
import org.roboticsapi.runtime.core.primitives.BooleanOr;
import org.roboticsapi.runtime.core.primitives.BooleanValue;
import org.roboticsapi.runtime.core.primitives.DoubleConditional;
import org.roboticsapi.runtime.core.primitives.DoubleDivide;
import org.roboticsapi.runtime.core.primitives.DoubleGreater;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.multijoint.JointDataflow;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.multijoint.driver.SoftRobotMultiJointDeviceDriver;
import org.roboticsapi.runtime.multijoint.mapper.AbstractMultiJointDeviceDriverMapperResult;
import org.roboticsapi.runtime.multijoint.mapper.JointPositionActionResult;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotMultiJointDeviceDriverJointPositionMapper<DD extends SoftRobotMultiJointDeviceDriver>
		extends AbstractSoftRobotMultiJointDeviceDriverMapper<DD, JointPositionActionResult> {

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime, DD driver, JointPositionActionResult actionResult,
			DeviceParameterBag parameters, DeviceMappingPorts ports) throws MappingException, RPIException {

		NetFragment fragment = new NetFragment("SoftRobotMultiJointDevice (Position)");

		JointDeviceParameters jointParameters = parameters.get(JointDeviceParameters.class);

		int jointCount = driver.getJointCount();
		ActuatorDriverMapperResult[] jointResult = new ActuatorDriverMapperResult[jointCount];

		SensorMappingContext context = new SensorMappingContext();
		OutPort illegal = fragment.add(new BooleanValue(false)).getOutValue();

		for (int i = 0; i < jointCount; i++) {
			if (jointParameters != null && jointParameters.getJointParameters(i) != null) {

				JointDataflow jointDataflow = new JointDataflow(driver.getJoint(i).getDriver());
				DataflowOutPort dataflow = fragment.addConverterLink(actionResult.getOutPort(), jointDataflow);
				SensorMapperResult<Double> currentPosition = runtime.getMapperRegistry().mapSensor(runtime,
						driver.getJoint(i).getCommandedPositionSensor(), fragment, context);

				JointParameters jointParameter = jointParameters.getJointParameters(i);

				DoubleGreater growing = fragment.add(new DoubleGreater());
				fragment.connect(dataflow, growing.getInFirst(), jointDataflow);
				fragment.connect(currentPosition.getSensorPort(), growing.getInSecond(), new DoubleDataflow());

				DoubleGreater tooBig = fragment.add(new DoubleGreater());
				fragment.connect(dataflow, tooBig.getInFirst(), jointDataflow);
				tooBig.setSecond(jointParameter.getMaximumPosition());

				BooleanAnd upLimit = fragment.add(new BooleanAnd());
				fragment.connect(growing.getOutValue(), upLimit.getInFirst());
				fragment.connect(tooBig.getOutValue(), upLimit.getInSecond());

				DoubleGreater shrinking = fragment.add(new DoubleGreater());
				fragment.connect(currentPosition.getSensorPort(), shrinking.getInFirst(), new DoubleDataflow());
				fragment.connect(dataflow, shrinking.getInSecond(), jointDataflow);

				DoubleGreater tooSmall = fragment.add(new DoubleGreater());
				tooSmall.setFirst(jointParameter.getMinimumPosition());
				fragment.connect(dataflow, tooSmall.getInSecond(), jointDataflow);

				BooleanAnd lowLimit = fragment.add(new BooleanAnd());
				fragment.connect(shrinking.getOutValue(), lowLimit.getInFirst());
				fragment.connect(tooSmall.getOutValue(), lowLimit.getInSecond());

				BooleanOr limit = fragment.add(new BooleanOr());
				fragment.connect(upLimit.getOutValue(), limit.getInFirst());
				fragment.connect(lowLimit.getOutValue(), limit.getInSecond());

				BooleanOr orLimit = fragment.add(new BooleanOr());
				fragment.connect(illegal, orLimit.getInFirst());
				fragment.connect(limit.getOutValue(), orLimit.getInSecond());
				illegal = orLimit.getOutValue();
			}
		}

		DoubleDivide nanValue = fragment.add(new DoubleDivide(0.0, 0.0));

		InPort[] inPorts = new InPort[jointCount];
		for (int i = 0; i < jointCount; i++) {

			DoubleConditional conditional = fragment.add(new DoubleConditional());
			inPorts[i] = conditional.getInFalse();
			fragment.connect(nanValue.getOutValue(), conditional.getInTrue());
			DataflowOutPort jointPort = fragment.addOutPort(new JointDataflow(driver.getJoint(i).getDriver()), false,
					conditional.getOutValue());
			fragment.connect(illegal, conditional.getInCondition());

			DeviceParameterBag dParameters = parameters;
			if (jointParameters != null) {
				JointParameters jointParameter = jointParameters.getJointParameters(i);
				if (jointParameter != null) {
					dParameters = parameters.withParameters(jointParameter);
				}
			}

			jointResult[i] = runtime.getMapperRegistry().mapActuatorDriver(runtime, driver.getJoint(i).getDriver(),
					new JointPositionActionResult(jointPort), dParameters, ports.cancel, ports.override);
			fragment.add(jointResult[i].getNetFragment());
		}

		fragment.connect(actionResult.getOutPort(),
				fragment.addInPort(new JointsDataflow(jointCount, driver), true, inPorts));

		fragment.addLinkBuilder(new SoftRobotMultijointLinkBuilder(runtime, driver, this));

		return new AbstractMultiJointDeviceDriverMapperResult<MultiJointDeviceDriver>(driver, fragment, parameters,
				runtime, jointResult) {
		};
	}
}
