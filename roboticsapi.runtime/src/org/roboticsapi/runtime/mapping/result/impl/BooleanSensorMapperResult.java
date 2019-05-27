/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorState;
import org.roboticsapi.runtime.core.PersistedBooleanSensor;
import org.roboticsapi.runtime.core.netcomm.ReadBoolFromNet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.StateDataflow;
import org.roboticsapi.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.runtime.rpi.NetcommValue;

public class BooleanSensorMapperResult extends AtomicSensorMapperResult<Boolean> {
	private static int netcommid = 0;

	public BooleanSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort) throws MappingException {
		super(fragment, sensorPort);

		addStatePort(SensorState.class, fragment.reinterpret(sensorPort, new StateDataflow()));
	}

	public BooleanSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort, DataflowOutPort sensorTimePort)
			throws MappingException {
		super(fragment, sensorPort, sensorTimePort);

		addStatePort(SensorState.class, fragment.reinterpret(sensorPort, new StateDataflow()));
	}

	@Override
	protected NetcommPrimitive createNetcommFragment() throws MappingException {
		final NetFragment commFragment = new NetFragment("SensorListener<Boolean>");

		ReadBoolFromNet netcomm = new ReadBoolFromNet("WatchBoolean" + ++netcommid);
		commFragment.add(netcomm);

		commFragment.connect(getSensorPort(), commFragment.addInPort(new StateDataflow(), true, netcomm.getInValue()));
		netcomm.getInValue().setDebug(2);

		getNetFragment().add(commFragment);
		return netcomm;
	}

	@Override
	protected Sensor<Boolean> createInterNetcommSensor(Command command, NetcommValue key) {
		return new PersistedBooleanSensor(command, key);
	}

	@Override
	protected Boolean convertNetcommValue(NetcommValue value) {
		return Boolean.valueOf(value.getString());
	}

}
