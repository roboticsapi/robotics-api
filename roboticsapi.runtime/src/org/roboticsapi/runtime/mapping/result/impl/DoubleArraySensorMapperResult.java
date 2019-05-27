/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.runtime.core.PersistedDoubleArraySensor;
import org.roboticsapi.runtime.core.netcomm.ReadDoubleArrayFromNet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleArrayDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.runtime.rpi.NetcommValue;

public class DoubleArraySensorMapperResult extends AtomicSensorMapperResult<Double[]> {
	private static int netcommid = 0;
	private int size;

	public DoubleArraySensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort) {
		super(fragment, sensorPort);
	}

	public DoubleArraySensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort,
			DataflowOutPort sensorTimePort) {
		super(fragment, sensorPort, sensorTimePort);
	}

	@Override
	protected NetcommPrimitive createNetcommFragment() throws MappingException {
		final NetFragment commFragment = new NetFragment("SensorListener<Double[]>");
		final DoubleArrayDataflow type = (DoubleArrayDataflow) getSensorPort().getType();
		size = type.getSize();
		final ReadDoubleArrayFromNet netcomm = new ReadDoubleArrayFromNet("WatchDoubleArray" + ++netcommid, size);
		commFragment.add(netcomm);
		// netcomm.getInValue().setDebug(2);

		getNetFragment().add(commFragment);
		getNetFragment().connect(getSensorPort(),
				commFragment.addInPort(new DoubleArrayDataflow(size), true, netcomm.getInValue()));
		return netcomm;
	}

	@Override
	protected Double[] convertNetcommValue(NetcommValue value) {
		return convert(value, size);
	}

	@Override
	protected Sensor<Double[]> createInterNetcommSensor(Command command, NetcommValue key) {
		return new PersistedDoubleArraySensor(command, size, key);
	}

	public static Double[] convert(NetcommValue value, int size) {
		Double[] ret = new Double[size];
		String line = value.getString();
		if (!line.startsWith("[") || !line.endsWith("]")) {
			return null;
		}

		String[] data = line.substring(1, line.length() - 1).split(",");
		if (data.length != size) {
			return null;
		}

		for (int i = 0; i < size; i++) {
			try {
				ret[i] = Double.valueOf(data[i]);
			} catch (NumberFormatException e) {
				ret[i] = Double.NaN;
			}
		}
		return ret;
	}

}
