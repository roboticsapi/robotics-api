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
import org.roboticsapi.runtime.world.PersistedTransformationSensor;
import org.roboticsapi.runtime.world.dataflow.TransformationDataflow;
import org.roboticsapi.runtime.world.netcomm.ReadFrameFromNet;
import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.runtime.world.util.ConversionUtils;
import org.roboticsapi.world.Transformation;

public class TransformationSensorMapperResult extends AtomicSensorMapperResult<Transformation> {
	private static int netcommid = 0;

	public TransformationSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort) {
		super(fragment, sensorPort);
	}

	public TransformationSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort,
			DataflowOutPort sensorTimePort) {
		super(fragment, sensorPort, sensorTimePort);
	}

	@Override
	protected NetcommPrimitive createNetcommFragment() throws MappingException {
		final NetFragment commFragment = new NetFragment("SensorListener<Transformation>");

		final ReadFrameFromNet netcomm = new ReadFrameFromNet("WatchTranformation" + ++netcommid);
		commFragment.add(netcomm);

		commFragment.connect(getSensorPort(),
				commFragment.addInPort(new TransformationDataflow(), true, netcomm.getInValue()));
		netcomm.getInValue().setDebug(2);

		getNetFragment().add(commFragment);
		return netcomm;
	}

	@Override
	protected Transformation convertNetcommValue(NetcommValue value) {
		RPIFrame frame = new RPIFrame(value.getString());
		return ConversionUtils.toTransformation(frame);
	}

	@Override
	protected Sensor<Transformation> createInterNetcommSensor(Command command, NetcommValue key) {
		return new PersistedTransformationSensor(command, key);
	}

}
