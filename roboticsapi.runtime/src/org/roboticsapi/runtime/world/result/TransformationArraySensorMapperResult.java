/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.result;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.impl.AtomicSensorMapperResult;
import org.roboticsapi.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.world.PersistedTransformationArraySensor;
import org.roboticsapi.runtime.world.dataflow.TransformationArrayDataflow;
import org.roboticsapi.runtime.world.netcomm.ReadFrameArrayFromNet;
import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.runtime.world.util.ConversionUtils;
import org.roboticsapi.world.Transformation;

public class TransformationArraySensorMapperResult extends AtomicSensorMapperResult<Transformation[]> {
	private static int netcommid = 0;
	private int size;

	public TransformationArraySensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort) {
		super(fragment, sensorPort);
	}

	public TransformationArraySensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort,
			DataflowOutPort sensorTimePort) {
		super(fragment, sensorPort, sensorTimePort);
	}

	@Override
	protected NetcommPrimitive createNetcommFragment() throws MappingException {
		final NetFragment commFragment = new NetFragment("SensorListener<Transformation[]>");
		final TransformationArrayDataflow type = (TransformationArrayDataflow) getSensorPort().getType();
		size = type.getSize();
		final ReadFrameArrayFromNet netcomm = new ReadFrameArrayFromNet("WatchFrameArray" + ++netcommid, size);
		commFragment.add(netcomm);
		// netcomm.getInValue().setDebug(2);

		getNetFragment().add(commFragment);
		getNetFragment().connect(getSensorPort(),
				commFragment.addInPort(new TransformationArrayDataflow(size), true, netcomm.getInValue()));
		return netcomm;
	}

	@Override
	protected Transformation[] convertNetcommValue(NetcommValue value) {
		Transformation[] ret = new Transformation[size];
		String line = value.getString();
		if (!line.startsWith("[") || !line.endsWith("]")) {
			return null;
		}

		String line2 = line.substring(2, line.length() - 1);
		String[] data = line2.split(",\\{");
		if (data.length != size) {
			return null;
		}

		for (int i = 0; i < size; i++) {
			RPIFrame frame = new RPIFrame("{" + data[i]);
			ret[i] = ConversionUtils.toTransformation(frame);
		}
		return ret;
	}

	@Override
	protected Sensor<Transformation[]> createInterNetcommSensor(Command command, NetcommValue key) {
		return new PersistedTransformationArraySensor(command, size, key);
	}
}
