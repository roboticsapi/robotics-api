/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import java.util.List;

import org.roboticsapi.core.Actuator;
import org.roboticsapi.core.ActuatorDriver;
import org.roboticsapi.core.actuator.ActuatorDriverRealtimeException;
import org.roboticsapi.core.state.ActuatorState;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;

public class WrappedActuatorDriverMapperResult extends BaseActuatorDriverMapperResult {
	private final ActuatorDriverMapperResult wrappedActionMapperResult;

	public WrappedActuatorDriverMapperResult(ActuatorDriver actuatorDriver, NetFragment fragment,
			final ActuatorDriverMapperResult wrappedActionMapperResult) {
		super(actuatorDriver, fragment, new AbstractStatePortFactory<Actuator.CompletedState>() {

			@Override
			public List<DataflowOutPort> createTypedStatePort(Actuator.CompletedState state) throws MappingException {
				return wrappedActionMapperResult.getStatePort(state);
			}
		});
		this.wrappedActionMapperResult = wrappedActionMapperResult;
	}

	@Override
	public List<DataflowOutPort> getStatePort(ActuatorState state) throws MappingException {
		return wrappedActionMapperResult.getStatePort(state);
	}

	@Override
	public List<DataflowOutPort> getExceptionPort(ActuatorDriverRealtimeException exception) throws MappingException {
		return wrappedActionMapperResult.getExceptionPort(exception);
	}

}
