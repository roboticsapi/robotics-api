/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.core.mapper;

import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleFromJavaSensor;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.netcomm.WriteDoubleToNet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.TimestampDataflow;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;

public class DoubleFromJavaSensorMapper implements SensorMapper<SoftRobotRuntime, Double, DoubleFromJavaSensor> {
	private static int nr = 0;

	@Override
	public DoubleSensorMapperResult map(final SoftRobotRuntime runtime, final DoubleFromJavaSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {
		NetFragment net = new NetFragment("DoubleFromJavaSensor");
		final NetFragment inputNet = new NetFragment("Value");
		net.add(inputNet);
		final WriteDoubleToNet netcomm = new WriteDoubleToNet("DoubleFromJava" + nr++, sensor.getDefaultValue());
		inputNet.add(netcomm);
		DataflowOutPort valuePort = inputNet.addOutPort(new DoubleDataflow(), true, netcomm.getOutValue());
		try {
			sensor.addListener(new SensorListener<Double>() {
				@Override
				public void onValueChanged(Double newValue) {
					netcomm.getNetcomm().setString("" + newValue);
				}
			});
		} catch (RoboticsException e) {
			throw new MappingException(e);
		}
		DataflowOutPort timePort = inputNet.addOutPort(new TimestampDataflow(), true, netcomm.getOutLastUpdated());

		return new DoubleSensorMapperResult(net, valuePort, timePort);
	}

}
