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
import org.roboticsapi.runtime.world.PersistedVectorSensor;
import org.roboticsapi.runtime.world.dataflow.VectorDataflow;
import org.roboticsapi.runtime.world.netcomm.ReadVectorFromNet;
import org.roboticsapi.runtime.world.types.RPIVector;
import org.roboticsapi.world.Vector;

public class VectorSensorMapperResult extends AtomicSensorMapperResult<Vector> {
	private static int netcommid = 0;

	public VectorSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort) {
		super(fragment, sensorPort);
	}

	public VectorSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort, DataflowOutPort sensorTimePort) {
		super(fragment, sensorPort, sensorTimePort);
	}

	@Override
	protected NetcommPrimitive createNetcommFragment() throws MappingException {
		final NetFragment commFragment = new NetFragment("SensorListener<Vector>");

		final ReadVectorFromNet netcomm = new ReadVectorFromNet("WatchVector" + ++netcommid);
		commFragment.add(netcomm);

		commFragment.connect(getSensorPort(), commFragment.addInPort(new VectorDataflow(), true, netcomm.getInValue()));
		netcomm.getInValue().setDebug(2);

		getNetFragment().add(commFragment);
		return netcomm;
	}

	@Override
	protected Vector convertNetcommValue(NetcommValue value) {
		RPIVector vector = new RPIVector(value.getString());
		return new Vector(vector.getX().get(), vector.getY().get(), vector.getZ().get());
	}

	@Override
	protected Sensor<Vector> createInterNetcommSensor(Command command, NetcommValue key) {
		return new PersistedVectorSensor(command, key);
	}

}
