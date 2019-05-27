/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import java.util.List;

import org.roboticsapi.core.SensorState;
import org.roboticsapi.runtime.core.primitives.TimeNet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.TimestampDataflow;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;

public abstract class BaseSensorMapperResult<T> extends BaseMapperResult implements SensorMapperResult<T> {
	private final DataflowOutPort sensorPort;
	private DataflowOutPort sensorTimePort = null;

	public BaseSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort) {
		super(fragment);
		this.sensorPort = sensorPort;
	}

	public BaseSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort, DataflowOutPort sensorTimePort) {
		super(fragment);
		this.sensorPort = sensorPort;
		this.sensorTimePort = sensorTimePort;
	}

	@Override
	public DataflowOutPort getSensorTimePort() throws MappingException {
		if (sensorTimePort == null) {
			sensorTimePort = getNetFragment().addOutPort(new TimestampDataflow(), false,
					getNetFragment().add(new TimeNet()).getOutValue());
		}
		return sensorTimePort;
	}

	@Override
	public DataflowOutPort getSensorPort() throws MappingException {
		return sensorPort;
	}

	@Override
	public List<DataflowOutPort> getStatePort(SensorState event) throws MappingException {
		return super.getStatePort(event);
	}

}
