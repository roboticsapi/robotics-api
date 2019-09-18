/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.io.runtime.rpi.extension;

import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.facet.runtime.rpi.extension.RpiExtension;
import org.roboticsapi.facet.runtime.rpi.mapping.MapperRegistry;
import org.roboticsapi.framework.io.action.MirrorAnalogValue;
import org.roboticsapi.framework.io.action.MirrorDigitalValue;
import org.roboticsapi.framework.io.action.SetAnalogValue;
import org.roboticsapi.framework.io.action.SetDigitalValue;
import org.roboticsapi.framework.io.activity.AnalogInputSensorInterface;
import org.roboticsapi.framework.io.activity.AnalogOutputSensorInterface;
import org.roboticsapi.framework.io.activity.DigitalInputSensorInterface;
import org.roboticsapi.framework.io.activity.DigitalOutputSensorInterface;
import org.roboticsapi.framework.io.runtime.rpi.AnalogInputGenericDriver;
import org.roboticsapi.framework.io.runtime.rpi.AnalogOutputGenericDriver;
import org.roboticsapi.framework.io.runtime.rpi.DigitalInputGenericDriver;
import org.roboticsapi.framework.io.runtime.rpi.DigitalOutputGenericDriver;
import org.roboticsapi.framework.io.runtime.rpi.FieldbusCouplerGenericDriver;
import org.roboticsapi.framework.io.runtime.rpi.activity.AnalogInputSensorInterfaceImpl;
import org.roboticsapi.framework.io.runtime.rpi.activity.AnalogOutputSensorInterfaceImpl;
import org.roboticsapi.framework.io.runtime.rpi.activity.DigitalInputSensorInterfaceImpl;
import org.roboticsapi.framework.io.runtime.rpi.activity.DigitalOutputSensorInterfaceImpl;
import org.roboticsapi.framework.io.runtime.rpi.mapper.AnalogInputMapper;
import org.roboticsapi.framework.io.runtime.rpi.mapper.AnalogOutputAnalogValueMapper;
import org.roboticsapi.framework.io.runtime.rpi.mapper.AnalogValueActionResult;
import org.roboticsapi.framework.io.runtime.rpi.mapper.DigitalInputMapper;
import org.roboticsapi.framework.io.runtime.rpi.mapper.DigitalOutputDigitalValueMapper;
import org.roboticsapi.framework.io.runtime.rpi.mapper.DigitalValueActionResult;
import org.roboticsapi.framework.io.runtime.rpi.mapper.MirrorAnalogValueMapper;
import org.roboticsapi.framework.io.runtime.rpi.mapper.MirrorDigitalValueMapper;
import org.roboticsapi.framework.io.runtime.rpi.mapper.SetAnalogValueMapper;
import org.roboticsapi.framework.io.runtime.rpi.mapper.SetDigitalValueMapper;

public class IORuntimeExtension extends RpiExtension {

	public IORuntimeExtension() {
		super(FieldbusCouplerGenericDriver.class, AnalogInputGenericDriver.class, DigitalInputGenericDriver.class,
				AnalogOutputGenericDriver.class, DigitalOutputGenericDriver.class);
	}

	@Override
	public void onAvailable(RoboticsObject object) {
		super.onAvailable(object);
		if (object instanceof AnalogInputGenericDriver) {
			((AnalogInputGenericDriver) object).getDevice().addInterfaceFactory(AnalogInputSensorInterface.class,
					() -> new AnalogInputSensorInterfaceImpl((AnalogInputGenericDriver) object));
		}
		if (object instanceof AnalogOutputGenericDriver) {
			((AnalogOutputGenericDriver) object).getDevice().addInterfaceFactory(AnalogOutputSensorInterface.class,
					() -> new AnalogOutputSensorInterfaceImpl((AnalogOutputGenericDriver) object));
		}
		if (object instanceof DigitalInputGenericDriver) {
			((DigitalInputGenericDriver) object).getDevice().addInterfaceFactory(DigitalInputSensorInterface.class,
					() -> new DigitalInputSensorInterfaceImpl((DigitalInputGenericDriver) object));
		}
		if (object instanceof DigitalOutputGenericDriver) {
			((DigitalOutputGenericDriver) object).getDevice().addInterfaceFactory(DigitalOutputSensorInterface.class,
					() -> new DigitalOutputSensorInterfaceImpl((DigitalOutputGenericDriver) object));
		}
	}

	@Override
	protected void registerMappers(MapperRegistry mr) {

		mr.registerActuatorDriverMapper(DigitalOutputGenericDriver.class, DigitalValueActionResult.class,
				new DigitalOutputDigitalValueMapper());
		mr.registerActuatorDriverMapper(AnalogOutputGenericDriver.class, AnalogValueActionResult.class,
				new AnalogOutputAnalogValueMapper());

		mr.registerRealtimeValueMapper(new DigitalOutputDigitalValueMapper());
		mr.registerRealtimeValueMapper(new DigitalInputMapper());
		mr.registerRealtimeValueMapper(new AnalogInputMapper());

		mr.registerActionMapper(SetDigitalValue.class, new SetDigitalValueMapper());
		mr.registerActionMapper(SetAnalogValue.class, new SetAnalogValueMapper());
		mr.registerActionMapper(MirrorAnalogValue.class, new MirrorAnalogValueMapper());
		mr.registerActionMapper(MirrorDigitalValue.class, new MirrorDigitalValueMapper());
	}

	@Override
	protected void unregisterMappers(MapperRegistry mr) {
		// TODO Auto-generated method stub

	}

}