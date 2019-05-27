/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.io.mapper;

import org.roboticsapi.core.Actuator.CompletedState;
import org.roboticsapi.core.DeviceParameterBag;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.io.SoftRobotDigitalOutputDriver;
import org.roboticsapi.runtime.io.primitives.OutBool;
import org.roboticsapi.runtime.io.primitives.OutBoolSensor;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.AlwaysActiveStatePortFactory;
import org.roboticsapi.runtime.mapping.result.impl.BaseActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.BooleanSensorMapperResult;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotDigitalOutputMapper
		implements ActuatorDriverMapper<SoftRobotRuntime, SoftRobotDigitalOutputDriver, DigitalValueActionResult>,
		SensorMapper<SoftRobotRuntime, Boolean, SoftRobotDigitalOutputDriver.DigitalOutputValue> {

	public static class SoftRobotDOMapperResult extends BaseActuatorDriverMapperResult {

		@Override
		public NetFragment getNetFragment() {
			return super.getNetFragment();
		}

		public SoftRobotDOMapperResult(SoftRobotRuntime runtime, final SoftRobotDigitalOutputDriver driver,
				DigitalValueActionResult actionResult, DeviceParameterBag parameters, DataflowOutPort cancel,
				DataflowOutPort override) throws MappingException {
			super(driver, new NetFragment("SoftRobot Digital Output"),
					new AlwaysActiveStatePortFactory<CompletedState>());

			final NetFragment outputNet = new NetFragment("Digital Output");

			final OutBool outputPrimitive = new OutBool();
			outputPrimitive.setDeviceID(driver.getDeviceName());
			outputPrimitive.setPort(driver.getNumber());
			outputNet.add(outputPrimitive);
			outputPrimitive.getInIO().setDebug(2);

			outputNet.connect(actionResult.getOutPort(),
					outputNet.addInPort(new StateDataflow(), false, outputPrimitive.getInIO()));

			getNetFragment().add(outputNet);
		}

	}

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime, SoftRobotDigitalOutputDriver device,
			DigitalValueActionResult actionResult, DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {
		return new SoftRobotDOMapperResult(runtime, device, actionResult, parameters, ports.cancel, ports.override);
	}

	@Override
	public SensorMapperResult<Boolean> map(final SoftRobotRuntime runtime,
			final SoftRobotDigitalOutputDriver.DigitalOutputValue sensor, SensorMappingPorts ports,
			SensorMappingContext context) throws MappingException {
		final int number = (sensor.getParent()).getNumber();
		final String friName = (sensor.getParent()).getDeviceName();

		NetFragment net = new NetFragment("SoftRobotDigitalOutput");
		final NetFragment inputNet = new NetFragment("Value");
		final OutBoolSensor inputPrimitive = new OutBoolSensor(friName, number);
		inputNet.add(inputPrimitive);
		DataflowOutPort valuePort = inputNet.addOutPort(new StateDataflow(), true, inputPrimitive.getOutIO());
		net.add(inputNet);
		return new BooleanSensorMapperResult(net, valuePort);
	}

}
