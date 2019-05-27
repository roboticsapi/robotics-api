/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.core.State;
import org.roboticsapi.core.runtime.CommandRealtimeException;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.result.MapperResult;

public abstract class BaseMapperResult implements MapperResult {

	private final NetFragment fragment;

	private final Map<Class<? extends CommandRealtimeException>, List<DataflowOutPort>> exceptionPorts = new HashMap<Class<? extends CommandRealtimeException>, List<DataflowOutPort>>();
	private final Map<Class<? extends CommandRealtimeException>, List<ExceptionPortFactory>> exceptionPortFactories = new HashMap<Class<? extends CommandRealtimeException>, List<ExceptionPortFactory>>();
	private final Map<Class<? extends State>, DataflowOutPort> statePorts = new HashMap<Class<? extends State>, DataflowOutPort>();
	private final Map<Class<? extends State>, StatePortFactory> statePortFactories = new HashMap<Class<? extends State>, StatePortFactory>();

	private final HashMap<State, List<DataflowOutPort>> cachedStateResult = new HashMap<State, List<DataflowOutPort>>();

	protected BaseMapperResult(NetFragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public NetFragment getNetFragment() {
		return fragment;
	}

	public <T extends CommandRealtimeException> void addExceptionPort(Class<T> exceptionType, DataflowOutPort port) {
		if (!exceptionPorts.containsKey(exceptionType)) {
			exceptionPorts.put(exceptionType, new ArrayList<DataflowOutPort>());
		}

		exceptionPorts.get(exceptionType).add(port);
	}

	public void addExceptionsPort(List<Class<? extends CommandRealtimeException>> exceptions, DataflowOutPort port) {
		for (Class<? extends CommandRealtimeException> e : exceptions) {
			addExceptionPort(e, port);
		}
	}

	public <T extends CommandRealtimeException> void addExceptionPortFactory(Class<T> exceptionType,
			ExceptionPortFactory factory) {
		if (!exceptionPortFactories.containsKey(exceptionType)) {
			exceptionPortFactories.put(exceptionType, new ArrayList<ExceptionPortFactory>());
		}
		exceptionPortFactories.get(exceptionType).add(factory);
	}

	public void addExceptionsPortFactory(List<Class<? extends CommandRealtimeException>> exceptionTypes,
			ExceptionPortFactory factory) {
		for (Class<? extends CommandRealtimeException> type : exceptionTypes) {
			addExceptionPortFactory(type, factory);
		}
	}

	public List<DataflowOutPort> getExceptionPort(CommandRealtimeException exception) throws MappingException {

		Class<? extends CommandRealtimeException> excClass = exception.getClass();

		List<DataflowOutPort> ports = new ArrayList<DataflowOutPort>();
		if (exceptionPorts.containsKey(excClass)) {
			ports.addAll(exceptionPorts.get(excClass));
		}

		for (Map.Entry<Class<? extends CommandRealtimeException>, List<ExceptionPortFactory>> entry : exceptionPortFactories
				.entrySet()) {
			if (entry.getKey().isAssignableFrom(excClass)) {
				for (ExceptionPortFactory factory : entry.getValue()) {
					List<DataflowOutPort> statePorts = factory.createExceptionPort(exception);
					if (statePorts != null) {
						ports.addAll(statePorts);
					}
				}
			}
		}

		return ports;
	}

	public <T extends State> void addStatePort(Class<T> stateType, DataflowOutPort port) {
		if (statePorts.containsKey(stateType)) {
			throw new IllegalArgumentException("For this State a port has already been registered");
		}

		if (statePortFactories.containsKey(stateType)) {
			throw new IllegalArgumentException("For this State a port factory has already been registered");
		}

		statePorts.put(stateType, port);
	}

	public void addStatesPort(List<Class<? extends State>> stateTypes, DataflowOutPort port) {
		for (Class<? extends State> type : stateTypes) {
			addStatePort(type, port);
		}
	}

	public <T extends State> void addStatePortFactory(Class<T> stateType, StatePortFactory factory) {
		if (statePorts.containsKey(stateType)) {
			throw new IllegalArgumentException("For this State a port has already been registered");
		}

		if (statePortFactories.containsKey(stateType)) {
			throw new IllegalArgumentException("For this State a port factory has already been registered");
		}

		statePortFactories.put(stateType, factory);
		factory.setNetFragment(getNetFragment());
	}

	public void addStatesPortFactory(List<Class<? extends State>> stateTypes, StatePortFactory factory) {

		for (Class<? extends State> type : stateTypes) {
			addStatePortFactory(type, factory);
		}
	}

	public final List<DataflowOutPort> getStatePort(State state) throws MappingException {
		if (!cachedStateResult.containsKey(state)) {
			List<DataflowOutPort> statePort = getStatePortInternal(state);

			cachedStateResult.put(state, statePort);
		}
		return cachedStateResult.get(state);
	}

	protected List<DataflowOutPort> getStatePortInternal(State state) throws MappingException {
		Class<?> targetClass = state.getClass();

		List<DataflowOutPort> ports = new ArrayList<DataflowOutPort>();

		for (Map.Entry<Class<? extends State>, DataflowOutPort> entry : statePorts.entrySet()) {
			if (entry.getKey().isAssignableFrom(targetClass)) {
				DataflowOutPort statePort = entry.getValue();
				if (statePort != null) {
					ports.add(statePort);
				}
			}
		}

		for (Map.Entry<Class<? extends State>, StatePortFactory> entry : statePortFactories.entrySet()) {
			if (entry.getKey().isAssignableFrom(targetClass)) {
				List<DataflowOutPort> statePorts = entry.getValue().createStatePort(state);
				if (statePorts != null) {
					ports.addAll(statePorts);
				}
			}
		}

		return ports;
	}
}
