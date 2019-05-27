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
import org.roboticsapi.runtime.world.PersistedRotationSensor;
import org.roboticsapi.runtime.world.dataflow.RotationDataflow;
import org.roboticsapi.runtime.world.netcomm.ReadRotationFromNet;
import org.roboticsapi.runtime.world.types.RPIRotation;
import org.roboticsapi.world.Rotation;

public class RotationSensorMapperResult extends AtomicSensorMapperResult<Rotation> {
	private static int netcommid = 0;

	public RotationSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort) {
		super(fragment, sensorPort);
	}

	public RotationSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort,
			DataflowOutPort sensorTimePort) {
		super(fragment, sensorPort, sensorTimePort);
	}

	@Override
	protected NetcommPrimitive createNetcommFragment() throws MappingException {
		final NetFragment commFragment = new NetFragment("SensorListener<Rotation>");

		final ReadRotationFromNet netcomm = new ReadRotationFromNet("WatchRotation" + ++netcommid);
		commFragment.add(netcomm);

		commFragment.connect(getSensorPort(),
				commFragment.addInPort(new RotationDataflow(), true, netcomm.getInValue()));
		netcomm.getInValue().setDebug(2);

		getNetFragment().add(commFragment);
		return netcomm;
	}

	@Override
	protected Rotation convertNetcommValue(NetcommValue value) {
		RPIRotation rotation = new RPIRotation(value.getString());
		return new Rotation(rotation.getA().get(), rotation.getB().get(), rotation.getC().get());
	}

	@Override
	protected Sensor<Rotation> createInterNetcommSensor(Command command, NetcommValue key) {
		return new PersistedRotationSensor(command, key);
	}

}
