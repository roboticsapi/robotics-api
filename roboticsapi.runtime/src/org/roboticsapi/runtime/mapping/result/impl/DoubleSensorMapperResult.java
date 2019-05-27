/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.runtime.core.PersistedDoubleSensor;
import org.roboticsapi.runtime.core.netcomm.ReadDoubleFromNet;
import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.runtime.rpi.NetcommValue;

public class DoubleSensorMapperResult extends AtomicSensorMapperResult<Double> {
	private static int netcommid = 0;

	public DoubleSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort) {
		super(fragment, sensorPort);
	}

	public DoubleSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort, DataflowOutPort sensorTimePort) {
		super(fragment, sensorPort, sensorTimePort);
	}

	@Override
	protected NetcommPrimitive createNetcommFragment() throws MappingException {
		final NetFragment commFragment = new NetFragment("SensorListener<Double>");

		final ReadDoubleFromNet netcomm = new ReadDoubleFromNet("WatchDouble" + ++netcommid);
		commFragment.add(netcomm);

		commFragment.connect(getSensorPort(), commFragment.addInPort(new DoubleDataflow(), true, netcomm.getInValue()));
		netcomm.getInValue().setDebug(2);

		getNetFragment().add(commFragment);
		return netcomm;
	}

	@Override
	protected Double convertNetcommValue(NetcommValue value) {
		return new RPIdouble(value.getString()).get();
	}

	@Override
	protected Sensor<Double> createInterNetcommSensor(Command command, NetcommValue key) {
		return new PersistedDoubleSensor(command, key);
	}

}
