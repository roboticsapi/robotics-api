/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.io.extension;

import org.roboticsapi.io.action.MirrorAnalogValue;
import org.roboticsapi.io.action.MirrorDigitalValue;
import org.roboticsapi.io.action.SetAnalogValue;
import org.roboticsapi.io.action.SetDigitalValue;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.extension.AbstractSoftRobotRoboticsBuilder;
import org.roboticsapi.runtime.io.SoftRobotAnalogInputDriver;
import org.roboticsapi.runtime.io.SoftRobotAnalogOutputDriver;
import org.roboticsapi.runtime.io.SoftRobotDigitalInputDriver;
import org.roboticsapi.runtime.io.SoftRobotDigitalOutputDriver;
import org.roboticsapi.runtime.io.SoftRobotFieldbusCouplerDriver;
import org.roboticsapi.runtime.io.mapper.AnalogValueActionResult;
import org.roboticsapi.runtime.io.mapper.DigitalValueActionResult;
import org.roboticsapi.runtime.io.mapper.MirrorAnalogValueMapper;
import org.roboticsapi.runtime.io.mapper.MirrorDigitalValueMapper;
import org.roboticsapi.runtime.io.mapper.SetAnalogValueMapper;
import org.roboticsapi.runtime.io.mapper.SetDigitalValueMapper;
import org.roboticsapi.runtime.io.mapper.SoftRobotAnalogInputMapper;
import org.roboticsapi.runtime.io.mapper.SoftRobotAnalogOutputMapper;
import org.roboticsapi.runtime.io.mapper.SoftRobotDigitalInputMapper;
import org.roboticsapi.runtime.io.mapper.SoftRobotDigitalOutputMapper;
import org.roboticsapi.runtime.mapping.MapperRegistry;

public class SoftRobotIOExtension extends AbstractSoftRobotRoboticsBuilder {

	public SoftRobotIOExtension() {
		super(SoftRobotFieldbusCouplerDriver.class, SoftRobotAnalogInputDriver.class, SoftRobotDigitalInputDriver.class,
				SoftRobotAnalogOutputDriver.class, SoftRobotDigitalOutputDriver.class);
	}

	@Override
	protected String[] getRuntimeExtensions() {
		return new String[] { "io" };
	}

	@Override
	protected void onRuntimeAvailable(SoftRobotRuntime oruntime) {
		MapperRegistry mapperregistry = oruntime.getMapperRegistry();

		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotDigitalOutputDriver.class,
				DigitalValueActionResult.class, new SoftRobotDigitalOutputMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class,
				SoftRobotDigitalOutputDriver.DigitalOutputValue.class, new SoftRobotDigitalOutputMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, SoftRobotDigitalInputDriver.DigitalInputValue.class,
				new SoftRobotDigitalInputMapper());
		mapperregistry.registerActuatorDriverMapper(SoftRobotRuntime.class, SoftRobotAnalogOutputDriver.class,
				AnalogValueActionResult.class, new SoftRobotAnalogOutputMapper());
		mapperregistry.registerSensorMapper(SoftRobotRuntime.class, SoftRobotAnalogInputDriver.AnalogInputValue.class,
				new SoftRobotAnalogInputMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, SetDigitalValue.class, new SetDigitalValueMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, SetAnalogValue.class, new SetAnalogValueMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, MirrorAnalogValue.class,
				new MirrorAnalogValueMapper());
		mapperregistry.registerActionMapper(SoftRobotRuntime.class, MirrorDigitalValue.class,
				new MirrorDigitalValueMapper());
	}

	@Override
	protected void onRuntimeUnavailable(SoftRobotRuntime runtime) {
	}
}