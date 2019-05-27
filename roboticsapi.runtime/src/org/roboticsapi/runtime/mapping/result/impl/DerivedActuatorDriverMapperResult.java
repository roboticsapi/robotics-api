/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import java.util.Arrays;
import java.util.List;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.state.ActuatorState;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;

public class DerivedActuatorDriverMapperResult implements ActuatorDriverMapperResult {
	private final ActuatorDriverMapperResult innerResult;
	private final NetFragment fragment;
	private final DataflowOutPort completed;

	public DerivedActuatorDriverMapperResult(ActuatorDriverMapperResult innerResult, NetFragment fragment,
			DataflowOutPort completed) {
		this.innerResult = innerResult;
		this.fragment = fragment;
		this.completed = completed;
	}

	public DerivedActuatorDriverMapperResult(ActuatorDriverMapperResult innerResult, NetFragment fragment) {
		this(innerResult, fragment, null);
	}

	@Override
	public NetFragment getNetFragment() {
		return fragment;
	}

	@Override
	public List<DataflowOutPort> getStatePort(ActuatorState state) throws MappingException {
		if (completed != null && state instanceof Actuator.CompletedState) {
			return Arrays.asList(completed);
		}

		return innerResult.getStatePort(state);
	}

	@Override
	public List<DataflowOutPort> getExceptionPort(ActuatorDriverRealtimeException exception) throws MappingException {
		return innerResult.getExceptionPort(exception);
	}

	@Override
	public ActuatorDriver getActuatorDriver() {
		return innerResult.getActuatorDriver();
	}
}