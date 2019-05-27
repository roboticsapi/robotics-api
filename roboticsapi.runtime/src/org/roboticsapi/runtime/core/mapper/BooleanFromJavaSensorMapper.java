/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.netcomm.WriteBooleanToNet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.mapping.net.TimestampDataflow;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.impl.BooleanSensorMapperResult;

public class BooleanFromJavaSensorMapper implements SensorMapper<SoftRobotRuntime, Boolean, BooleanFromJavaSensor> {
	private static int nr = 0;

	@Override
	public BooleanSensorMapperResult map(final SoftRobotRuntime runtime, final BooleanFromJavaSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		final NetFragment net = new NetFragment("BooleanFromJavaSensor");
		final WriteBooleanToNet netcomm = new WriteBooleanToNet("BooleanFromJava" + nr++, sensor.getDefaultValue());

		net.add(netcomm);
		DataflowOutPort valuePort = net.addOutPort(new StateDataflow(), true, netcomm.getOutValue());
		try {
			sensor.addListener(new SensorListener<Boolean>() {
				@Override
				public void onValueChanged(Boolean newValue) {
					netcomm.getNetcomm().setString("" + newValue);
				}
			});
		} catch (RoboticsException e) {
			throw new MappingException(e);
		}
		DataflowOutPort timePort = net.addOutPort(new TimestampDataflow(), true, netcomm.getOutLastUpdated());
		return new BooleanSensorMapperResult(net, valuePort, timePort);
	}

}
