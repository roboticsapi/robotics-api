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
import org.roboticsapi.runtime.world.dataflow.VelocityDataflow;
import org.roboticsapi.runtime.world.netcomm.ReadTwistFromNet;
import org.roboticsapi.runtime.world.types.RPITwist;
import org.roboticsapi.world.Twist;

public class VelocitySensorMapperResult extends AtomicSensorMapperResult<Twist> {

	private static int netcommid = 0;

	public VelocitySensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort) {
		super(fragment, sensorPort);
	}

	public VelocitySensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort,
			DataflowOutPort sensorTimePort) {
		super(fragment, sensorPort, sensorTimePort);
	}

	@Override
	protected NetcommPrimitive createNetcommFragment() throws MappingException {
		final NetFragment commFragment = new NetFragment("SensorListener<Twist>");

		final ReadTwistFromNet netcomm = new ReadTwistFromNet("WatchTwist" + ++netcommid);
		commFragment.add(netcomm);

		commFragment.connect(getSensorPort(),
				commFragment.addInPort(new VelocityDataflow(null, null, null, null), true, netcomm.getInValue()));
		netcomm.getInValue().setDebug(2);

		getNetFragment().add(commFragment);
		return netcomm;
	}

	@Override
	protected Twist convertNetcommValue(NetcommValue value) {
		RPITwist twist = new RPITwist(value.getString());
		return new Twist(twist.getVel().getX().get(), twist.getVel().getY().get(), twist.getVel().getZ().get(),
				twist.getRot().getX().get(), twist.getRot().getY().get(), twist.getRot().getZ().get());
	}

	@Override
	protected Sensor<Twist> createInterNetcommSensor(Command command, NetcommValue key) {
		// FIXME add
		return null;
	}

}
