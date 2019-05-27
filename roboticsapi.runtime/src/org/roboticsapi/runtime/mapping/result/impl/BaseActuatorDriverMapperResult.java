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
import org.roboticsapi.runtime.mapping.parts.TrueFragment;
import org.roboticsapi.runtime.mapping.result.ActuatorDriverMapperResult;

public class BaseActuatorDriverMapperResult extends BaseMapperResult implements ActuatorDriverMapperResult {

	private final ActuatorDriver actuatorDriver;

	public BaseActuatorDriverMapperResult(ActuatorDriver actuatorDriver, NetFragment fragment, boolean completed) {
		this(actuatorDriver, fragment, completed ? fragment.add(new TrueFragment()).getTrueOut() : null);
	}

	public BaseActuatorDriverMapperResult(ActuatorDriver actuatorDriver, NetFragment fragment,
			DataflowOutPort completedPort) {
		super(fragment);
		this.actuatorDriver = actuatorDriver;

		addStatePort(Actuator.CompletedState.class, completedPort);
	}

	public BaseActuatorDriverMapperResult(ActuatorDriver actuatorDriver, NetFragment fragment,
			StatePortFactory completedPortFactory) {
		super(fragment);
		this.actuatorDriver = actuatorDriver;
		addStatePortFactory(Actuator.CompletedState.class, completedPortFactory);
	}

	@Override
	public ActuatorDriver getActuatorDriver() {
		return actuatorDriver;
	}

	@Override
	public List<DataflowOutPort> getExceptionPort(ActuatorDriverRealtimeException exception) throws MappingException {
		return super.getExceptionPort(exception);
	}

	public <T extends ActuatorState> void addActuatorStatePort(Class<T> stateType, DataflowOutPort port) {
		super.addStatePort(stateType, port);

	}

	public void addActuatorStatesPort(List<Class<? extends ActuatorState>> stateTypes, DataflowOutPort port) {
		for (Class<? extends ActuatorState> type : stateTypes) {
			addActuatorStatePort(type, port);
		}

	}

	public <T extends ActuatorState> void addActuatorStatePortFactory(Class<T> stateType, StatePortFactory factory) {
		super.addStatePortFactory(stateType, factory);
	}

	public void addActuatorStatesPortFactory(List<Class<? extends ActuatorState>> stateTypes,
			StatePortFactory factory) {
		for (Class<? extends ActuatorState> type : stateTypes) {
			addActuatorStatePortFactory(type, factory);
		}

	}

	@Override
	public List<DataflowOutPort> getStatePort(ActuatorState state) throws MappingException {
		return super.getStatePort(state);
	}

	public <T extends ActuatorDriverRealtimeException> void addActuatorExceptionPort(Class<T> exceptionType,
			DataflowOutPort port) {
		super.addExceptionPort(exceptionType, port);
	}

	public void addActuatorExceptionsPort(List<Class<? extends ActuatorDriverRealtimeException>> exceptionTypes,
			DataflowOutPort port) {
		for (Class<? extends ActuatorDriverRealtimeException> type : exceptionTypes) {
			addActuatorExceptionPort(type, port);
		}
	}

	public <T extends ActuatorDriverRealtimeException> void addActuatorExceptionPortFactory(Class<T> exceptionType,
			ExceptionPortFactory factory) {
		super.addExceptionPortFactory(exceptionType, factory);
	}

	public void addActuatorExceptionsPortFactory(List<Class<? extends ActuatorDriverRealtimeException>> exceptionTypes,
			ExceptionPortFactory factory) {
		for (Class<? extends ActuatorDriverRealtimeException> type : exceptionTypes) {
			addActuatorExceptionPortFactory(type, factory);
		}

	}
}
