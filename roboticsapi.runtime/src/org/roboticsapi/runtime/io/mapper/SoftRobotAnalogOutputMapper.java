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
import org.roboticsapi.runtime.io.SoftRobotAnalogOutputDriver;
import org.roboticsapi.runtime.io.primitives.OutReal;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.ActuatorDriverMapper;
import org.roboticsapi.runtime.mapping.parts.DeviceMappingPorts;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.AlwaysActiveStatePortFactory;
import org.roboticsapi.runtime.mapping.result.impl.BaseActuatorDriverMapperResult;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotAnalogOutputMapper
		implements ActuatorDriverMapper<SoftRobotRuntime, SoftRobotAnalogOutputDriver, AnalogValueActionResult> {

	public static class SoftRobotAOMapperResult extends BaseActuatorDriverMapperResult {

		@Override
		public NetFragment getNetFragment() {
			return super.getNetFragment();
		}

		public SoftRobotAOMapperResult(SoftRobotRuntime runtime, final SoftRobotAnalogOutputDriver driver,
				AnalogValueActionResult actionResult, DeviceParameterBag parameters, DataflowOutPort cancel,
				DataflowOutPort override) throws MappingException {
			super(driver, new NetFragment("SoftRobot Analog Output"),
					new AlwaysActiveStatePortFactory<CompletedState>());

			final NetFragment outputNet = new NetFragment("Analog Output");

			final OutReal outputPrimitive = new OutReal();
			outputPrimitive.setDeviceID(driver.getDeviceName());
			outputPrimitive.setPort(driver.getNumber());
			outputNet.add(outputPrimitive);
			outputPrimitive.getInIO().setDebug(2);

			InPort[] inports = new InPort[] { outputPrimitive.getInIO() };

			outputNet.connect(actionResult.getOutPort(), outputNet.addInPort(new DoubleDataflow(), false, inports));

			getNetFragment().add(outputNet);
		}

	}

	@Override
	public ActuatorDriverMapperResult map(SoftRobotRuntime runtime, SoftRobotAnalogOutputDriver device,
			AnalogValueActionResult actionResult, DeviceParameterBag parameters, DeviceMappingPorts ports)
			throws MappingException, RPIException {
		return new SoftRobotAOMapperResult(runtime, device, actionResult, parameters, ports.cancel, ports.override);
	}

}
